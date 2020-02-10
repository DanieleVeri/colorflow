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
    private var worker: SendChannel<WorkerMsg> = CoroutineScope(Dispatchers.Default).worker()

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
        var fft_map: Array<FloatArray>? = null
        var beat_map: HashMap<String, Array<BeatSample>> = HashMap()

        for (msg in channel) {
            when (msg) {
                is Analyze -> {
                    val scope = CoroutineScope(Dispatchers.Default)
                    val jobs = listOf(
                    scope.async {
                        if (beat_map[current_track] == null)
                            beat_map[current_track] = analyzer.analyze_beat(current_track)
                    },
                    scope.async {
                        fft_map = analyzer.fft_slice(current_track)
                    })
                    jobs.awaitAll()
                    msg.response.complete(Unit)
                }

                is GetFFT -> {
                    val slice = fft_map!![floor(played_time!!*44100.0/4096.0).toInt()]
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
        worker_do<Unit>{ Analyze(it) }
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
            val sample = worker_do<BeatSample?> { GetBeat(delta, it) }
            if(sample != null) {
                listeners.forEach {
                    it.on_beat(this, sample)
                }
            }
            // spectrum
            val fft_slice = worker_do<FloatArray> { GetFFT(it) }
            listeners.forEach { it.on_fft(this, fft_slice) }
        }
        super.act(delta)
    }

    override fun dispose() {
        manager.release()
        worker.close()
    }
}