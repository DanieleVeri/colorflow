package com.colorflow.music

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Disposable
import kotlinx.coroutines.*
import java.util.HashMap
import java.util.HashSet

class Music (protected val analyzer: IMusicAnalyzer,
             protected val manager: IMusicManager): Disposable {

    private lateinit var current_track: String
    private var start_pause: Long = 0
    private var start_time: Long = 0
    private var paused: Long = 0

    private var listeners: MutableSet<IEventListener> = HashSet()

    private val beat_map: HashMap<String, Array<BeatSample>> = HashMap()
    private lateinit var beat_timer: Job

    init {
        manager.on_completition_cb = {
            listeners.forEach {
                it.on_completition()
            }
        }
        manager.on_error_cb = {/**/}
    }

    fun add_listener(listener: IEventListener) {
        listeners.add(listener)
    }

    fun rem_listener(listener: IEventListener) {
        listeners.add(listener)
    }

    fun analyze(track_id: String) {
        analyze_beat(track_id)
    }

    fun prepare(track_id: String) {
        paused = 0
        start_pause = 0
        start_time = System.currentTimeMillis()
        current_track = track_id
        manager.reset()
        manager.load(track_id)
    }

    fun play() {
        if (start_pause > 0L)
            paused += (System.currentTimeMillis() - start_pause)
        start_pause = 0L
        manager.play()
        analyzer_play()
    }

    fun pause() {
        manager.pause()
        start_pause = System.currentTimeMillis()
        beat_timer.cancel()
    }

    fun stop() {
        manager.stop()
        start_pause = System.currentTimeMillis()
        beat_timer.cancel()
    }

    protected fun analyzer_play() {
        beat_timer = GlobalScope.launch {
            if (start_pause > 0L)
                return@launch
            val current = System.currentTimeMillis() - start_time - paused
            val sample = beat_map[current_track]!!.find { it.ms.toLong() >=  current} ?: return@launch
            Gdx.app.debug(this@Music::class.java.simpleName, sample.confidence.toString())
            delay(sample.ms.toLong() - current)
            coroutineScope {
                listeners.forEach {
                    launch { it.on_beat(sample) }
                }
            }
            analyzer_play()
        }
    }

    protected fun analyze_beat(track_id: String) {
        if(beat_map[track_id] != null)
            return
        beat_map[track_id] = analyzer.analyze_beat(track_id)
    }

    override fun dispose() {
        manager.release()
    }
}