package com.colorflow.music

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.colorflow.utility.Position

import java.util.Arrays
import java.util.Observable
import java.util.Observer

class BeatDetector(private val musicManager: MusicManagerInterface) : Observer, IBeatDetector {
    private var isInitialized = false
    private var captureRate: Double = 0.toDouble()
    private var derivative: CircularBuffer? = null
    private var periodicity: CircularBuffer? = null
    private var lastSample: DoubleArray? = null
    private var bpmMap: DoubleArray? = null
    private var lastBpm = 0.0
    private var bpmHistory: IntArray? = null
    private var indexBpmHistory = 0
    private val renderer: ShapeRenderer

    companion object {
        /* TUNING */
        const val BUFFER_SIZE_SEC = 3.0
        private const val ENERGY_THRESHOLD = 100.0
        private const val PEAK_THRESHOLD = 2.0
        private const val BAND_SIZE = 2
        private const val BPM_HISTORY_SIZE = 5
        private const val PLAUSIBLE_SIZE = 3
        private const val VAR_STABILITY_THRESHOLD = 5.0

        init {
            System.loadLibrary("beatdetector-lib")
        }
    }

    init {
        this.musicManager.addObserver(this)
        this.renderer = ShapeRenderer()
    }

    external fun add(a:Int, b:Int): Int

    override fun update(o: Observable, arg: Any) {
        val capture = arg as CaptureInterface
        when (capture.type) {
            CaptureInterface.Type.PCM -> {
            }
            CaptureInterface.Type.FFT -> {
                init(capture)
                collect(capture)
                analyze()
            }
        }
    }

    private fun init(capture: CaptureInterface) {
        if (isInitialized) {
            return
        }
        var bands = capture.captureSize/ 2 / BAND_SIZE
        val buff = (capture.captureRate/ 1000.0 * BUFFER_SIZE_SEC).toInt()
        if ((capture.captureSize - 1) / 2 / BAND_SIZE == bands) {
            bands++
        }
        if (bpmMap == null) bpmMap = DoubleArray(capture.captureRate / 1000 * 60)
        if (lastSample == null) lastSample = DoubleArray(bands)
        if (derivative == null) derivative = CircularBuffer(buff, bands)
        if (periodicity == null) periodicity = CircularBuffer(buff, bands)
        if (bpmHistory == null) bpmHistory = IntArray(BPM_HISTORY_SIZE)
        isInitialized = true
    }

    private fun collect(capture: CaptureInterface) {
        var mod: Double
        captureRate = capture.accurateCaptureRate
        var i = 0
        while (i < capture.captureSize) {
            mod = 0.0
            var j = 0
            while (j < BAND_SIZE * 2) {
                if (i + j == 0) {
                    mod += Math.pow(Math.abs(capture.samples[0].toInt()).toDouble(), 2.0)
                } else {
                    mod += Math.pow(capture.samples[i + j].toDouble(), 2.0) + Math.pow(capture.samples[i + j + 1].toDouble(), 2.0)
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

    private fun analyze() {
        detectBPM()
    }

    private fun detectBPM() {
        for (band in 0 until derivative!!.numColumns) {
            periodicity!!.matrix[periodicity!!.rowIndex][band] = periodicity(derivative!!.getColumn(band))
        }
        Arrays.fill(bpmMap!!, 0.0)
        for (band in 0 until periodicity!!.matrix[periodicity!!.rowIndex].size) {
            if (java.lang.Double.isNaN(periodicity!!.matrix[periodicity!!.rowIndex][band])) {
                continue
            }
            var stability = 1 / (MathUtils.`var`(periodicity!!.getColumn(band)) + 0.001)
            if (java.lang.Double.isNaN(stability)) {
                stability = 0.0
            }
            val index = Math.round(periodicity!!.matrix[periodicity!!.rowIndex][band]).toInt()
            if (index >= 0 && index < bpmMap!!.size) {
                bpmMap!![index] += MathUtils.rootMeanSquare(derivative!!.getColumn(band)) + stability * 10000
            } else {
                Gdx.app.debug("Invalid bpm index", index.toString())
            }
        }
        val sortedMap = Arrays.copyOf(bpmMap!!, bpmMap!!.size)
        MathUtils.sort(sortedMap, true)
        val plausible = IntArray(PLAUSIBLE_SIZE)
        for (i in 0 until PLAUSIBLE_SIZE) {
            plausible[i] = MathUtils.indexOf(bpmMap!!, sortedMap[i])
        }
        /*
        if (indexBpmHistory > 0) {
            if (MathUtils.indexOf(plausible, bpmHistory[(indexBpmHistory - 1) % bpmHistory.length]) == -1) {
                bpmHistory[indexBpmHistory % bpmHistory.length] = plausible[0];
            } else {
                bpmHistory[indexBpmHistory % bpmHistory.length] = bpmHistory[(indexBpmHistory - 1) % bpmHistory.length];
            }
        }
        */
        bpmHistory!![indexBpmHistory % bpmHistory!!.size] = plausible[0]
        if (MathUtils.`var`(bpmHistory!!) < VAR_STABILITY_THRESHOLD) {
            lastBpm = bpmHistory!![0].toDouble()
        }
        Gdx.app.log("BPM", lastBpm.toString() + " plausible:" + Arrays.toString(plausible))
        indexBpmHistory++
        periodicity!!.incRowIndex()
    }

    private fun periodicity(array: DoubleArray): Double {
        val autocorrelation = MathUtils.autocorrelation(array)
        val sortedAutCorr = Arrays.copyOf(autocorrelation, autocorrelation.size)
        MathUtils.sort(sortedAutCorr, true)
        var peaks = IntArray(sortedAutCorr.size)
        var indexPeak = 0
        for (i in sortedAutCorr.indices) {
            if (sortedAutCorr[1] / sortedAutCorr[i] > PEAK_THRESHOLD) {
                break
            }
            peaks[indexPeak] = MathUtils.indexOf(autocorrelation, sortedAutCorr[i])
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

    fun render() {
        if (lastSample == null) {
            return
        }
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        for (band in lastSample!!.indices) {
            renderer.color = Color.GREEN
            renderer.rect(band * Position.widthScreen / lastSample!!.size, Position.center.y,
                    Position.widthScreen / lastSample!!.size - 1, lastSample!![band].toFloat() * 5)
            renderer.color = Color.BLUE
            renderer.rect(band * Position.widthScreen / periodicity!!.numColumns, Position.center.y,
                    Position.widthScreen / periodicity!!.numColumns - 1, periodicity!!.matrix[periodicity!!.rowIndex][band].toFloat() * -1.0f)
        }
        renderer.color = Color.WHITE
        val bpm = 174f
        renderer.line(0f, Position.center.y, Position.widthScreen, Position.center.y)
        renderer.line(Position.widthScreen - 100, Position.center.y - bpm * 2.0f / 1.0f, Position.widthScreen, Position.center.y - bpm * 2.0f / 1.0f)
        renderer.line(Position.widthScreen - 100, Position.center.y - bpm * 3.0f / 2.0f, Position.widthScreen, Position.center.y - bpm * 3.0f / 2.0f)
        renderer.line(Position.widthScreen - 100, Position.center.y - bpm * 1.0f / 1.0f, Position.widthScreen, Position.center.y - bpm * 1.0f / 1.0f)
        renderer.line(Position.widthScreen - 100, Position.center.y - bpm * 1.0f / 2.0f, Position.widthScreen, Position.center.y - bpm * 1.0f / 2.0f)
        renderer.color = Color.RED
        renderer.line(0f, Position.center.y - lastBpm.toFloat(), Position.widthScreen, Position.center.y - lastBpm.toFloat())
        renderer.end()
    }

}
