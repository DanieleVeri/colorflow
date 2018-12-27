package com.colorflow.music;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.colorflow.utility.Position;

import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

public class BeatDetector implements Observer, IBeatDetector {

    /* TUNING */
    public static final double BUFFER_SIZE_SEC = 3.0;
    private static double ENERGY_THRESHOLD = 100.0;
    private static double PEAK_THRESHOLD = 2.0;
    private static int BAND_SIZE = 2;
    private static int BPM_HISTORY_SIZE = 5;
    private static int PLAUSIBLE_SIZE = 3;
    private static double VAR_STABILITY_THRESHOLD = 5.0;

    private MusicManagerInterface musicManager;
    private boolean isInitialized = false;
    private double captureRate;
    private CircularBuffer derivative, periodicity;
    private double[] lastSample;
    private double[] bpmMap;
    private double lastBpm = 0;
    private int[] bpmHistory;
    private int indexBpmHistory = 0;
    private ShapeRenderer renderer;

    public BeatDetector(MusicManagerInterface musicManager) {
        this.musicManager = musicManager;
        this.musicManager.addObserver(this);
        this.renderer = new ShapeRenderer();
    }

    @Override
    public void update(Observable o, Object arg) {
        CaptureInterface capture = (CaptureInterface) arg;
        switch (capture.getType()) {
            case PCM:
                break;
            case FFT:
                init(capture);
                collect(capture);
                analyze();
                break;
            default:
                throw new IllegalStateException();
        }
    }

    private void init(CaptureInterface capture) {
        if (isInitialized) {
            return;
        }
        int bands = capture.getCaptureSize() / 2 / BAND_SIZE,
                buff = (int) (capture.getCaptureRate() / 1000.0 * BUFFER_SIZE_SEC);
        if ((capture.getCaptureSize() - 1) / 2 / BAND_SIZE == bands) {
            bands++;
        }
        if (bpmMap == null) bpmMap = new double[capture.getCaptureRate() / 1000 * 60];
        if (lastSample == null) lastSample = new double[bands];
        if (derivative == null) derivative = new CircularBuffer(buff, bands);
        if (periodicity == null) periodicity = new CircularBuffer(buff, bands);
        if (bpmHistory == null) bpmHistory = new int[BPM_HISTORY_SIZE];
        isInitialized = true;
    }

    private void collect(CaptureInterface capture) {
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

    private void analyze() {
        detectBPM();
    }

    private void detectBPM() {
        for (int band = 0; band < derivative.getNumColumns(); band++) {
            periodicity.getMatrix()[periodicity.getRowIndex()][band] = periodicity(derivative.getColumn(band));
        }
        Arrays.fill(bpmMap, 0);
        for (int band = 0; band < periodicity.getMatrix()[periodicity.getRowIndex()].length; band++) {
            if (Double.isNaN(periodicity.getMatrix()[periodicity.getRowIndex()][band])) {
                continue;
            }
            double stability = 1 / (MathUtils.var(periodicity.getColumn(band)) + 0.001);
            if (Double.isNaN(stability)) {
                stability = 0;
            }
            int index = (int) Math.round(periodicity.getMatrix()[periodicity.getRowIndex()][band]);
            if (index >= 0 && index < bpmMap.length) {
                bpmMap[index] += MathUtils.rootMeanSquare(derivative.getColumn(band)) + stability * 10000;
            } else {
                Gdx.app.debug("Invalid bpm index", String.valueOf(index));
            }
        }
        double[] sortedMap = Arrays.copyOf(bpmMap, bpmMap.length);
        MathUtils.sort(sortedMap, true);
        int[] plausible = new int[PLAUSIBLE_SIZE];
        for (int i = 0; i < PLAUSIBLE_SIZE; i++) {
            plausible[i] = MathUtils.indexOf(bpmMap, sortedMap[i]);
        }
        /*
        if (indexBpmHistory > 0) {
            if (MathUtils.indexOf(plausible, bpmHistory[(indexBpmHistory - 1) % bpmHistory.length]) == -1) {
                bpmHistory[indexBpmHistory % bpmHistory.length] = plausible[0];
            } else {
                bpmHistory[indexBpmHistory % bpmHistory.length] = bpmHistory[(indexBpmHistory - 1) % bpmHistory.length];
            }
        }
        */
        bpmHistory[indexBpmHistory % bpmHistory.length] = plausible[0];
        if (MathUtils.var(bpmHistory) < VAR_STABILITY_THRESHOLD) {
            lastBpm = bpmHistory[0];
        }
        Gdx.app.log("BPM", lastBpm + " plausible:" + Arrays.toString(plausible));
        indexBpmHistory++;
        periodicity.incRowIndex();
    }

    private double periodicity(double[] array) {
        double[] autocorrelation = MathUtils.autocorrelation(array);
        double[] sortedAutCorr = Arrays.copyOf(autocorrelation, autocorrelation.length);
        MathUtils.sort(sortedAutCorr, true);
        int[] peaks = new int[sortedAutCorr.length];
        int indexPeak = 0;
        for (int i = 0; i < sortedAutCorr.length; i++) {
            if (sortedAutCorr[1] / sortedAutCorr[i] > PEAK_THRESHOLD) {
                break;
            }
            peaks[indexPeak] = MathUtils.indexOf(autocorrelation, sortedAutCorr[i]);
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

    public void render() {
        if (lastSample == null) {
            return;
        }
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int band = 0; band < lastSample.length; band++) {
            renderer.setColor(Color.GREEN);
            renderer.rect(band * Position.getWidthScreen() / lastSample.length, Position.getCenter().getY(),
                    Position.getWidthScreen() / lastSample.length - 1, (float) lastSample[band] * 5);
            renderer.setColor(Color.BLUE);
            renderer.rect(band * Position.getWidthScreen() / periodicity.getNumColumns(), Position.getCenter().getY(),
                    Position.getWidthScreen() / periodicity.getNumColumns() - 1, (float) periodicity.getMatrix()[periodicity.getRowIndex()][band] * -1.0f);
        }
        renderer.setColor(Color.WHITE);
        float bpm = 174f;
        renderer.line(0, Position.getCenter().getY(), Position.getWidthScreen(), Position.getCenter().getY());
        renderer.line(Position.getWidthScreen() - 100, Position.getCenter().getY() - bpm * 2.0f / 1.0f, Position.getWidthScreen(), Position.getCenter().getY() - bpm * 2.0f / 1.0f);
        renderer.line(Position.getWidthScreen() - 100, Position.getCenter().getY() - bpm * 3.0f / 2.0f, Position.getWidthScreen(), Position.getCenter().getY() - bpm * 3.0f / 2.0f);
        renderer.line(Position.getWidthScreen() - 100, Position.getCenter().getY() - bpm * 1.0f / 1.0f, Position.getWidthScreen(), Position.getCenter().getY() - bpm * 1.0f / 1.0f);
        renderer.line(Position.getWidthScreen() - 100, Position.getCenter().getY() - bpm * 1.0f / 2.0f, Position.getWidthScreen(), Position.getCenter().getY() - bpm * 1.0f / 2.0f);
        renderer.setColor(Color.RED);
        renderer.line(0, Position.getCenter().getY() - (float) lastBpm, Position.getWidthScreen(), Position.getCenter().getY() - (float) lastBpm);
        renderer.end();
    }

}
