package com.stasbar.concurrency.looper

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import com.stasbar.concurrency.R
import com.stasbar.concurrency.hide
import com.stasbar.concurrency.show
import kotlinx.android.synthetic.main.activity_looper.*
import kotlin.random.Random

class LooperActivity : AppCompatActivity() {

    private lateinit var looperThread: LooperThread
    private lateinit var consumeAndQuitThread1: ConsumeAndQuitThread
    private lateinit var consumeAndQuitThread2: ConsumeAndQuitThread
    private lateinit var backgroundThread: BackgroundWorkerThread

    val progressHandler = Handler { message ->
        when (message.what) {
            SHOW_PROGRESS_BAR -> progressBar.show()
            HIDE_PROGRESS_BAR -> {
                tvResult.text = message.arg1.toString()
                progressBar.hide()
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_looper)
        looperThread = LooperThread()
        looperThread.start()

        btnLooperCommunication.setOnClickListener {
            looperThread.handler?.obtainMessage(0)?.let { message ->
                message.data.putString("message", "Hello World")
                message.sendToTarget()
            }
        }

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
    }

    companion object {
        const val SHOW_PROGRESS_BAR = 0
        const val HIDE_PROGRESS_BAR = 1
    }
}
