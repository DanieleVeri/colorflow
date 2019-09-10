package com.colorflow.music

import android.content.Context
import kotlinx.coroutines.*
import java.util.*

class MusicAnalyzer(private val context: Context): IMusicAnalyzer {
    companion object {
        init {
            System.loadLibrary("musalyzer")
        }
    }
    external fun detectBeat(path: String): Array<BeatSample>

    private lateinit var _current_track: String

    private var _start_pause: Long = 0
    private var _start_time: Long = 0
    private var _paused: Long = 0

    override fun prepare(track_id: String) {
        _paused = 0
        _start_pause = 0
        _start_time = System.currentTimeMillis()
        _current_track = track_id
    }

    override fun play_time() {
        if (_start_pause > 0L)
            _paused += (System.currentTimeMillis() - _start_pause)
        _start_pause = 0L
        _play()
    }

    override fun pause_time() {
        _start_pause = System.currentTimeMillis()
        _beat_timer.cancel()
    }

    private fun _play() {
        _beat_timer = GlobalScope.launch {
            val current = System.currentTimeMillis() - _start_time - _paused
            val sample = _beat_map[_current_track]!!.find { it.ms.toLong() >=  current}
            sample ?: return@launch
            delay(sample.ms.toLong() - current)
            _beat_callbacks.forEach { runBlocking { it() } }
            _play()
        }
    }

    private val _beat_map: HashMap<String, Array<BeatSample>> = HashMap()
    private var _beat_callbacks: List<(suspend ()->Unit)> = ArrayList()
    private lateinit var _beat_timer: Job

    override fun analyze_beat(track_id: String) {
        val file = get_music_file(context, track_id)
        _beat_map[track_id] = detectBeat(file.absolutePath)
    }

    override fun add_beat_cb(cb: suspend ()->Unit) {
        _beat_callbacks += cb
    }
    override fun rem_beat_cb(cb: suspend ()->Unit) {
        _beat_callbacks -= cb
    }
}