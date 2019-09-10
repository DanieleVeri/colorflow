package com.colorflow.music

interface IMusicAnalyzer {
    // ===== Beat
    fun analyze_beat(track_id: String)

    fun prepare(track_id: String)
    fun pause_time()
    fun play_time()

    fun add_beat_cb(cb: suspend ()->Unit)
    fun rem_beat_cb(cb: suspend ()->Unit)
}