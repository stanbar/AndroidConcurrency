package com.stasbar.concurrency

import android.app.IntentService
import android.app.Service
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Created by stasbar on 06.07.2017
 */
class LoggingService : IntentService("LoggingService") {
    companion object {
        val LOG_KEY = "LOG_MSG"
        fun log(context : Context, message : String){
            val intent = Intent(context, LoggingService::class.java)
            intent.putExtra(LOG_KEY,message)
            context.startService(intent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        // Don't restart if it gets shutdown
        return Service.START_NOT_STICKY
    }

    override fun onHandleIntent(intent: Intent?) {
        if(intent == null)
            return
        val logMessage = intent.getStringExtra(LOG_KEY)
        Log.i("Logging",logMessage)

    }




}