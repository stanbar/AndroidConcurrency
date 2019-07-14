package com.stasbar.concurrency.looper

import android.annotation.SuppressLint
import android.os.*
import android.util.Log
import android.util.LogPrinter
import androidx.appcompat.app.AppCompatActivity
import com.stasbar.concurrency.R
import com.stasbar.concurrency.hide
import com.stasbar.concurrency.show
import kotlinx.android.synthetic.main.activity_looper.*
import timber.log.Timber
import kotlin.random.Random

class LooperActivity : AppCompatActivity() {

    private lateinit var looperThread: LooperThread
    private lateinit var consumeAndQuitThread1: ConsumeAndQuitThread
    private lateinit var consumeAndQuitThread2: ConsumeAndQuitThread
    private lateinit var backgroundThread: BackgroundWorkerThread
    private lateinit var logHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_looper)
        setupBgLooper()

        setupIdle()

        setupBgToUiCommunication()

        setupLoggingLooperQueue()
    }

    @SuppressLint("HandlerLeak")
    private fun setupLoggingLooperQueue() {
        Thread {
            Looper.prepare()
            logHandler = object : Handler() {
                override fun handleMessage(msg: Message?) {
                    Timber.tag("LogHandler").d("handleMessage - what ${msg?.what}")
                }
            }
            Looper.loop()
        }.start()

        btnLogLooperQueue.setOnClickListener {
            logHandler.sendEmptyMessageDelayed(1, 2000)
            logHandler.sendEmptyMessage(2)
            logHandler.obtainMessage(3, 0, 0, Any()).sendToTarget()
            logHandler.sendEmptyMessageDelayed(4, 300)
            logHandler.postDelayed({ Timber.d("Executed") }, 300)
            logHandler.sendEmptyMessage(5)

            logHandler.dump(LogPrinter(Log.DEBUG, "LogHandler"), "")
        }
    }

    private fun setupBgLooper() {
        looperThread = LooperThread()
        looperThread.start()

        btnLooperCommunication.setOnClickListener {
            looperThread.handler?.obtainMessage(0)?.let { message ->
                message.data.putString("message", "Hello World")
                message.sendToTarget()
            }
        }
    }

    private fun setupIdle() {
        consumeAndQuitThread1 = ConsumeAndQuitThread(1)
        consumeAndQuitThread1.start()

        consumeAndQuitThread2 = ConsumeAndQuitThread(2)
        consumeAndQuitThread2.start()

        btnIdle.setOnClickListener {
            repeat(10) { threadNo ->
                Thread({
                    repeat(10) {
                        SystemClock.sleep(Random.nextLong(10))
                        if (it % 2 == 0)
                            consumeAndQuitThread1.enqueueData(it)
                        else
                            consumeAndQuitThread2.enqueueData(it)
                    }
                }, "Producer-$threadNo").start()
            }
        }

    }

    private val progressHandler = Handler { message ->
        when (message.what) {
            SHOW_PROGRESS_BAR -> progressBar.show()
            HIDE_PROGRESS_BAR -> {
                tvResult.text = message.arg1.toString()
                progressBar.hide()
            }
        }
        false
    }

    private fun setupBgToUiCommunication() {
        backgroundThread = BackgroundWorkerThread(1, progressHandler)
        backgroundThread.start()

        btnWorker.setOnClickListener {
            backgroundThread.doWork()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        looperThread.handler?.looper?.quit()
        consumeAndQuitThread1.consumerHandler?.looper?.quit()
        consumeAndQuitThread2.consumerHandler?.looper?.quit()
        backgroundThread.exit()
        logHandler.looper.quit()
    }

    companion object {
        const val SHOW_PROGRESS_BAR = 0
        const val HIDE_PROGRESS_BAR = 1
    }
}
