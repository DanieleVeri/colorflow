package com.colorflow.music

interface IMusicManager {
    fun reset()
    fun load(music_id: String)
    fun play()
    fun pause()
    fun stop()
    fun release()
    var on_completition_cb: ()->Unit
    var on_error_cb: ()->Unit
}