package com.stasbar.concurrency.looper

import android.os.Handler
import android.os.Looper
import android.os.MessageQueue
import timber.log.Timber

class ConsumeAndQuitThread(number: Int) : Thread("ConsumeAndQuitThread-$number"), MessageQueue.IdleHandler {

    var consumerHandler: Handler? = null

    private var isFirstIdle = true
    override fun queueIdle(): Boolean {
        if (isFirstIdle) {
            isFirstIdle = false
            return true
        }
        Timber.tag(Thread.currentThread().name).d("MessageQueue is in idle, it's going to quit now. Bye !")
        Looper.myLooper()?.quit()
        return false
    }

    override fun run() {
        Looper.prepare()
        consumerHandler = Handler { message ->
            Timber.tag(Thread.currentThread().name).d("processing message: ${message.what}")
            sleep(100)
            false
        }

        Looper.myQueue().addIdleHandler(this)
        Looper.loop()
    }

    fun enqueueData(what: Int) {
        consumerHandler?.sendEmptyMessage(what)
        Timber.tag(Thread.currentThread().name).d("Sending message to handler")
    }
}