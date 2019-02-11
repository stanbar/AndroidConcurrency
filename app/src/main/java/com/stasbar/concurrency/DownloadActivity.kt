package com.stasbar.concurrency

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.TimeUtils
import kotlinx.android.synthetic.main.activity_download.*
import java.io.File

class DownloadActivity : AppCompatActivity() {
    val downloadHandler: Handler = MyHandler()
    var start: Long = 0L
    var end: Long = 0L
    val onEvent = MyBroadcastReceiver()

    inner class MyHandler : Handler(Looper.myLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.obj) {
                is Bitmap -> imageView.setImageBitmap(msg.obj as Bitmap)
                is Drawable -> imageView.setImageDrawable(msg.obj as Drawable)
                else -> {
                    Toast.makeText(this@DownloadActivity, "Could not set Image", Toast.LENGTH_SHORT).show()
                }
            }
            end = System.currentTimeMillis()
            printBenchmark()

        }
    }

    //-n "com.stasbar.concurrency/com.stasbar.concurrency.started.StartedServicesActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
    inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent) {

            Log.i("DownloadActivity", cacheDir.absolutePath + " " + DownloadIntentService.FILE_NAME)
            imageView.setImageURI(Uri.parse(File(cacheDir, DownloadIntentService.FILE_NAME).absolutePath))
            end = System.currentTimeMillis()
            printBenchmark()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)
        fabClean.setOnClickListener { imageView.setImageDrawable(null) }
        downloadViaBroadcast.setOnClickListener { initiateDownloadViaBroadcast(); Utils.hideKeyboard(this)  }
        downloadDeferred.setOnClickListener { initiateDeferredDownloadActivity(); Utils.hideKeyboard(this)  }

        downloadViaStartedService.setOnClickListener {
            val downloadImageType = getDownloadImageType(radioGroupImageType.checkedRadioButtonId)
            downloadStartedService(downloadImageType)
            Utils.hideKeyboard(this)
        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(DownloadIntentService.ACTION_COMPLETE)
        registerReceiver(onEvent, intentFilter)
    }

    override fun onPause() {
        unregisterReceiver(onEvent)
        super.onPause()
    }

    fun getDownloadImageType(buttonId: Int): String {
        when (buttonId) {
            R.id.radioButtonBitmap -> return DownloadService.BITMAP
            R.id.radioButtonDrawable -> return DownloadService.DRAWABLE
            else -> throw IllegalStateException()
        }
    }

    private fun downloadStartedService(format: String) {
        start = System.currentTimeMillis()
        val intent = Intent(this, DownloadService::class.java)
        val messenger = Messenger(downloadHandler)
        intent.putExtra(DownloadService.MESSENGER_KEY, messenger)
        intent.putExtra(DownloadService.URL_KEY, editTextUrl.text.toString())
        intent.putExtra(DownloadService.FORMAT_KEY, format)
        startService(intent)

    }


    private fun initiateDownloadViaBroadcast() {
        start = System.currentTimeMillis()
        val intent = Intent(this@DownloadActivity, DownloadIntentService::class.java)
        intent.putExtra(DownloadIntentService.URL_KEY, editTextUrl.text.toString())
        intent.putExtra(DownloadIntentService.PACKAGE_NAME_KEY, packageName)
        startService(intent)
    }

    private fun initiateDeferredDownloadActivity() {
        Log.i("DownloadActivity", "initiateDeferredDownloadActivity")
        //Intent that will start downloadService
        val intent: Intent = Intent(this@DownloadActivity, DeferredDownloadService::class.java)
        intent.putExtra(DownloadIntentService.URL_KEY, editTextUrl.text.toString())
        //Wrap this intent with PendingIntent to stick it with AlarmManager
        val pendingIntent = PendingIntent.getService(this@DownloadActivity, 0, intent, 0)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var deferredTime: Long = 1000L
        try {
            deferredTime = etDeferredTime.text.toString().toLong() * 1000L
            val builder = StringBuilder()
            TimeUtils.formatDuration(deferredTime, builder)
            Toast.makeText(this, "Download will start in " + builder.toString(), Toast.LENGTH_SHORT).show()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
        val downloadTime = System.currentTimeMillis() + deferredTime
        //Shedule pendingIntent to be fired in 3sec
        alarmManager.set(AlarmManager.RTC_WAKEUP, downloadTime, pendingIntent)
    }


    @SuppressLint("RestrictedApi")
    private fun printBenchmark() {
        val time = end - start
        val stringBuilder = StringBuilder()
        TimeUtils.formatDuration(time, stringBuilder)
        Toast.makeText(this@DownloadActivity, stringBuilder.toString(), Toast.LENGTH_SHORT).show()

    }
}


