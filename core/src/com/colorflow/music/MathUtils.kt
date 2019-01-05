package com.colorflow.music

import java.util.Arrays

object MathUtils {

    fun sort(array: DoubleArray, descending: Boolean) {
        Arrays.sort(array)
        var swap: Double
        if (descending) {
            for (i in 0 until array.size / 2) {
                swap = array[i]
                array[i] = array[array.size - 1 - i]
                array[array.size - 1 - i] = swap
            }
        }
    }

    fun sort(array: IntArray, descending: Boolean) {
        Arrays.sort(array)
        var swap: Int
        if (descending) {
            for (i in 0 until array.size / 2) {
                swap = array[i]
                array[i] = array[array.size - 1 - i]
                array[array.size - 1 - i] = swap
            }
        }
    }

    fun indexOf(array: DoubleArray, value: Double): Int {
        for (i in array.indices) {
            if (array[i] == value) {
                return i
            }
        }
        return -1
    }

    fun indexOf(array: IntArray, value: Double): Int {
        for (i in array.indices) {
            if (array[i].toDouble() == value) {
                return i
            }
        }
        return -1
    }

    fun mean(array: DoubleArray): Double {
        var sum = 0.0
        var i = 0
        for (a in array) {
            if (!java.lang.Double.isNaN(a)) {
                sum += a
                i++
            }
        }
        return sum / i.toDouble()
    }

    fun mean(array: IntArray): Double {
        var sum = 0.0
        var i = 0
        for (a in array) {
            sum += a
            i++

        }
        return sum / i.toDouble()
    }

    fun rootMeanSquare(array: DoubleArray): Double {
        var sum = 0.0
        var i = 0
        for (a in array) {
            if (!java.lang.Double.isNaN(a)) {
                sum += a * a
                i++
            }
        }
        return Math.sqrt(sum / i.toDouble())
    }

    fun weightedAvg(array: DoubleArray): Double {
        var sum = 0.0
        var w = 0.0
        for (i in array.indices) {
            if (!java.lang.Double.isNaN(array[i])) {
                sum += array[i] * i
                w += array[i]
            }
        }
        return sum / w
    }


    fun `var`(array: DoubleArray): Double {
        val mean = mean(array)
        var temp = 0.0
        var i = 0
        for (a in array) {
            if (!java.lang.Double.isNaN(a)) {
                temp += (a - mean) * (a - mean)
                i++
            }
        }
        return temp / (i - 1).toDouble()
    }

    fun `var`(array: IntArray): Double {
        val mean = mean(array)
        var temp = 0.0
        var i = 0
        for (a in array) {
            if (!java.lang.Double.isNaN(a.toDouble())) {
                temp += (a - mean) * (a - mean)
                i++
            }
        }
        return temp / (i - 1).toDouble()
    }

    fun autocorrelation(array: DoubleArray): DoubleArray {
        var sum: Double
        val result = DoubleArray(array.size)
        for (l in array.indices) {
            sum = 0.0
            for (i in array.indices) {
                if (i - l >= 0 && i - l < array.size) {
                    sum += array[i] * array[i - l]
                }
            }
            result[l] = sum
        }
        return result
    }

    fun modDFT(signal: DoubleArray): DoubleArray {
        val dft = DoubleArray(signal.size)
        for (q in dft.indices) {
            var sumR = 0.0
            var sumI = 0.0
            for (n in signal.indices) {
                sumR += signal[n] * Math.cos(-2.0 * Math.PI * n.toDouble() * q.toDouble() / signal.size.toDouble())
                sumI += signal[n] * Math.sin(-2.0 * Math.PI * n.toDouble() * q.toDouble() / signal.size.toDouble())
            }
            dft[q] = Math.sqrt(sumR * sumR + sumI * sumI)
        }
        return dft
    }

    fun isMultiple(a: Double, b: Double): Boolean {
        val r = Math.max(a, b) / Math.min(a, b)
        return r - Math.round(r) < 0.01
    }

}
