package com.stasbar.concurrency.executor

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.flexbox.FlexboxLayout
import com.stasbar.concurrency.R
import timber.log.Timber
import java.util.concurrent.*
import kotlin.random.Random

class ExecutorActivity : AppCompatActivity() {

    class ImageDownloadTask : Callable<Bitmap> {
        override fun call(): Bitmap {
            return downloadRemoteImage()
        }

        private fun downloadRemoteImage(): Bitmap {
            val height = 100
            val width = 100
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val color = if (x % 25 > 12 && y % 25 > 12) Color.rgb(
                        Random.nextInt(255),
                        Random.nextInt(255),
                        Random.nextInt(255)
                    ) else Color.WHITE
                    bitmap.setPixel(x, y, color)
                }
            }
            return bitmap
        }

    }

    class DownloadCompletionService(val executorService: ExecutorService) :
        ExecutorCompletionService<Bitmap>(executorService) {


        fun shutdown() = executorService.shutdown()
        fun isTerminated() = executorService.isTerminated
    }

    inner class ConsumerThread(val downloadCompletionService: DownloadCompletionService) : Thread() {

        override fun run() {
            super.run()
            try {
                while (!downloadCompletionService.isTerminated()) {
                    val future = downloadCompletionService.poll(1, TimeUnit.SECONDS)
                    if (future != null) {
                        addImage(future.get())
                    }
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_executor)

        findViewById<Button>(R.id.btnStartPreloaded).setOnClickListener {
            startPreloaded()
        }
        findViewById<Button>(R.id.btnStartZeroCoreSize).setOnClickListener {
            startZeroCoreSize()
        }
        findViewById<Button>(R.id.btnStartSingleThreadExecutor).setOnClickListener {
            startSingleThreadExecutor()
        }
        findViewById<Button>(R.id.btnStartSingleThreadExecutorCallable).setOnClickListener {
            startSingleThreadExecutorCallable()
        }
        findViewById<Button>(R.id.btnStartDownloadImages).setOnClickListener {
            startDownloadImages()
        }
    }

    private fun startDownloadImages() {
        val esc = DownloadCompletionService(Executors.newCachedThreadPool())
        ConsumerThread(esc).start()

        repeat(5) {
            esc.submit(ImageDownloadTask())
        }

        esc.shutdown()
    }

    private fun addImage(bitmap: Bitmap) {
        runOnUiThread {
            val imagesContainer = findViewById<FlexboxLayout>(R.id.imagesContainer)
            val imageView = ImageView(this)
            imageView.setImageBitmap(bitmap)
            imagesContainer.addView(imageView)
        }
    }

    private fun startSingleThreadExecutorCallable() {
        val executor = Executors.newSingleThreadExecutor()
        val future = executor.submit(Callable<String> {
            Timber.d("started")
            SystemClock.sleep(1000)
            Timber.d("finished")
            "Done"
        })
        executor.submit {
            Timber.d(future.get())
        }
    }

    private fun startSingleThreadExecutor() {
        val executor = Executors.newSingleThreadExecutor()
        repeat(10) {
            executor.execute {
                Timber.d("[$it] started")
                SystemClock.sleep(1000)
                Timber.d("[$it] finished")
            }
        }
    }

    var thread: Thread? = null
    private fun startZeroCoreSize() {
        val N = Runtime.getRuntime().availableProcessors()
        val executor = ThreadPoolExecutor(
            0,
            N * 2,
            60, TimeUnit.SECONDS,
            ArrayBlockingQueue<Runnable>(10)
        )
        thread = Thread {
            var counter = 0
            while (!Thread.interrupted()) {
                executor.submit {
                    Timber.d("[$counter] started")
                    SystemClock.sleep(500)
                }
                SystemClock.sleep(1000)
                counter++
            }
        }
        thread?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        thread?.interrupt()
        thread = null
    }

    private fun startPreloaded() {
        val preloadedTasks = LinkedBlockingQueue<Runnable>()
        repeat(20) {
            preloadedTasks.add(Runnable {
                Timber.d("Started task $it")
                SystemClock.sleep(1000)
                Timber.d("Finished task $it")
            })
        }

        val executor = ThreadPoolExecutor(5, 10, 1, TimeUnit.SECONDS, preloadedTasks)
        executor.prestartAllCoreThreads()
    }
}
