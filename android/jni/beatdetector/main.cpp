//
// Created by dan on 29/12/18.
//

#include <jni.h>
#include <BeatDetector.h>
#include <aubio.h>
#include <cstddef>
#include <android/log.h>

#define APPNAME "Colorflow"

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_colorflow_music_MusicManager_detect(JNIEnv *env, jobject instance) {
    jintArray result;
    jint fill[800];   // 4min song at 180bpm
    int counter = 0;

    uint_t samplerate = 0;
    uint_t win_size = 1024; // window size
    uint_t hop_size = win_size / 4;
    uint_t n_frames = 0, read = 0;

    char_t *source_path = "/data/data/com.colorflow/files/music/ncs.wav";
    aubio_source_t *source = new_aubio_source(source_path, samplerate, hop_size);
    if (!source) {
        __android_log_print(ANDROID_LOG_ERROR, APPNAME, "error loading music file");
        return NULL;
    }

    if (samplerate == 0) samplerate = aubio_source_get_samplerate(source);
    fvec_t *in = new_fvec(hop_size); // input audio buffer
    fvec_t *out = new_fvec(1); // output position
    aubio_tempo_t *o = new_aubio_tempo("default", win_size, hop_size, samplerate);

    do {
        aubio_source_do(source, in, &read);
        aubio_tempo_do(o, in, out);
        if (out->data[0] != 0) {
            /*
            __android_log_print(ANDROID_LOG_INFO, APPNAME,
                                "beat at %.3fms, %.3fs, frame %d, %.2fbpm with confidence %.2f\n",
                                aubio_tempo_get_last_ms(o), aubio_tempo_get_last_s(o),
                                aubio_tempo_get_last(o), aubio_tempo_get_bpm(o),
                                aubio_tempo_get_confidence(o));
            */
            fill[counter++] = aubio_tempo_get_last_ms(o);
        }
        n_frames += read;
    } while (read == hop_size);

    __android_log_print(ANDROID_LOG_INFO, APPNAME,
                        "read %.2fs, %d frames at %dHz (%d blocks) from %s\n",
                        n_frames * 1. / samplerate,
                        n_frames, samplerate,
                        n_frames / hop_size, source_path);

    del_aubio_tempo(o);
    del_fvec(in);
    del_fvec(out);
    del_aubio_source(source);
    aubio_cleanup();

    result = env->NewIntArray(counter);
    env->SetIntArrayRegion(result, 0, counter, fill);
    return result;
}