package com.stasbar.concurrency.java_concurrency

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.stasbar.concurrency.LoggableActivity
import com.stasbar.concurrency.R
import com.stasbar.concurrency.data.Transacton
import com.stasbar.concurrency.view.LogView
import kotlinx.android.synthetic.main.activity_java_concurency.*
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.system.measureNanoTime

class JavaConcurencyActivity : LoggableActivity() {
    override fun getLogger() = logger


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_java_concurency)

        btnStart.setOnClickListener {
            val threadPool = etThreadPool.text.toString().toIntOrNull()
            val complexity = etComplexity.text.toString().toIntOrNull()
            if(complexity == null || threadPool == null)
                return@setOnClickListener
            val action = {
                repeat(complexity) {
                    it / it
                }
            }

            val time = time(Executors.newFixedThreadPool(threadPool), threadPool, action)
            log("Process took $time ns")
        }


    }


    fun time(executor: Executor, concurrency: Int, action: () -> Unit): Long {

        val ready = CountDownLatch(concurrency)
        val start = CountDownLatch(1)
        val done = CountDownLatch(concurrency)

        repeat(concurrency) {
            executor.execute {
                ready.countDown() //Tell the timer we are ready
                log("Ready")
                try {
                    start.await() //Wait till peers are ready
                    action()
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                } finally {
                    done.countDown() //Tell timer we are done
                    log("Done")

                }
            }
        }

        ready.await() //Wait for all workers to be ready

        return measureNanoTime {
            // For interval timing always use nanoTime in preference to millis, nano time is both more precise and accurate.
            //and it is not affected by adjustments to the system real-time clock
            log("Start")
            start.countDown()
            done.await()
        }

    }
}
