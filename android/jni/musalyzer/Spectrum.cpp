#include "Spectrum.h"

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_colorflow_music_MusicAnalyzer_fft(JNIEnv *env, jobject instance, jstring path) {
    uint_t samplerate = 0;
    const uint_t win_size = 4096;
    const uint_t hop_size = win_size;
    uint_t read = 0;

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

    jobjectArray jFloatArrayArray = env->NewObjectArray(10000, env->FindClass("[F"), nullptr);

    int j = 0;
    do {
        // sampling
        aubio_source_do(source, in, &read);
        aubio_fft_do(fft, in, out);
        float max = 0;
        jfloatArray samples = env->NewFloatArray(win_size / 2);
        for (uint_t i = 0; i < win_size / 4 && read > 0; i++) {
            jfloat buf = cvec_norm_get_sample(out, i);
            if (max < buf)
                max = buf;
            env->SetFloatArrayRegion(samples, i, 1, &buf);
        }
        // normalization
        if (max > 1e-6) {
            for (uint_t i = 0; i < win_size / 4; i++) {
                jfloat buf;
                env->GetFloatArrayRegion(samples, i, 1, &buf);
                buf /= max;
                env->SetFloatArrayRegion(samples, i, 1, &buf);
            }
        }
        env->SetObjectArrayElement(jFloatArrayArray, j++, samples);
        env->DeleteLocalRef(samples);
    }  while (read == win_size);

    del_aubio_fft(fft);
    del_fvec(in);
    del_cvec(out);
    del_aubio_source(source);
    aubio_cleanup();
    env->ReleaseStringUTFChars(path, source_path);

    return jFloatArrayArray;
}
