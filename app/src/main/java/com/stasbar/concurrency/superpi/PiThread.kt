package com.stasbar.concurrency.superpi

class PiThread(private val iterations: Long) : Thread() {
    private val calculator: PiCalculator = PiCalculator() //TODO compare with static method

    private var ended = false

    override fun run() {
        calculator.gaussLegendre(iterations)
        ended = true
    }

    fun hasEnded(): Boolean {
        return ended
    }
}