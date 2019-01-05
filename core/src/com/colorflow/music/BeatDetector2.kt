package com.colorflow.music

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.colorflow.utility.Position
import java.util.Arrays
import java.util.Observable
import java.util.Observer

class BeatDetector2(private val musicManager: MusicManagerInterface) : Observer, IBeatDetector {
    private var captureRate: Double = 0.toDouble()
    private var lastSample: DoubleArray? = null
    private var derivative: CircularBuffer? = null
    private val renderer: ShapeRenderer = ShapeRenderer()

    init {
        Gdx.app.logLevel = Application.LOG_DEBUG
        this.musicManager.addObserver(this)
    }

    override fun update(o: Observable, arg: Any) {
        val capture = arg as CaptureInterface
        when (capture.type) {
            CaptureInterface.Type.PCM -> {
            }
            CaptureInterface.Type.FFT -> {
                _init(capture)
                _collect(capture)
                _analyze()
            }
        }
    }

    private fun _init(capture: CaptureInterface) {
        var bands = capture.captureSize / 2 / BAND_SIZE
        val buff = (capture.captureRate / 1000.0 * BUFFER_SIZE_SEC).toInt()
        if ((capture.captureSize - 1) / 2 / BAND_SIZE == bands) {
            bands++
        }
        if (lastSample == null) lastSample = DoubleArray(bands)
        if (derivative == null) derivative = CircularBuffer(buff, bands)
    }

    // save increments from last capture
    private fun _collect(capture: CaptureInterface) {
        var mod: Double
        captureRate = capture.accurateCaptureRate
        var i = 0
        while (i < capture.captureSize) {
            mod = 0.0
            var j = 0
            while (j < BAND_SIZE * 2) {
                mod += if (i + j == 0) {
                    Math.pow(Math.abs(capture.samples[0].toInt()).toDouble(), 2.0)
                } else {
                    Math.pow(capture.samples[i + j].toDouble(), 2.0) + Math.pow(capture.samples[i + j + 1].toDouble(), 2.0)
                }
                j += 2
            }
            mod = Math.sqrt(mod / BAND_SIZE)
            derivative!!.matrix[derivative!!.rowIndex][i / 2 / BAND_SIZE] = mod - lastSample!![i / 2 / BAND_SIZE]
            lastSample!![i / 2 / BAND_SIZE] = mod
            i += BAND_SIZE * 2
        }
        derivative!!.incRowIndex()
    }

    // Analyze data
    private fun _analyze() {
        _detectBPM()
    }

    private fun _detectBPM() {
        val sumAutoco = DoubleArray(derivative!!.numColumns)
        for (band in 0 until derivative!!.numColumns) {
            val autoco = MathUtils.autocorrelation(derivative!!.getColumn(band))
            for (i in autoco.indices) {
                sumAutoco[i] += autoco[i]
            }
        }
        Gdx.app.debug("BPM", java.lang.Double.toString(_periodicity(sumAutoco)))
    }

    private fun _periodicity(array: DoubleArray): Double {
        val sorted = Arrays.copyOf(array, array.size)
        MathUtils.sort(sorted, true)
        var peaks = IntArray(sorted.size)
        var indexPeak = 0
        for (i in sorted.indices) {
            if (sorted[1] / sorted[i] > PEAK_THRESHOLD) {
                break
            }
            peaks[indexPeak] = MathUtils.indexOf(array, sorted[i])
            indexPeak++
        }
        peaks = Arrays.copyOf(peaks, indexPeak)
        MathUtils.sort(peaks, false)
        var sum = 0.0
        var indexSum = 0.0
        for (i in 1 until peaks.size) {
            if (peaks[i] - peaks[i - 1] == 1) {
                sum += 1.0
            } else {
                sum += (peaks[i] - peaks[i - 1]).toDouble()
                indexSum++
            }
        }
        return captureRate / (sum / indexSum) * 60.0
    }

    override fun render() {
        if (lastSample == null) {
            return
        }
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        for (band in lastSample!!.indices) {
            renderer.color = Color.GREEN
            renderer.rect(band * Position.widthScreen / lastSample!!.size, Position.center.y,
                    Position.widthScreen / lastSample!!.size - 1, lastSample!![band].toFloat() * 5)
            renderer.color = Color.BLUE
                    }
        renderer.color = Color.WHITE
        val bpm = 174f
        renderer.line(0f, Position.center.y, Position.widthScreen, Position.center.y)
        renderer.line(Position.widthScreen - 100, Position.center.y - bpm * 2.0f / 1.0f, Position.widthScreen, Position.center.y - bpm * 2.0f / 1.0f)
        renderer.line(Position.widthScreen - 100, Position.center.y - bpm * 3.0f / 2.0f, Position.widthScreen, Position.center.y - bpm * 3.0f / 2.0f)
        renderer.line(Position.widthScreen - 100, Position.center.y - bpm * 1.0f / 1.0f, Position.widthScreen, Position.center.y - bpm * 1.0f / 1.0f)
        renderer.line(Position.widthScreen - 100, Position.center.y - bpm * 1.0f / 2.0f, Position.widthScreen, Position.center.y - bpm * 1.0f / 2.0f)
        renderer.end()
    }

    companion object {

        /* TUNING */
        const val BUFFER_SIZE_SEC = 3.0
        private const val PEAK_THRESHOLD = 2.0
        private const val BAND_SIZE = 2
    }

}