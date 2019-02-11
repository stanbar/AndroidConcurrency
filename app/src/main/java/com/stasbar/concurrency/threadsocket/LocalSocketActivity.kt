package com.stasbar.concurrency.threadsocket

import android.content.Intent
import android.net.LocalSocket
import android.net.LocalSocketAddress
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stasbar.concurrency.R
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class LocalSocketActivity : AppCompatActivity(), CoroutineScope {
    private val parent = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + parent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread_socket)
        val intent = Intent(this, LocalSocketService::class.java)
        startService(intent)
        val socket = LocalSocket()
        launch {
            delay(1000)
            socket.connect(LocalSocketAddress("test"))
            socket.outputStream.use {
                val writer = it.bufferedWriter()
                writer.write("Hello")
                writer.write("How are you ?")
                writer.flush()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        parent.cancel()
    }
}
