package com.colorflow.music

interface IEventListener {
    suspend fun on_beat(sample: BeatSample)
}