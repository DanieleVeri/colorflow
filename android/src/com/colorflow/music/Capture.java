package com.colorflow.music;

import java.util.Calendar;

public class Capture implements CaptureInterface {

    private Type type;
    private byte[] samples;
    private int samplingRate;
    private int captureRate;

    private double accurateCaptureRate;
    private long millis = 0, timer = 0, pausedTimer = 0;

    public Capture(Type type, int captureSize, int captureRate) {
        this.type = type;
        this.samples = new byte[captureSize];
        this.captureRate = captureRate;
        this.accurateCaptureRate = captureRate;
    }

    public void update(byte[] samples, int samplingRate) {
        int buffSize = (int) Math.floor(captureRate / 1000.0 * BeatDetector.BUFFER_SIZE_SEC);
        if (timer % buffSize == 0) {
            accurateCaptureRate = (double) buffSize / ((double) (Calendar.getInstance().getTimeInMillis() - millis) / 1000.0);
            millis = Calendar.getInstance().getTimeInMillis();
        }
        timer++;
        this.samples = samples;
        this.samplingRate = samplingRate;
    }

    @Override
    public Type getType() {
        return type;
    }

    public byte[] getSamples() {
        return samples;
    }

    public int getSamplingRate() {
        return samplingRate;
    }

    public int getCaptureSize() {
        return samples.length;
    }

    public int getCaptureRate() {
        return captureRate;
    }

    public double getAccurateCaptureRate() {
        return accurateCaptureRate;
    }

    public void pause() {
        if (pausedTimer == 0) {
            pausedTimer = Calendar.getInstance().getTimeInMillis();
        }
    }

    public void resume() {
        if (pausedTimer != 0) {
            millis += Calendar.getInstance().getTimeInMillis() - pausedTimer;
            pausedTimer = 0;
        }
    }
}
