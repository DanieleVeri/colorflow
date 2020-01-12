#include "BeatDetector.h"

JNI_BeatSample *loadJniBeatSample(JNIEnv *env) {
    JNI_BeatSample *jniBeatSample = new JNI_BeatSample;
    jniBeatSample->cls = env->FindClass("com/colorflow/music/BeatSample");
    jniBeatSample->ctorID = env->GetMethodID(jniBeatSample->cls, "<init>", "(FFF)V");
    jniBeatSample->msID = env->GetFieldID(jniBeatSample->cls, "ms", "F");
    jniBeatSample->confidenceID = env->GetFieldID(jniBeatSample->cls, "confidence", "F");
    jniBeatSample->bpmID = env->GetFieldID(jniBeatSample->cls, "bpm", "F");
    return jniBeatSample;
}

jobject struct2jobject(JNIEnv *env, JNI_BeatSample *jniBeatSample, BeatSample sample) {
    jobject jBeatSample = env->NewObject(jniBeatSample->cls, jniBeatSample->ctorID);
    env->SetFloatField(jBeatSample, jniBeatSample->msID, sample.ms);
    env->SetFloatField(jBeatSample, jniBeatSample->confidenceID, sample.confidence);
    env->SetFloatField(jBeatSample, jniBeatSample->bpmID, sample.bpm);
    return jBeatSample;
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_colorflow_music_MusicAnalyzer_detectBeat(JNIEnv *env, jobject instance, jstring path) {
    JNI_BeatSample *jniBeatSample = loadJniBeatSample(env);
    const int sample_num_max = 800; // ~ 4min song at 180bpm
    BeatSample fill[sample_num_max];

    int counter = 0;
    uint_t samplerate = 0;
    const uint_t win_size = 1024;
    const uint_t hop_size = win_size / 4;
    uint_t n_frames = 0, read = 0;

    const char_t *source_path = env->GetStringUTFChars(path, 0);
    aubio_source_t *source = new_aubio_source(source_path, samplerate, hop_size);
    if (!source) {
        __android_log_print(ANDROID_LOG_ERROR, LIBNAME, "error loading music file");
        return nullptr;
    }

    samplerate = aubio_source_get_samplerate(source);
    fvec_t *in = new_fvec(hop_size); // input audio buffer
    fvec_t *out = new_fvec(1); // output position
    aubio_tempo_t *beat_tracking_obj = new_aubio_tempo("default", win_size, hop_size, samplerate);

    do {
        aubio_source_do(source, in, &read);
        aubio_tempo_do(beat_tracking_obj, in, out);
        if (out->data[0] != 0) {
/*
            __android_log_print(ANDROID_LOG_INFO, LIBNAME,
                "beat at %.3fms, %.3fs, frame %d, %.2fbpm with confidence %.2f\n",
                aubio_tempo_get_last_ms(beat_tracking_obj), aubio_tempo_get_last_s(beat_tracking_obj),
                aubio_tempo_get_last(beat_tracking_obj), aubio_tempo_get_bpm(beat_tracking_obj),
                aubio_tempo_get_confidence(beat_tracking_obj));
*/
            float conf = aubio_tempo_get_confidence(beat_tracking_obj);
            float ms = aubio_tempo_get_last_ms(beat_tracking_obj);
            float bpm = aubio_tempo_get_bpm(beat_tracking_obj);
            if (conf > .01f) {
                fill[counter].ms = ms;
                fill[counter].confidence = conf;
                fill[counter].bpm = bpm;
                counter++;
            }
        }
        n_frames += read;
    } while (read == hop_size);
    __android_log_print(ANDROID_LOG_INFO, LIBNAME,
                        "read %.2fs, %d frames at %dHz (%d blocks) from %s\n",
                        n_frames * 1. / samplerate,
                        n_frames, samplerate,
                        n_frames / hop_size, source_path);

    jobjectArray jBeatSampleArray = env->NewObjectArray(counter, jniBeatSample->cls, nullptr);
    for (int i = 0; i < counter; i++) {
        jobject obj = struct2jobject(env, jniBeatSample, fill[i]);
        env->SetObjectArrayElement(jBeatSampleArray, i, obj);
        env->DeleteLocalRef(obj);
    }

    del_aubio_tempo(beat_tracking_obj);
    del_fvec(in);
    del_fvec(out);
    del_aubio_source(source);
    aubio_cleanup();
    env->ReleaseStringUTFChars(path, source_path);
    delete jniBeatSample;

    return jBeatSampleArray;
}
