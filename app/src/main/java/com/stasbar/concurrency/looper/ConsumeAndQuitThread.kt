package com.stasbar.concurrency.looper

import android.os.Handler
import android.os.Looper
import android.os.MessageQueue
import timber.log.Timber

class ConsumeAndQuitThread : Thread("ConsumeAndQuitThread"), MessageQueue.IdleHandler {

    lateinit var consumerHandler: Handler

    private var isFirstIdle = true
    override fun queueIdle(): Boolean {
        if (isFirstIdle) {
            isFirstIdle = false
            return true
        }
        Timber.d("MessageQueue is in idle, it's going to quit now. Bye !")
        Looper.myLooper()?.quit()
        return false
    }

    override fun run() {
        Looper.prepare()
        consumerHandler = Handler { message ->
            Timber.d("ConsumeHandler ${message.what}")
            sleep(100)
            false
        }

        Looper.myQueue().addIdleHandler(this)
        Looper.loop()
    }

    fun enqueueData(what: Int) {
        consumerHandler.sendEmptyMessage(what)
    }
}