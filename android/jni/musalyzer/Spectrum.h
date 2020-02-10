#ifndef COLORFLOW_SPECTRUM_H
#define COLORFLOW_SPECTRUM_H

extern "C" {

#include <jni.h>
#include <aubio.h>
#include <cstddef>
#include <android/log.h>

#define LIBNAME "libmusalyzer"

JNIEXPORT jobjectArray JNICALL
Java_com_colorflow_music_MusicAnalyzer_fft(JNIEnv *env, jobject instance, jstring path);

}

#endif //COLORFLOW_SPECTRUM
