package com.stasbar.concurrency.piping

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stasbar.concurrency.R
import com.stasbar.concurrency.onTextChanged
import kotlinx.android.synthetic.main.activity_pipe.*
import timber.log.Timber
import java.io.IOException
import java.io.PipedReader
import java.io.PipedWriter

class PipeActivity : AppCompatActivity() {
    lateinit var workerThread: Thread
    lateinit var writer: PipedWriter
    lateinit var reader: PipedReader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pipe)
        writer = PipedWriter()
        reader = PipedReader()
        try {
            writer.connect(reader)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        editText.onTextChanged { s, start, before, count ->
            try {
                if (count > before) {
                    writer.write(s.subSequence(before, count).toString())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        workerThread = Thread(TextHandlerTask(reader))
        workerThread.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        workerThread.interrupt()
        try {
            writer.close()
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    class TextHandlerTask(private val reader: PipedReader) : Runnable {
        override fun run() {
            while (!Thread.currentThread().isInterrupted) {
                try {
                    var out = reader.read()
                    while (out != -1) {
                        Timber.d(out.toChar().toString())
                        out = reader.read()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

    }
}
