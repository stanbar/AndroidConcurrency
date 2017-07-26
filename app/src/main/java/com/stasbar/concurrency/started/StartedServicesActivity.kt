package com.stasbar.concurrency.started


import android.content.Intent
import android.os.*
import android.support.v7.app.AppCompatActivity

import com.stasbar.concurrency.LoggingService
import com.stasbar.concurrency.MediaService

import com.stasbar.concurrency.MediaService.Companion.SOUND_KEY
import com.stasbar.concurrency.R
import kotlinx.android.synthetic.main.activity_started_services.*

class StartedServicesActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_started_services)
        setSupportActionBar(toolbar)



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
