package com.stasbar.concurrency

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_view_image.*
import java.io.File

class ViewImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)
        Log.i("ViewImageActivity", "onCreate")
        imageView.setImageURI(Uri.parse(File(cacheDir, DeferredDownloadService.FILE_NAME).absolutePath))
    }
}
