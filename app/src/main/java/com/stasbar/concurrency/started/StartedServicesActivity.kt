package com.stasbar.concurrency.started


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stasbar.concurrency.LoggingService
import com.stasbar.concurrency.R
import com.stasbar.concurrency.started.MediaService.Companion.SOUND_KEY
import kotlinx.android.synthetic.main.activity_started_services.*


class StartedServicesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_started_services)

        setSupportActionBar(toolbar)
        setTitle(R.string.started_service)
        startButton.setOnClickListener {
            val musicIntent = Intent(this, MediaService::class.java)
            musicIntent.putExtra(SOUND_KEY, R.raw.sound)
            startService(musicIntent)
            LoggingService.log(this, "started MusicService")
        }
        stopButton.setOnClickListener {
            val musicIntent = Intent(this, MediaService::class.java)
            stopService(musicIntent)
            LoggingService.log(this, "stopped MusicService")
        }

    }
}
