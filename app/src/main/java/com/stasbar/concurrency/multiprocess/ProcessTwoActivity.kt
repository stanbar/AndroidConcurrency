package com.stasbar.concurrency.multiprocess

import android.os.Bundle
import android.os.Process
import androidx.appcompat.app.AppCompatActivity
import com.stasbar.concurrency.R
import kotlinx.android.synthetic.main.activity_process_two.*

class ProcessTwoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_process_two)
        tvProcessName.text = "my PID: ${Process.myPid()}"
        btnKillMyself.setOnClickListener {
            Process.killProcess(Process.myPid())
        }
        btnKillProcessPid.setOnClickListener {
            val pidToKill = etProcessPid.text.toString().toInt()
            Process.killProcess(pidToKill)
        }
    }
}
