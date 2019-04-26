package com.colorflow.music

import android.content.Context
import android.util.Log
import java.lang.Exception
import java.util.*
import kotlin.concurrent.schedule

class MusicAnalyzer(private val context: Context):
        IMusicAnalyzer {

    companion object {
        init {
            System.loadLibrary("beatdetector")
        }
    }

    external fun detectBeat(path: String): Array<BeatSample>

    private val _tempos: HashMap<String, Array<BeatSample>> = HashMap()
    private var _beat_cb_list: List<(()->Unit)> = ArrayList()
    private var _beat_timer = Timer("beat_flow", false)

    override fun add_beat_cb(cb: ()->Unit) {
        _beat_cb_list+= cb
    }
    override fun rem_beat_cb(cb: ()->Unit) {
        _beat_cb_list-= cb
    }

    override fun analyze_beat(music_id: String) {
        val file = get_music_file(context, music_id)
        _tempos[music_id] = detectBeat(file.absolutePath)
    }

    override fun start_beat_flow(music_id: String) {
        _tempos[music_id]!!.map {
            _beat_timer.schedule(it.ms.toLong()) {
                _beat_cb_list.map { cb -> cb() }
            }
        }
    }

    override fun stop_beat_flow() {
        try {
            _beat_timer.cancel()
            _beat_timer.purge()
        } catch (unused: Exception) {}
    }
}