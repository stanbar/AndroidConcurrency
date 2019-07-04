package com.stasbar.concurrency.benchmarks

import timber.log.Timber

@ExperimentalUnsignedTypes
class Updater(
    val id: Int,
    var searchLength: ULong = 0u,
    val onUpdate: (JobUpdate) -> Unit
) : Thread() {
    var currentNonce: ULong = 0u

    fun incNonce() = currentNonce++

    override fun run() {
        while (!isInterrupted) {
            try {
                sleep(1000)
            } catch (e: InterruptedException) {
                Timber.d(e)
            }

            onUpdate(JobUpdate(id, currentNonce, searchLength))
        }
    }
}