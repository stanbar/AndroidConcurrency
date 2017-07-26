package com.stasbar.concurrency

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder


/**
 * Created by stasbar on 06.07.2017
 */
class MediaService : Service() {
    companion object {
        val SOUND_KEY = "SongID"
    }
    lateinit var mediaPlayer: MediaPlayer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaPlayer = MediaPlayer.create(this, intent!!.getIntExtra(SOUND_KEY, R.raw.sound))
        mediaPlayer.isLooping = false
        mediaPlayer.start()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        mediaPlayer.stop()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}