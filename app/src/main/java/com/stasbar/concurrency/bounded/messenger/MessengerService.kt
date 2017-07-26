package com.stasbar.concurrency.bounded.messenger

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.util.Log
import com.stasbar.concurrency.R

class MessengerService : Service() {
    companion object {
        const val MSG_BLOCK_THREAD = 1
        const val MSG_PLAY_SOUND = 2
        const val MESSENGER = "messenger"
    }

    val mMessenger: Messenger = Messenger(InHandler())
    private lateinit var clientMessenger: Messenger

    var mediaPlayer: MediaPlayer? = null

    inner class InHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_BLOCK_THREAD -> blockThread(msg)
                MSG_PLAY_SOUND -> playSound(msg)
                else -> super.handleMessage(msg)
            }
        }
    }

    private fun blockThread(msg: Message) {
        //Does not take effect since sleep is faster than processing this message by Looper
        clientMessenger.send(Message.obtain(null, BoundViaMessengerActivity.SET_TEST, 0, 0, "UI is Sleeping"))
        Log.d("MessengerService", "Processing...")
        Thread.sleep(1000)
        Log.d("MessengerService", "Done!")
        clientMessenger.send(Message.obtain(null, BoundViaMessengerActivity.SET_TEST, 0, 0, "UI is Running"))


    }

    private fun playSound(msg: Message) {
        val mediaPlayer = MediaPlayer.create(this@MessengerService, R.raw.sound)
        mediaPlayer.isLooping = false
        mediaPlayer.start()
        this.mediaPlayer = mediaPlayer

    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        clientMessenger = intent.getParcelableExtra(MESSENGER)!!
        return mMessenger.binder
    }
}
