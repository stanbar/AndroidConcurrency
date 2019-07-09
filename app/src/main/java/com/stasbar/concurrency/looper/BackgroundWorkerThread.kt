package com.stasbar.concurrency.looper

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import timber.log.Timber
import kotlin.random.Random

class BackgroundWorkerThread(number: Int, private val progressHandler: Handler) :
    Thread("BackgroundWorkerThread-$number") {
    private lateinit var backgroundHandler: Handler

    override fun run() {
        Looper.prepare()
        backgroundHandler = Handler()
        Looper.loop()
        Timber.d("Released looper from looping")
    }

    fun doWork() {
        backgroundHandler.post {
            val msgShowProgress = progressHandler.obtainMessage(LooperActivity.SHOW_PROGRESS_BAR)
            progressHandler.sendMessage(msgShowProgress)

            SystemClock.sleep(1000)

            val msgHideProgress =
                progressHandler.obtainMessage(LooperActivity.HIDE_PROGRESS_BAR, Random.nextInt(10), 0, null)
            progressHandler.sendMessage(msgHideProgress)
        }
    }

    fun exit() {
        Timber.d("exit")
        backgroundHandler.looper.quit()
    }
}