package com.colorflow.music

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.Equalizer
import android.media.audiofx.Visualizer
import android.net.Uri
import android.util.Log

import java.io.File
import java.io.IOException
import java.util.Observable

class MusicManager(private val context: Context) : Observable(), MusicManagerInterface, Visualizer.OnDataCaptureListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private var visualizer: Visualizer? = null
    private var mediaPlayer: MediaPlayer? = null
    private var statePlayer: State? = null
    private var pendingStart: Boolean = false
    private var current: Int = 0
    private var pcm: Capture? = null
    private var fft: Capture? = null

    private val currentFile: File
        get() = File(context.applicationInfo.dataDir +
                "/files/music/" + current.toString() + ".mp3")

    override fun init() {
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer!!.setOnCompletionListener(this)
        mediaPlayer!!.setOnPreparedListener(this)
        mediaPlayer!!.setOnErrorListener(this)
        statePlayer = State.IDLE
        //TODO: audio should be 48000 Hz (detect sampling rate device)
        //AudioManager myAudioMgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //String nativeSampleRate = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
        //TODO: Visualizer affected by system volume
        val mEqualizer = Equalizer(0, mediaPlayer!!.audioSessionId)
        visualizer = Visualizer(mediaPlayer!!.audioSessionId)
        mEqualizer.enabled = true
        visualizer!!.scalingMode = Visualizer.SCALING_MODE_NORMALIZED
        visualizer!!.captureSize = Visualizer.getCaptureSizeRange()[1]
        val captureRate = Visualizer.getMaxCaptureRate()
        visualizer!!.setDataCaptureListener(this, captureRate, false, true)
        pcm = Capture(CaptureInterface.Type.PCM, visualizer!!.captureSize, captureRate)
        fft = Capture(CaptureInterface.Type.FFT, visualizer!!.captureSize, captureRate)
        reset()
    }

    override fun reset() {
        if (statePlayer!!.`is`(State.PREPARED)) {
            return
        }
        pendingStart = false
        current = 0
        mediaPlayer!!.reset()
        statePlayer = State.IDLE
        loadAndPrepare(mediaPlayer!!)
        disableVisualizer()
    }

    override fun play() {
        if (statePlayer!!.`is`(State.PAUSED, State.PREPARED)) {
            mediaPlayer!!.start()
            statePlayer = State.STARTED
            enableVisualizer()
        } else if (statePlayer!!.`is`(State.PREPARING)) {
            pendingStart = true
        }
    }

    override fun pause() {
        if (statePlayer!!.`is`(State.STARTED, State.COMPLETED)) {
            mediaPlayer!!.pause()
            statePlayer = State.PAUSED
        }
        disableVisualizer()
    }

    override fun stop() {
        if (statePlayer!!.`is`(State.PREPARED, State.STARTED, State.STOPPED, State.PAUSED, State.COMPLETED)) {
            mediaPlayer!!.stop()
            statePlayer = State.STOPPED
        }
        disableVisualizer()
    }

    fun release() {
        stop()
        mediaPlayer!!.release()
        statePlayer = State.RELEASED
        visualizer!!.release()
    }

    override fun onCompletion(mp: MediaPlayer) {
        disableVisualizer()
        statePlayer = State.COMPLETED
        mp.reset()
        statePlayer = State.IDLE
        loadAndPrepare(mp)
        pendingStart = true
    }

    override fun onPrepared(mp: MediaPlayer) {
        statePlayer = State.PREPARED
        if (pendingStart) {
            pendingStart = false
            mp.start()
            statePlayer = State.STARTED
            enableVisualizer()
        }
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        Log.e("Error MediaPlayer",
                "what: " + what.toString() + " extra: " + extra.toString())
        statePlayer = State.ERROR
        return false
    }

    override fun onWaveFormDataCapture(visualizer: Visualizer, waveform: ByteArray, samplingRate: Int) {
        this.pcm!!.update(waveform, samplingRate)
        setChanged()
        notifyObservers(this.pcm)
    }

    override fun onFftDataCapture(visualizer: Visualizer, fft: ByteArray, samplingRate: Int) {
        this.fft!!.update(fft, samplingRate)
        setChanged()
        notifyObservers(this.fft)
    }

    private fun loadAndPrepare(mp: MediaPlayer) {
        if (!statePlayer!!.`is`(State.IDLE)) {
            throw IllegalStateException("setDataSource() in a not IDLE state.")
        }
        try {
            mp.setDataSource(context, Uri.fromFile(currentFile))
            statePlayer = State.INIT
        } catch (e: IOException) {
            e.printStackTrace()
            //TODO: End music (???)
            return
        }

        if (!statePlayer!!.`is`(State.INIT, State.STOPPED)) {
            throw IllegalStateException("prepareAsync() in a not INIT/STOPPED state.")
        }
        mp.prepareAsync()
        statePlayer = State.PREPARING
        current++
    }

    private fun disableVisualizer() {
        visualizer!!.enabled = false
        fft!!.pause()
    }

    private fun enableVisualizer() {
        visualizer!!.enabled = true
        fft!!.resume()
    }

    private enum class State {
        IDLE, INIT, PREPARING, PREPARED, STARTED, PAUSED, STOPPED, COMPLETED, ERROR, RELEASED;

        fun `is`(vararg states: State): Boolean {
            for (state in states) {
                if (this == state) {
                    return true
                }
            }
            return false
        }
    }

}