package com.stasbar.concurrency.bounded.local

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.stasbar.concurrency.R
import kotlinx.android.synthetic.main.activity_bound.*

class BoundLocalActivity : AppCompatActivity() {
    var mBound: Boolean = false
    lateinit var mService: LocalService
    private val conn = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            mBound = false
        }

        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            mBound = true
            mService = (binder as LocalService.LocalBinder).getService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bound)
        buttonNextRand.setOnClickListener { onButtonClick() }

    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, LocalService::class.java)
        bindService(intent, conn, Context.BIND_AUTO_CREATE)

    }

    override fun onStop() {
        super.onStop()
        if (mBound) unbindService(conn)
    }

    private fun onButtonClick() {
        if (mBound) buttonNextRand.text = mService.getRand().toString()
    }
}
