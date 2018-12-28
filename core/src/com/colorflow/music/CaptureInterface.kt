package com.colorflow.music

interface CaptureInterface {

    val type: Type

    val samples: ByteArray

    val samplingRate: Int

    val captureSize: Int

    val captureRate: Int

    val accurateCaptureRate: Double

    enum class Type {
        PCM, FFT
    }
}
