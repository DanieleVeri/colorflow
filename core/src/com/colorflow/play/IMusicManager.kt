package com.colorflow.play

interface IMusicManager {
    fun init()
    fun reset()
    fun play()
    fun pause()
    fun stop()

    fun analyze()
    fun add_beat_cb(cb: ()->Unit)
    fun rem_beat_cb(cb: ()->Unit)

}