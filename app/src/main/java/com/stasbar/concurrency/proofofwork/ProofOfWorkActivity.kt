package com.stasbar.concurrency.proofofwork

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.stasbar.concurrency.R
import kotlinx.android.synthetic.main.activity_proof_of_work.*
import kotlinx.android.synthetic.main.content_proof_of_work.*
import org.jetbrains.anko.toast
import java.text.NumberFormat

@ExperimentalUnsignedTypes
class ProofOfWorkActivity : AppCompatActivity() {

    private var asyncTasks: List<ProofOfWorkAsyncTask> = listOf()


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proof_of_work)
        setSupportActionBar(toolbar)
        tvCpuSize.text = "Available CPUs: ${Runtime.getRuntime().availableProcessors()}"
        btnMineSync.setOnClickListener {
            val difficulty = etDifficulty.text.toString().toInt()
            Thread {
                progressContainer.removeAllViews()
                val progressBar = ProgressBar(this).apply {
                    isIndeterminate = false
                    max = 100
                    layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                }
                progressContainer.addView(progressBar)
                val result = mine("Hello", difficulty)
                etDifficulty.post { showResults(result, progressBar) }
            }.start()

        }

        btnStartAsync.setOnClickListener {
            progressContainer.removeAllViews()
            val difficulty = etDifficulty.text.toString().toUInt()
            val workersSize = etWorkers.text.toString().toUInt()
            createAsyncTasks(workersSize)
            startAsyncTasks(workersSize, difficulty)
        }
        btnStop.setOnClickListener {
            asyncTasks.forEach { it.cancel(true) }
            progressContainer.removeAllViews()
        }
    }

    private fun startAsyncTasks(workersSize: UInt, difficulty: UInt) {
        var from = ULong.MIN_VALUE
        val perWorker = (ULong.MAX_VALUE / 1_000_000_000_000u / workersSize.toUInt())
        toast("Range perWorker: $perWorker")
        asyncTasks.forEach {
            it.executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR,
                PoWParams(ULongRange(from, from + perWorker), "Hello", difficulty)
            )
            from += perWorker
        }
    }

    private fun createAsyncTasks(workersSize: UInt) {
        asyncTasks = List(workersSize.toInt()) {
            val container =
                layoutInflater.inflate(R.layout.vertical_progress_bar, progressContainer, false) as LinearLayout
            val tvCounter = container.findViewById<TextView>(R.id.tvCounter)
            val progressBar = container.findViewById<ProgressBar>(R.id.progressBar)

            progressContainer.addView(container)

            ProofOfWorkAsyncTask(it.toString(), progressBar, tvCounter)
            { results ->
                showResults(results, progressBar)
                asyncTasks.forEach { task -> task.cancel(true) }
            }
        }
    }

    private var testCounter = 0
    @SuppressLint("SetTextI18n")
    private fun showResults(result: MiningResult, progressBar: ProgressBar) {
        if (result is MiningResult.Success) {
            val formattedTime = measureFormat.format(result.time)
            println("Block Mined!!! : ${result.hash} in $formattedTime")
            tvHash.text = "[$testCounter] ${result.hash}"
            tvTime.text = "[$testCounter] $formattedTime"

        } else {
            println("Didn't find pow")
            tvHash.text = "[$testCounter] Didn't find PoW"
            tvTime.text = "[$testCounter] Never"
            progressBar.alpha = 0.2f
        }
        testCounter++
    }

    companion object {
        val measureFormat = NumberFormat.getNumberInstance()
    }
}
