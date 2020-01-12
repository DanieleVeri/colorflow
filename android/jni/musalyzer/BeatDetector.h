#ifndef COLORFLOW_BEATDETECTOR_H
#define COLORFLOW_BEATDETECTOR_H

extern "C" {

#include <jni.h>
#include <aubio.h>
#include <cstddef>
#include <android/log.h>

#define LIBNAME "libmusalyzer"

typedef struct _JNI_BeatSample {
    jclass cls;
    jmethodID ctorID;
    jfieldID msID;
    jfieldID confidenceID;
    jfieldID bpmID;
} JNI_BeatSample;

typedef struct _BeatSample {
    float confidence;
    float ms;
    float bpm;
} BeatSample;

JNIEXPORT jobjectArray JNICALL
Java_com_colorflow_music_MusicAnalyzer_detectBeat(JNIEnv *env, jobject instance, jstring path);

}

#endif //COLORFLOW_BEATDETECTOR_H
