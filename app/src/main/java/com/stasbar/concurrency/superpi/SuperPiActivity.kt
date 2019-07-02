package com.stasbar.concurrency.superpi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stasbar.concurrency.R
import kotlinx.android.synthetic.main.activity_super_pi.*
import java.text.NumberFormat

class SuperPiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_super_pi)

        btnStartSuperPi.setOnClickListener {
            val threads = etThreads.text.toString().toLong()
            val iteraions = etIterations.text.toString().toLong()
            val start = System.currentTimeMillis()
            PiStresser(threads, iteraions).start()
            tvTime.text = "${measureFormat.format(System.currentTimeMillis() - start)} ms"

        }
    }

    companion object {
        val measureFormat = NumberFormat.getNumberInstance()
    }
}
