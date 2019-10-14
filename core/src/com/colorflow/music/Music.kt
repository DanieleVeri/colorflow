package com.colorflow.music

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import com.badlogic.gdx.utils.Disposable
import com.colorflow.graphic.laction
import kotlinx.coroutines.*
import java.util.HashMap
import java.util.HashSet

class Music (protected val analyzer: IMusicAnalyzer,
             protected val manager: IMusicManager): Actor(), Disposable {

    private lateinit var current_track: String
    private var played_time: Float? = null
    private var listeners: MutableSet<IEventListener> = HashSet()
    private val beat_map: HashMap<String, Array<BeatSample>> = HashMap()

    init {
        manager.on_completition_cb = {
            listeners.forEach {
                it.on_completition()
            }
        }
        manager.on_error_cb = {
            // TODO
        }
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
        played_time = null
        current_track = track_id
        manager.reset()
        manager.load(track_id)
    }

    fun play() {
        if(played_time == null)
            played_time = 0f
        manager.play()
    }

    fun pause() {
        manager.pause()
    }

    fun stop() {
        manager.stop()
        clearActions()
        played_time = null
    }

    override fun act(delta: Float) {
        if(played_time != null) {
            played_time = played_time!! + delta
            val sample = beat_map[current_track]!!.find {
                it.ms/1000f >=  played_time!!-delta && it.ms/1000f < played_time!!}
            if(sample != null) {
                listeners.forEach {
                    it.on_beat(this, sample)
                }
            }
        }
        super.act(delta)
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