package com.stasbar.concurrency.looper

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import com.stasbar.concurrency.R
import kotlinx.android.synthetic.main.activity_looper.*
import timber.log.Timber

class LooperActivity : AppCompatActivity() {

    lateinit var looperThread: LooperThread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_looper)
        looperThread = LooperThread()
        looperThread.start()

        button.setOnClickListener {
            looperThread.handler?.obtainMessage(0)?.let { message ->
                message.data.putString("message", "Hello World")
                message.sendToTarget()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        looperThread.handler?.looper?.quit()
    }

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

            Looper.loop()
            Timber.d("Released thread from looping")
        }

        private fun doLongRunningOperation(message: String) {
            Handler(Looper.getMainLooper()).post {
                Timber.d("Invoked from MainLooper in thread: ${Thread.currentThread().name} with message $message")
            }

            Timber.d("Invoked from LooperThread in thread: ${Thread.currentThread().name} with message $message")
        }
    }
}
