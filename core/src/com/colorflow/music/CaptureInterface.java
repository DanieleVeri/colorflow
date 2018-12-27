package com.colorflow.music;

public interface CaptureInterface {

    Type getType();

    byte[] getSamples();

    int getSamplingRate();

    int getCaptureSize();

    int getCaptureRate();

    double getAccurateCaptureRate();

    enum Type {
        PCM, FFT
    }
}
