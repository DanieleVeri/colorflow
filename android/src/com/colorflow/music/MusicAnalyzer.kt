package com.colorflow.music

import android.content.Context
import java.io.File

class MusicAnalyzer(private val context: Context): IMusicAnalyzer {

    override fun analyze_beat(track_id: String): Array<BeatSample> {
        return detectBeat(get_music_file(context, track_id).path)
    }

    override fun fft_slice(track_id: String): Array<FloatArray> {
        return fft(get_music_file(context, track_id).path)
    }

    // NOTE: native functions must be camelCase
    external fun detectBeat(path: String): Array<BeatSample>
    external fun fft(path: String): Array<FloatArray>

    companion object {
        init {
            System.loadLibrary("musalyzer")
        }

        fun get_music_file(context: Context, track_id: String)
                = File(context.applicationInfo.dataDir + "/files/music/" + track_id + ".mp3")
    }
}