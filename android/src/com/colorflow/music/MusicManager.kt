package com.colorflow.music

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.Equalizer
import android.net.Uri
import android.util.Log
import com.colorflow.music.MusicManager.State.*

class MusicManager(private val context: Context):
        IMusicManager,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    private enum class State {
        IDLE, INIT, PREPARING, PREPARED, STARTED, PAUSED, STOPPED, COMPLETED, ERROR, RELEASED;

        fun `is`(vararg states: State): Boolean {
            for (state in states)
                if (this == state)
                    return true
            return false
        }
    }

    private var _media_player: MediaPlayer
    private var _state: State

    init {
        _media_player = MediaPlayer()
        _media_player.setAudioStreamType(AudioManager.STREAM_MUSIC)
        _media_player.setOnCompletionListener(this)
        _media_player.setOnErrorListener(this)
        _state = IDLE
        val equalizer = Equalizer(0, _media_player.audioSessionId)
        equalizer.enabled = true
        reset()
    }

    override fun reset() {
        if (_state.`is`(PREPARED))
            return
        _media_player.reset()
        _state = IDLE
    }

    override fun load(music_id: String) {
        if (!_state.`is`(IDLE))
            return
        _media_player.setDataSource(context, Uri.fromFile(get_music_file(context, music_id)))
        _state = INIT
        _state = PREPARING
        _media_player.prepare()
        _state = PREPARED
    }

    override fun play() {
        if (!_state.`is`(PAUSED, PREPARED))
            return
        _media_player.start()
        _state = STARTED
    }

    override fun pause() {
        if (!_state.`is`(STARTED, COMPLETED))
            return
        _media_player.pause()
        _state = PAUSED
    }

    override fun stop() {
        if (!_state.`is`(PREPARED, STARTED, STOPPED, PAUSED, COMPLETED))
            return
        _media_player.stop()
        _state = STOPPED
    }

    override fun onCompletion(mp: MediaPlayer) {
        _state = COMPLETED
        mp.reset()
        _state = IDLE
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        Log.e("Error MediaPlayer",
                "what: " + what.toString() + " extra: " + extra.toString())
        _state = ERROR
        return false
    }

    fun release() {
        stop()
        _media_player.release()
        _state = RELEASED
    }

}