package com.stasbar.concurrency

import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.stasbar.concurrency.view.LogView

/**
 * Created by stasbar on 23.10.2017
 */
abstract class LoggableActivity : AppCompatActivity(){
    abstract fun getLogger() : LogView
    private val handler = Handler()

    protected fun log(message: String) {
        val threadName = Thread.currentThread().name
        handler.post { getLogger().log("$threadName:  $message") }
    }
}