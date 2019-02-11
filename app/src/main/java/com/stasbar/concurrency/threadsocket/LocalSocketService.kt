package com.stasbar.concurrency.threadsocket

import android.app.Service
import android.content.Intent
import android.net.LocalServerSocket
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LocalSocketService : Service(), CoroutineScope {
    companion object {
        const val TAG = "LocalSocketService"
    }

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, "onBind")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        launch(Dispatchers.IO) {
            val socket = LocalServerSocket("test").accept()
            Log.d(TAG, "open socket")
            socket.inputStream.bufferedReader().useLines {
                it.iterator().forEach { println(it) }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        Log.d(TAG, "onDestroy")
    }

}
