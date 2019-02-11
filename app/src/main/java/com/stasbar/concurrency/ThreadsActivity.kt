package com.stasbar.concurrency

import android.os.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_threads.*


class ThreadsActivity : AppCompatActivity() {

    lateinit var backgroundHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_threads)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        usingHandlerAndThread()
        usingHandlerThread()
    }

    private fun usingHandlerAndThread() {
        val thread = Thread({
            Looper.prepare()

            backgroundHandler = object : Handler(Looper.myLooper()) {
                override fun handleMessage(msg: Message) {
                    try {
                        Thread.sleep(0)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                    updateTextView(msg)
                }
            }
            Looper.loop()
        }, "Background Thread")
        thread.start()
        try {
            Thread.sleep(100)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        backgroundHandler.obtainMessage(0, "message from UI").sendToTarget()

    }

    private fun usingHandlerThread() {
        val handlerThread = HandlerThread("Background HandlerThread")
        handlerThread.start()

        val backgroundHandler = object : Handler(handlerThread.looper) {
            override fun handleMessage(msg: Message) {
                updateTextView(msg)
            }
        }

        try {
            Thread.sleep(100)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        backgroundHandler.obtainMessage(0, "elo").sendToTarget()
    }

    private fun updateTextView(msg: Message) {
        val text = msg.obj as String
        textViewThreads.text = String.format("%s\n%s: %s", textViewThreads.text.toString(), Thread.currentThread().name, text)
    }

}
