//
// Created by dan on 29/12/18.
//

#ifndef COLORFLOW_BEATDETECTOR_H
#define COLORFLOW_BEATDETECTOR_H

#include <jni.h>
#include <BeatDetector.h>
#include <aubio.h>
#include <cstddef>
#include <android/log.h>

#define APPNAME "Colorflow"

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

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_colorflow_music_MusicAnalyzer_detectBeat(JNIEnv *env, jobject instance, jstring path);

#endif //COLORFLOW_BEATDETECTOR_H
