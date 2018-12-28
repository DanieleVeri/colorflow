package com.colorflow.music

import java.util.Observer

interface MusicManagerInterface {
    fun init()
    fun reset()
    fun play()
    fun pause()
    fun stop()
    fun addObserver(o: Observer)
}
