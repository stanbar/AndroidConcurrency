package com.stasbar.concurrency.masterthesis

import android.util.Log

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
                Log.d("Updater", "InterruptedException")
            }

            onUpdate(JobUpdate(id, currentNonce, searchLength))

//                    val progress = (currentNonce.toDouble() / searchLength.toDouble() * 100).toInt()
//                    progressBar.post { progressBar.progress = progress }
//                    textView.post { textView.text = "$currentNonce/$searchLength[$progress]" }
        }
    }
}