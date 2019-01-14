package com.colorflow.music

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.Equalizer
import android.net.Uri
import android.util.Log
import com.colorflow.play.IMusicManager

import kotlinx.coroutines.*

import java.io.File
import java.io.IOException
import java.util.*

class MusicManager(private val context: Context) :
        IMusicManager,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    companion object {
        init {
            System.loadLibrary("beatdetector")
        }
    }

    external fun detect(): IntArray
    private var peaks: IntArray? = null

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var statePlayer: State
    private var pendingStart: Boolean = false
    private var current: Int = 0

    private var startTime: Long = 0
    private var pauseTime: Long = 0
    private var pauseElapsed: Long = 0
    val playElapsed: Long
        get() {
            if (startTime == 0L) return 0
            return Calendar.getInstance().timeInMillis - startTime - pauseElapsed
        }

    private val currentFile: File
        get() = File(context.applicationInfo.dataDir +
                "/files/music/" + current.toString() + ".mp3")

    private var beat_cb: List<(()->Unit)> = ArrayList()
    override fun add_beat_cb(cb: ()->Unit) {
        beat_cb+= cb
    }
    override fun rem_beat_cb(cb: ()->Unit) {
        beat_cb-= cb
    }

    init {
        GlobalScope.launch {
            peaks = detect()
            var counter = 0
            while(counter < peaks!!.size) {
                if (playElapsed >= peaks!![counter]) {
                    beat_cb.map { it() }
                    counter++
                }
                delay(100)
            }
        }
    }

    override fun init() {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer.setOnCompletionListener(this)
        mediaPlayer.setOnPreparedListener(this)
        mediaPlayer.setOnErrorListener(this)
        statePlayer = State.IDLE
        val mEqualizer = Equalizer(0, mediaPlayer.audioSessionId)
        mEqualizer.enabled = true
        reset()
    }

    override fun reset() {
        if (statePlayer.`is`(State.PREPARED)) {
            return
        }
        pendingStart = false
        current = 0
        startTime = 0
        pauseTime = 0
        pauseElapsed = 0
        mediaPlayer.reset()
        statePlayer = State.IDLE
        loadAndPrepare(mediaPlayer)
    }

    override fun play() {
        if (statePlayer.`is`(State.PAUSED, State.PREPARED)) {
            mediaPlayer.start()
            if (statePlayer == State.PAUSED)
                pauseElapsed += Calendar.getInstance().timeInMillis - pauseTime
            else
                startTime = Calendar.getInstance().timeInMillis
            statePlayer = State.STARTED

        } else if (statePlayer.`is`(State.PREPARING)) {
            pendingStart = true
        }
    }

    override fun pause() {
        if (statePlayer.`is`(State.STARTED, State.COMPLETED)) {
            mediaPlayer.pause()
            statePlayer = State.PAUSED
            pauseTime = Calendar.getInstance().timeInMillis
        }
    }

    override fun stop() {
        if (statePlayer.`is`(State.PREPARED, State.STARTED, State.STOPPED, State.PAUSED, State.COMPLETED)) {
            mediaPlayer.stop()
            statePlayer = State.STOPPED
        }
    }

    fun release() {
        stop()
        mediaPlayer.release()
        statePlayer = State.RELEASED
    }

    override fun onCompletion(mp: MediaPlayer) {
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
            startTime = Calendar.getInstance().timeInMillis
        }
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        Log.e("Error MediaPlayer",
                "what: " + what.toString() + " extra: " + extra.toString())
        statePlayer = State.ERROR
        return false
    }

    private fun loadAndPrepare(mp: MediaPlayer) {
        if (!statePlayer.`is`(State.IDLE)) {
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

        if (!statePlayer.`is`(State.INIT, State.STOPPED)) {
            throw IllegalStateException("prepareAsync() in a not INIT/STOPPED state.")
        }
        mp.prepareAsync()
        statePlayer = State.PREPARING
        current++
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