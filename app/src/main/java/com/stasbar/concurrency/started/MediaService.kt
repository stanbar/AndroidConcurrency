package com.stasbar.concurrency.started

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.stasbar.concurrency.R


/**
 * Created by stasbar on 06.07.2017
 *
 * Started Service is often used to perform single operation & might not return the result to caller directly (Command
 * Processor Pattern), you can start this service by Intent.
 *
 * After calling startService(Intent(this, MediaService::class.java)), OS find that service and if it's not created yet.
 * It call onCreate(). Then our intent is passed to onStartCommand(), and here is the place where you can do your job.
 * This method then return result to Android and not to our client.
 * A service can stop itself by stopSelf(), or another component can stop it by calling stopService()
 *
 * Usages: SMS, MMS, CalendarEventNotification Services,
 *
 * We can set how to treat the service when it crashes or stopped or restarted, by returning flag in onStartCommand().
 * START_STICKY - Don\t redelived intent to onStartCommand()
 * START_REDELIVER_INTENT - Restart Service via onStartCommand() supplying the same intent as was deliver this time.
 * START_NOT_STICKY - Service should remain stopped until explicitly started by application code.
 *
 * Normally the services lifes in the same process as the caller, but you can direct in Manifest file to start it in
 * different process by:
 * android:process":myProcess"
 * android:exported="true" which means that everybody that has permissions can access this service, it's not private
 *
 * startService with intent cause service to be activated on demand, onCreate() is called if it's not already running.
 * If it's already running it will be delivered to onStartCommand. This is baed on Activator Pattern (allows you to
 * start applications component or processes on demand so you don't end up using resources unless they are needed.
 * So the idea is to optimize system resources so you don't take up memory or processor time if you are not actually
 * using the thing that is involved.
 *
 * This service can be started via
 * adb shell am startservice com.stasbar.concurrency/.started.MediaService
 */
class MediaService : Service() {
    companion object {
        const val SOUND_KEY = "SongID"
    }

    lateinit var mediaPlayer: MediaPlayer

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mediaPlayer = MediaPlayer.create(
            this, intent.getIntExtra(
                SOUND_KEY,
                R.raw.sound
            )
        )
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