package com.stasbar.concurrency.looper

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.MessageQueue
import timber.log.Timber

class LooperThread : Thread() {
    var handler: Handler? = null

    override fun run() {
        Looper.prepare()
        handler = Handler { msg: Message? ->
            if (msg?.what == 0) {
                doLongRunningOperation(msg.data.getString("message", ""))
            }
            false
        }

        val queue = Looper.myQueue()
        val idleHandler = object : MessageQueue.IdleHandler {
            var invocations = 0
            override fun queueIdle(): Boolean {
                Timber.d("[${invocations++}] Dispatched in idle handler")
                return invocations < 10
            }

        }
        queue.addIdleHandler(idleHandler)
        Looper.loop()
        Timber.d("Released thread from looping")
    }

    private fun doLongRunningOperation(message: String) {
        Timber.tag(Thread.currentThread().name).d("Send from LooperThread.Looper  with message $message")

        Handler(Looper.getMainLooper()).post {
            Timber.tag(Thread.currentThread().name).d("Invoked from MainLooper with message $message")
        }
    }
}