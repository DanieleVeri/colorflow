package com.colorflow.music;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Observable;

public class MusicManager extends Observable implements MusicManagerInterface, Visualizer.OnDataCaptureListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private Context context;
    private Visualizer visualizer;
    private MediaPlayer mediaPlayer;
    private State statePlayer;
    private boolean pendingStart;
    private int current;
    private Capture pcm, fft;

    public MusicManager(Context context) {
        this.context = context;
    }

    public void init() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        statePlayer = State.IDLE;
        //TODO: audio should be 48000 Hz (detect sampling rate device)
        //AudioManager myAudioMgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //String nativeSampleRate = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
        //TODO: Visualizer affected by system volume
        Equalizer mEqualizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
        visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);
        visualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        int captureRate = Visualizer.getMaxCaptureRate();
        visualizer.setDataCaptureListener(this, captureRate, false, true);
        pcm = new Capture(CaptureInterface.Type.PCM, visualizer.getCaptureSize(), captureRate);
        fft = new Capture(CaptureInterface.Type.FFT, visualizer.getCaptureSize(), captureRate);
        reset();
    }

    public void reset() {
        if (statePlayer.is(State.PREPARED)) {
            return;
        }
        pendingStart = false;
        current = 0;
        mediaPlayer.reset();
        statePlayer = State.IDLE;
        loadAndPrepare(mediaPlayer);
        disableVisualizer();
    }

    public void play() {
        if (statePlayer.is(State.PAUSED, State.PREPARED)) {
            mediaPlayer.start();
            statePlayer = State.STARTED;
            enableVisualizer();
        } else if (statePlayer.is(State.PREPARING)) {
            pendingStart = true;
        }
    }

    public void pause() {
        if (statePlayer.is(State.STARTED, State.COMPLETED)) {
            mediaPlayer.pause();
            statePlayer = State.PAUSED;
        }
        disableVisualizer();
    }

    public void stop() {
        if (statePlayer.is(State.PREPARED, State.STARTED, State.STOPPED, State.PAUSED, State.COMPLETED)) {
            mediaPlayer.stop();
            statePlayer = State.STOPPED;
        }
        disableVisualizer();
    }

    public void release() {
        stop();
        mediaPlayer.release();
        statePlayer = State.RELEASED;
        visualizer.release();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        disableVisualizer();
        statePlayer = State.COMPLETED;
        mp.reset();
        statePlayer = State.IDLE;
        loadAndPrepare(mp);
        pendingStart = true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        statePlayer = State.PREPARED;
        if (pendingStart) {
            pendingStart = false;
            mp.start();
            statePlayer = State.STARTED;
            enableVisualizer();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("Error MediaPlayer",
                "what: " + String.valueOf(what) + " extra: " + String.valueOf(extra));
        statePlayer = State.ERROR;
        return false;
    }

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
        this.pcm.update(waveform, samplingRate);
        setChanged();
        notifyObservers(this.pcm);
    }

    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
        this.fft.update(fft, samplingRate);
        setChanged();
        notifyObservers(this.fft);
    }

    private void loadAndPrepare(MediaPlayer mp) {
        if (!statePlayer.is(State.IDLE)) {
            throw new IllegalStateException("setDataSource() in a not IDLE state.");
        }
        try {
            mp.setDataSource(context, Uri.fromFile(getCurrentFile()));
            statePlayer = State.INIT;
        } catch (IOException e) {
            e.printStackTrace();
            //TODO: End music (???)
            return;
        }
        if (!statePlayer.is(State.INIT, State.STOPPED)) {
            throw new IllegalStateException("prepareAsync() in a not INIT/STOPPED state.");
        }
        mp.prepareAsync();
        statePlayer = State.PREPARING;
        current++;
    }

    private File getCurrentFile() {
        return new File(context.getApplicationInfo().dataDir +
                "/files/music/" + String.valueOf(current) + ".mp3");
    }

    private void disableVisualizer() {
        visualizer.setEnabled(false);
        fft.pause();
    }

    private void enableVisualizer() {
        visualizer.setEnabled(true);
        fft.resume();
    }

    private enum State {
        IDLE, INIT, PREPARING, PREPARED, STARTED, PAUSED, STOPPED, COMPLETED, ERROR, RELEASED;

        public boolean is(State... states) {
            for (State state : states) {
                if (this == state) {
                    return true;
                }
            }
            return false;
        }
    }

}