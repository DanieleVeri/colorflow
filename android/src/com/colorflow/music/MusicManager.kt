package com.colorflow.music

import android.content.Context
import android.media.MediaPlayer
import android.media.audiofx.Equalizer
import android.net.Uri
import android.util.Log

class MusicManager(private val context: Context):
        IMusicManager,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    private var media_player: MediaPlayer

    init {
        media_player = MediaPlayer()
        media_player.setOnCompletionListener(this)
        media_player.setOnErrorListener(this)
        val equalizer = Equalizer(0, media_player.audioSessionId)
        equalizer.enabled = true
        reset()
    }

    override fun reset() {
        media_player.reset()
    }

    override fun load(music_id: String) {
        media_player.setDataSource(context, Uri.fromFile(get_music_file(context, music_id)))
        media_player.prepare()
    }

    override fun play() {
        media_player.start()
    }

    override fun pause() {
        media_player.pause()
    }

    override fun stop() {
        media_player.stop()
    }

    override fun onCompletion(mp: MediaPlayer) {
        mp.reset()
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        Log.e("Error MediaPlayer",
                "what: " + what.toString() + " extra: " + extra.toString())
        return false
    }

    fun release() {
        stop()
        media_player.release()
    }

}