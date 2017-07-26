package com.stasbar.concurrency.bounded.messenger

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.support.v7.app.AppCompatActivity
import com.stasbar.concurrency.R
import kotlinx.android.synthetic.main.activity_bound_via_messenger.*

class BoundViaMessengerActivity : AppCompatActivity() {
    companion object {
        const val SET_TEST = 1
    }

    private lateinit var mMessenger: Messenger
    private lateinit var myMessenger: Messenger

    private val myHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                SET_TEST -> textView.text = msg.obj as String
                else -> super.handleMessage(msg)
            }
        }

    }

    private var mBound: Boolean = false
    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(calassName: ComponentName?) {
            mBound = false
        }

        override fun onServiceConnected(className: ComponentName?, service: IBinder) {
            mMessenger = Messenger(service)
            mBound = true
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bound_via_messenger)
        myMessenger = Messenger(myHandler)
        btnBlockUI.setOnClickListener { blockMainThread() }
        btnPlaySound.setOnClickListener { playSound() }

    }

    private fun playSound() {
        if (!mBound) return
        val message = Message.obtain(null, MessengerService.MSG_PLAY_SOUND, 0, 0)
        mMessenger.send(message)

    }

    private fun blockMainThread() {
        if (!mBound) return
        textView.text = "UI is Sleeping"
        val message = Message.obtain(null, MessengerService.MSG_BLOCK_THREAD, 0, 0)
        mMessenger.send(message)
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MessengerService::class.java)
        intent.putExtra(MessengerService.MESSENGER, myMessenger)
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (mBound) {
            unbindService(mConnection)
            mBound = false
        }
    }

}
