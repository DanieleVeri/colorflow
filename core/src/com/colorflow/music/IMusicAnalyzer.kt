package com.colorflow.music

interface IMusicAnalyzer {
    fun analyze_beat(music_id: String)
    fun start_beat_flow(music_id: String)
    fun stop_beat_flow()
    fun add_beat_cb(cb: ()->Unit)
    fun rem_beat_cb(cb: ()->Unit)
}