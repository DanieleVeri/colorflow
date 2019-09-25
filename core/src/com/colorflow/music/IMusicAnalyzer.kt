package com.colorflow.music

interface IMusicAnalyzer {
    fun add_listener(listener: IEventListener)
    fun rem_listener(listener: IEventListener)
    fun analyze(track_id: String)
    fun prepare(track_id: String)
    fun play_time()
    fun pause_time()
}