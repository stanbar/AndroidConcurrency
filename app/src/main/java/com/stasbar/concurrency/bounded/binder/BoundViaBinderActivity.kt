package com.stasbar.concurrency.bounded.binder

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.stasbar.concurrency.R
import com.stasbar.concurrency.aidl.KeyGenerator
import com.stasbar.concurrency.aidl.KeyGeneratorCallback
import kotlinx.android.synthetic.main.activity_bound_via_binder.*

class BoundViaBinderActivity : AppCompatActivity() {
    var mService: KeyGenerator? = null
    var mBound: Boolean = false

    val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, iService: IBinder?) {
            mService = KeyGenerator.Stub.asInterface(iService)
            mBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            mService = null
            mBound = false
        }
    }
    val callback = object : KeyGeneratorCallback.Stub() {

        override fun sendKey(key: String?) {
            runOnUiThread {
                buttonGenerateUUID.text = key
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bound_via_binder)
        buttonGenerateUUID.setOnClickListener {
            if (mBound) mService?.setCallback(callback) //Invoke two-way remote call
        }
    }

    override fun onStart() {
        super.onStart()

        val intent = Intent(KeyGenService::class.java.name)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        if (mBound) unbindService(connection)
        super.onStop()
    }
}
