package com.stasbar.concurrency.multiprocess

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stasbar.concurrency.R

class ProcessGlobalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process_global)
    }
}
