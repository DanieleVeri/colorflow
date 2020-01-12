package com.colorflow.music

interface IEventListener {
    fun on_beat(music: Music, sample: BeatSample)
    fun on_fft(music: Music, buffer: FloatArray)
    fun on_completition()
}