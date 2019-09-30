package com.colorflow.music

interface IMusicAnalyzer {
    fun analyze_beat(track_id: String): Array<BeatSample>
}