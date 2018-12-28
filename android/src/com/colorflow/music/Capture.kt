package com.colorflow.music

import java.util.Calendar

class Capture(
        override val type: CaptureInterface.Type,
        override val captureSize: Int,
        override val captureRate: Int
) : CaptureInterface {

    override lateinit var samples: ByteArray
    override var samplingRate: Int = 0
    override var accurateCaptureRate: Double = 0.toDouble()

    private var millis: Long = 0
    private var timer: Long = 0
    private var pausedTimer: Long = 0

    init {
        this.samples = ByteArray(captureSize)
        this.accurateCaptureRate = captureRate.toDouble()
    }

    fun update(samples: ByteArray, samplingRate: Int) {
        val buffSize = Math.floor(captureRate / 1000.0 * BeatDetector.BUFFER_SIZE_SEC).toInt()
        if (timer % buffSize == 0L) {
            accurateCaptureRate = buffSize.toDouble() / ((Calendar.getInstance().timeInMillis - millis).toDouble() / 1000.0)
            millis = Calendar.getInstance().timeInMillis
        }
        timer++
        this.samples = samples
        this.samplingRate = samplingRate
    }

    fun pause() {
        if (pausedTimer == 0L) {
            pausedTimer = Calendar.getInstance().timeInMillis
        }
    }

    fun resume() {
        if (pausedTimer != 0L) {
            millis += Calendar.getInstance().timeInMillis - pausedTimer
            pausedTimer = 0
        }
    }
}
