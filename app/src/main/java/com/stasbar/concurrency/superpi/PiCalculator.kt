package com.stasbar.concurrency.superpi

import kotlin.math.pow
import kotlin.math.sqrt

class PiCalculator {
    fun gaussLegendre(iterations: Long): Double {
        var a = 1.0
        var b = 1.0f / sqrt(2.0)
        var t = (1.0f / 4.0f).toDouble()
        var p = 1.0

        for (i in 0 until iterations) {
            val aNext = (a + b) / 2
            val bNext = sqrt(a * b)
            val tNext = t - p * (a - aNext).pow(2.0)
            val pNext = 2 * p
            a = aNext
            b = bNext
            t = tNext
            p = pNext
        }

        return (a + b).pow(2.0) / (4 * t)
    }
}