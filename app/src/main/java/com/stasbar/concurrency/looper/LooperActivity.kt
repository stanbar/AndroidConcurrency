package com.stasbar.concurrency.looper

import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import com.stasbar.concurrency.R
import kotlinx.android.synthetic.main.activity_looper.*
import kotlin.random.Random

class LooperActivity : AppCompatActivity() {

    private lateinit var looperThread: LooperThread
    private lateinit var consumeAndQuitThread: ConsumeAndQuitThread

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

        consumeAndQuitThread = ConsumeAndQuitThread()
        consumeAndQuitThread.start()

        btnIdle.setOnClickListener {
            repeat(10) {
                Thread {
                    repeat(10) {
                        SystemClock.sleep(Random.nextLong(10))
                        consumeAndQuitThread.enqueueData(it)
                    }
                }.start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        looperThread.handler?.looper?.quit()
    }
}
