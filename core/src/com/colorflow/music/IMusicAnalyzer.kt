package com.colorflow.music

interface IMusicAnalyzer {
    fun analyze_beat(track_id: String): Array<BeatSample>
    fun fft_slice(track_id: String): Array<FloatArray>
}