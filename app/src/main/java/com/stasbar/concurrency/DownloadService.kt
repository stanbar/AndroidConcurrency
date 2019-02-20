package com.stasbar.concurrency

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.*
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


/**
 * Created by stasbar on 06.07.2017
 */
class DownloadService : Service() {
    companion object {
        val MESSENGER_KEY = "MESSENGER"
        val URL_KEY = "URL"
        val FORMAT_KEY = "FORMAT"
        val DRAWABLE = "DRAWABLE"
        val BITMAP = "BITMAP"
    }
    private lateinit var mServiceLooper : Looper
    private lateinit var mServiceHandler : ServiceHandler

    override fun onCreate() {
        super.onCreate()
        /**
         * Start up the thread running the service, which we create separate thread since,
         * Service by default run in main Thread
         */
        val handlerThread = HandlerThread("DownloadService")
        handlerThread.start()
        /**
         * Get the Thread-Specific Storage Looper from this Service HandlerThread
         * and create Handler to it so we can pass work (sendMessages) to his Looper messageQueue
         */

        mServiceLooper = handlerThread.looper
        mServiceHandler = ServiceHandler(mServiceLooper)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        /**
         * For each start requests create a message and
         * pass it to looper associated in WorkerThread/HandlerThread created in onCreate()
         * so it starts a job and pass startId so we know what request we are stopping on finish job
         */
        val msg = mServiceHandler.obtainMessage()
        msg.obj = intent
        msg.arg1 = startId
        mServiceHandler.sendMessage(msg)  // or msg.sendToTarget()

        return START_NOT_STICKY

    }


    //Handler that receives messages from a thread
    inner class ServiceHandler(looper : Looper) : Handler(looper) {

        //Dispaches callback hook methods to download a file
        override fun handleMessage(msg: Message?) {
            if (msg==null)
                return
            val intent = msg.obj as Intent
            downloadImage(intent)

            // Stop a service with startId so so that we don't stop service in the middle of handling another job
            stopSelf(msg.arg1)
        }
    }


    //Download image and notify the client
    fun downloadImage(intent: Intent){
        val msg = Message.obtain()
        val url = intent.getStringExtra(URL_KEY)
        when (intent.getStringExtra(FORMAT_KEY)){
            BITMAP -> msg.obj = loadBitmapFromURL(url)
            DRAWABLE -> msg.obj = loadDrawableFromURL(url)
        }

        val messenger = intent.extras?.get(MESSENGER_KEY) as Messenger
        messenger.send(msg)
    }


    private fun loadBitmapFromURL(src: String): Bitmap? {
        return try {
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            val myBitmap = BitmapFactory.decodeStream(input)
            myBitmap
        } catch (e: IOException) {
            // Log exception
            null
        }
    }

    private fun loadDrawableFromURL(url: String): Drawable? {
        return try {
            val `is` = URL(url).content as InputStream
            val d = Drawable.createFromStream(`is`,url)
            d
        } catch (e: Exception) {
            null
        }

    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        mServiceLooper.quit()
    }

}