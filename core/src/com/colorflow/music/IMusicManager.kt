package com.colorflow.music

interface IMusicManager {
    fun reset()
    fun load(music_id: String)
    fun play()
    fun pause()
    fun stop()
}