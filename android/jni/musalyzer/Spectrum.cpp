#include "Spectrum.h"

extern "C"
JNIEXPORT jfloatArray JNICALL
Java_com_colorflow_music_MusicAnalyzer_fft(JNIEnv *env, jobject instance, jstring path, float time) {
    uint_t samplerate = 0;
    const uint_t win_size = 4096;
    const uint_t hop_size = win_size;
    uint_t n_frames = 0, read = 0;

    const char_t *source_path = env->GetStringUTFChars(path, 0);
    aubio_source_t *source = new_aubio_source(source_path, samplerate, hop_size);
    if (!source) {
        __android_log_print(ANDROID_LOG_ERROR, LIBNAME, "error loading music file");
        return nullptr;
    }

    samplerate = aubio_source_get_samplerate(source);
    fvec_t *in = new_fvec(win_size); // input audio buffer
    cvec_t *out = new_cvec(win_size); // output position
    aubio_fft_t *fft = new_aubio_fft(win_size);

    auto frame = static_cast<uint_t>(time * samplerate);
    aubio_source_seek(source, frame);
    aubio_source_do(source, in, &read);
    aubio_fft_do(fft, in, out);
    //__android_log_print(ANDROID_LOG_INFO, LIBNAME, "frame: %d / %d", frame, aubio_source_get_duration(source));

    jfloatArray samples = env->NewFloatArray(win_size);
    for (uint_t i=0; i<win_size && read>0; i++) {
        jfloat buf = cvec_norm_get_sample(out, i);
        env->SetFloatArrayRegion(samples, i, 1, &buf);
    }

    //for(int i = 0; i < win_size; i++)
    //    __android_log_print(ANDROID_LOG_INFO, LIBNAME, "fft sample %d: %.2f", i, out->norm[i]);

    del_aubio_fft(fft);
    del_fvec(in);
    del_cvec(out);
    del_aubio_source(source);
    aubio_cleanup();
    env->ReleaseStringUTFChars(path, source_path);

    return samples;
}
