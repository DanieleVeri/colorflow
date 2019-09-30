package com.colorflow.music

import android.content.Context

class MusicAnalyzer(private val context: Context): IMusicAnalyzer {

    override fun analyze_beat(track_id: String): Array<BeatSample> {
        return detectBeat(get_music_file(context, track_id).path)
    }

    companion object {
        init {
            System.loadLibrary("musalyzer")
        }
    }

    // NOTE: native functions must be camelCase
    external fun detectBeat(path: String): Array<BeatSample>
}