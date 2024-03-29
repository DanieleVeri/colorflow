package com.colorflow.music

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Disposable
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import java.lang.Math.floor
import java.util.HashMap
import java.util.HashSet
import kotlin.coroutines.CoroutineContext

class Music (protected val analyzer: IMusicAnalyzer,
             protected val manager: IMusicManager): Actor(), Disposable {

    private lateinit var current_track: String
    private var played_time: Float? = null
    private var listeners: MutableSet<IEventListener> = HashSet()

    init {
        manager.on_completition_cb = {
            listeners.forEach {
                it.on_completition()
            }
        }
        manager.on_error_cb = {
            Gdx.app.error(this::class.java.simpleName, "Music Manager error")
        }
    }
    private val fft_map: HashMap<String, Array<FloatArray>> = HashMap()
    private val beat_map: HashMap<String, Array<BeatSample>> = HashMap()


    private var worker: SendChannel<WorkerMsg> = CoroutineScope(Dispatchers.Default).worker()

    protected fun <T>worker_do(block: suspend (CompletableDeferred<T>) -> WorkerMsg): T {
        return runBlocking {
            val res = CompletableDeferred<T>()
            withContext(Dispatchers.Default) {
                worker.send(block(res))
            }
            return@runBlocking res.await()
        }
    }

    open class WorkerMsg
    class Analyze(val response: CompletableDeferred<Unit>): WorkerMsg()
    class GetFFT(val response: CompletableDeferred<FloatArray>): WorkerMsg()
    class GetBeat(val delta: Float,
                  val response: CompletableDeferred<BeatSample?>): WorkerMsg()

    protected fun CoroutineScope.worker() = actor<WorkerMsg> {
        for (msg in channel) {
            when (msg) {
                is Analyze -> {
                    val scope = CoroutineScope(Dispatchers.Default)
                    val jobs = listOf(
                    scope.async {
                        if (beat_map[current_track] == null) {
                            beat_map[current_track] = analyzer.analyze_beat(current_track)
                            val values = beat_map[current_track]?.joinToString(){it.confidence.toString()}
                            Gdx.app.debug("Beat detected", values)
                            Gdx.app.debug("Beat number", beat_map[current_track]?.size.toString())
                        }
                    },
                    scope.async {
                        if (fft_map[current_track] == null)
                            fft_map[current_track] = analyzer.fft_slice(current_track)
                    })
                    jobs.awaitAll()
                    msg.response.complete(Unit)
                }

                is GetFFT -> {
                    val slice = fft_map[current_track]!![floor(played_time!!*44100.0/4096.0).toInt()]
                    msg.response.complete(slice)
                }

                is GetBeat -> {
                    val sample = beat_map[current_track]!!.find {
                        it.ms/1000f >=  played_time!!-msg.delta && it.ms/1000f < played_time!!}
                    msg.response.complete(sample)
                }
            }
        }
    }

    fun add_listener(listener: IEventListener) {
        listeners.add(listener)
    }

    fun rem_listener(listener: IEventListener) {
        listeners.add(listener)
    }

    fun prepare(track_id: String) {
        played_time = null
        manager.reset()

        current_track = track_id
        manager.load(current_track)
        worker_do<Unit>{Analyze(it)}
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
            // beat detection
            val sample = beat_map[current_track]!!.find {
                it.ms/1000f >=  played_time!!-delta && it.ms/1000f < played_time!!}
            if(sample != null) {
                Gdx.app.debug("Current beat confidence", sample.confidence.toString())
                listeners.forEach {
                    it.on_beat(this, sample)
                }
            }
            // spectrum
            val slice = fft_map[current_track]!![floor(played_time!!*44100.0/4096.0).toInt()]
            listeners.forEach { it.on_fft(this, slice) }
        }
        super.act(delta)
    }

    override fun dispose() {
        manager.release()
        //worker.close()
    }
}