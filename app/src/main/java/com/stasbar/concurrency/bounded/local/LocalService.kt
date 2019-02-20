package com.stasbar.concurrency.bounded.local

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.util.*

class LocalService : Service() {
    private val mBinder: IBinder = LocalBinder()
    private val rand = Random()

    inner class LocalBinder : Binder() {
        fun getService(): LocalService = this@LocalService
    }

    override fun onBind(p0: Intent?): IBinder {
        return mBinder
    }

    fun getRand() = rand.nextInt(100)


}
