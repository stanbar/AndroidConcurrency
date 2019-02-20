package com.stasbar.concurrency

import android.app.IntentService
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL


/**
 * Created by stasbar on 07.07.2017
 */
class DownloadIntentService : IntentService("DownloadIntentService") {
    companion object {
        val FILE_NAME = "image"
        val URL_KEY = "url"
        val ACTION_COMPLETE = "action_complete"
        val PACKAGE_NAME_KEY = "package_name"
    }

    override fun onHandleIntent(intent: Intent) {
        val url = intent.getStringExtra(URL_KEY)
        val packageName = intent.getStringExtra(PACKAGE_NAME_KEY)

        saveImageIntoFile(url)

        val actionCompleteIntent = Intent(ACTION_COMPLETE)
        actionCompleteIntent.`package` = packageName

        sendBroadcast(actionCompleteIntent)
    }


    private fun saveImageIntoFile(src: String) {
        try {
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            val myBitmap = BitmapFactory.decodeStream(input)

            val out = FileOutputStream(File(cacheDir, FILE_NAME))
            myBitmap.compress(Bitmap.CompressFormat.WEBP,70,out)

            Log.i("DownloadIntentService", cacheDir.absolutePath)
            //Cleanup
            out.close()
            input.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}