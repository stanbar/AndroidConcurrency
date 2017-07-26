package com.stasbar.concurrency

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by stasbar on 07.07.2017
 */
class DeferredDownloadService : IntentService("DeferredDownloadService") {
    companion object {
        val FILE_NAME = "image"
        val URL_KEY = "url"
        val ACTION_COMPLETE = "action_complete"
        val PACKAGE_NAME_KEY = "package_name"
    }

    override fun onHandleIntent(intent: Intent) {
        val url = intent.getStringExtra(URL_KEY)

        saveImageIntoFile(url)

        val viewDownloadedImageIntent = Intent(this@DeferredDownloadService, ViewImageActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this@DeferredDownloadService, 0, viewDownloadedImageIntent, 0)
        Log.i("DeferredDownloadService","onHandleIntent" )
        sendNotification(pendingIntent)
    }

    fun sendNotification(intent: PendingIntent) {
        val notification = NotificationCompat.Builder(this, "Default")
                .setContentTitle("ImageDownloadComplete")
                .setContentText(cacheDir.absolutePath.toString())
                .setContentIntent(intent)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .build()
        val notificationService = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("Default", NotificationChannel.DEFAULT_CHANNEL_ID,NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "My Description"
            notificationService.createNotificationChannel(channel)
        }

        notificationService.notify(0, notification)

        Log.i("DeferredDownloadService","sendNotification" )
    }


    fun saveImageIntoFile(src: String) {
        var input: InputStream? = null
        var out: FileOutputStream? = null
        try {
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            input = connection.inputStream
            val myBitmap = BitmapFactory.decodeStream(input)

            out = FileOutputStream(File(cacheDir, FILE_NAME))
            myBitmap.compress(Bitmap.CompressFormat.WEBP, 70, out)

            //Cleanup

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            out?.close()
            input?.close()
        }
    }
}