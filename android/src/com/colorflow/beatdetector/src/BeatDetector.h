//
// Created by dan on 29/12/18.
//

#ifndef COLORFLOW_BEATDETECTOR_H
#define COLORFLOW_BEATDETECTOR_H

#include <jni.h>

extern "C"
JNIEXPORT jint JNICALL
Java_com_colorflow_music_BeatDetector_add(JNIEnv* pEnv,
    jobject pThis,
    jint a,
    jint b) {
    return a + b;
}

#endif //COLORFLOW_BEATDETECTOR_H
