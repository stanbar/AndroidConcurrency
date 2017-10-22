package com.stasbar.concurrency

import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.stasbar.concurrency.view.LogView

/**
 * Created by stasbar on 23.10.2017
 */
abstract class LoggableActivity : AppCompatActivity(){
    abstract fun getLogger() : LogView
    val handler = Handler()


    protected fun log(message: String) {
        val threadName = Thread.currentThread().name
        handler.post { getLogger().log("$threadName:  $message") }
    }
}