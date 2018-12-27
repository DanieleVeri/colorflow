//TODO: Implement in C/C++

package com.colorflow.music;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

public class BeatDetector2 implements Observer, IBeatDetector {

    /* TUNING */
    public static final double BUFFER_SIZE_SEC = 3.0;
    private static double PEAK_THRESHOLD = 2.0;
    private static int BAND_SIZE = 2;

    private MusicManagerInterface musicManager;
    private double captureRate;
    private double[] lastSample;
    private CircularBuffer derivative;

    public BeatDetector2(MusicManagerInterface musicManager) {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        this.musicManager = musicManager;
        this.musicManager.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        CaptureInterface capture = (CaptureInterface) arg;
        switch (capture.getType()) {
            case PCM:
                break;
            case FFT:
                _init(capture);
                _collect(capture);
                _analyze();
                break;
            default:
                throw new IllegalStateException();
        }
    }
    private void _init(CaptureInterface capture) {
        int bands = capture.getCaptureSize() / 2 / BAND_SIZE,
                buff = (int) (capture.getCaptureRate() / 1000.0 * BUFFER_SIZE_SEC);
        if ((capture.getCaptureSize() - 1) / 2 / BAND_SIZE == bands) {
            bands++;
        }
        if (lastSample == null) lastSample = new double[bands];
        if (derivative == null) derivative = new CircularBuffer(buff, bands);
    }

    // save increments from last capture
    private void _collect(CaptureInterface capture) {
        double mod;
        captureRate = capture.getAccurateCaptureRate();
        for (int i = 0; i < capture.getCaptureSize(); i += BAND_SIZE * 2) {
            mod = 0;
            for (int j = 0; j < BAND_SIZE * 2; j += 2) {
                if (i + j == 0) {
                    mod += Math.pow(Math.abs(capture.getSamples()[0]), 2);
                } else {
                    mod += Math.pow(capture.getSamples()[i + j], 2) + Math.pow(capture.getSamples()[i + j + 1], 2);
                }
            }
            mod = Math.sqrt(mod / BAND_SIZE);
            derivative.getMatrix()[derivative.getRowIndex()][i / 2 / BAND_SIZE] = mod - lastSample[i / 2 / BAND_SIZE];
            lastSample[i / 2 / BAND_SIZE] = mod;
        }
        derivative.incRowIndex();
    }

    // Analyze data
    private void _analyze() {
        _detectBPM();
    }

    private void _detectBPM() {
        double[] sumAutoco = new double[derivative.getNumColumns()];
        for (int band = 0; band < derivative.getNumColumns(); band++) {
            double[] autoco = MathUtils.autocorrelation(derivative.getColumn(band));
            for (int i = 0; i < autoco.length; i++) {
                sumAutoco[i] += autoco[i];
            }
        }
        Gdx.app.debug("BPM", Double.toString(_periodicity(sumAutoco)));
    }

    private double _periodicity(double[] array) {
        double[] sorted = Arrays.copyOf(array, array.length);
        MathUtils.sort(sorted, true);
        int[] peaks = new int[sorted.length];
        int indexPeak = 0;
        for (int i = 0; i < sorted.length; i++) {
            if (sorted[1] / sorted[i] > PEAK_THRESHOLD) {
                break;
            }
            peaks[indexPeak] = MathUtils.indexOf(array, sorted[i]);
            indexPeak++;
        }
        peaks = Arrays.copyOf(peaks, indexPeak);
        MathUtils.sort(peaks, false);
        double sum = 0, indexSum = 0;
        for (int i = 1; i < peaks.length; i++) {
            if (peaks[i] - peaks[i - 1] == 1) {
                sum += 1.0;
            } else {
                sum += peaks[i] - peaks[i - 1];
                indexSum++;
            }
        }
        return captureRate / (sum / indexSum) * 60.0;
    }

}