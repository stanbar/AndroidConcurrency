package com.stasbar.concurrency.benchmarks

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.stasbar.concurrency.R
import com.stasbar.concurrency.benchmarks.widget.ProgressView
import kotlinx.android.synthetic.main.activity_proof_of_work.*
import kotlinx.android.synthetic.main.content_proof_of_work.*
import java.text.NumberFormat

enum class ProcessingMethod(@IdRes val radioButtonId: Int) {
    SYNCHRONIZED(R.id.rbSynchronized),
    THREADS(R.id.rbThreads),
    ASYNCTASKS(R.id.rbAsyncTask),
    COROUTINES(R.id.rbCoroutines);

    companion object {
        fun forButtonId(@IdRes radioButtonId: Int) =
            values().find { it.radioButtonId == radioButtonId } ?: error("No matching method")
    }
}

@ExperimentalUnsignedTypes
class ProofOfWorkActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proof_of_work)
        setSupportActionBar(toolbar)
        tvCpuSize.text = "Available CPUs: ${Runtime.getRuntime().availableProcessors()}"

        btnStart.setOnClickListener {
            val method = ProcessingMethod.forButtonId(radioGroupMethod.checkedRadioButtonId)
            val algorithm = Algorithm.forButtonId(radioGroupAlgorithm.checkedRadioButtonId)

            val difficulty = etDifficulty.text.toString().toUInt()
            val poolSize = etPoolSize.text.toString().toUInt()
            val jobSize = etJobSize.text.toString().toUInt()

            startProcessing(method, algorithm, difficulty, poolSize, jobSize)
        }
    }

    private fun startProcessing(
        method: ProcessingMethod,
        algorithm: Algorithm,
        difficulty: UInt,
        poolSize: UInt,
        jobSize: UInt
    ) {
        progressContainer.removeAllViews()
        val progressViews = List(jobSize.toInt()) {
            ProgressView(this)
        }
        progressViews.forEach {
            progressContainer.addView(it)
        }
        algorithm.processOn(method, difficulty, poolSize, jobSize, { update ->
            val (id, currentNonce, searchLength) = update

            val progress = (currentNonce.toDouble() / searchLength.toDouble() * 100).toInt()
            progressViews[id].progressBar.progress = progress
            progressViews[id].tvCounter.text = "$currentNonce/$searchLength[$progress]"
        }, { result ->
            showResults(result, progressViews[result.id])
        })
    }


    @SuppressLint("SetTextI18n")
    private fun showResults(result: MiningResult, progressView: ProgressView) {
        if (result is MiningResult.Success) {
            val formattedTime = measureFormat.format(result.time)
            println("Block Mined!!! : ${result.hash} in $formattedTime")
            tvHash.text = "${result.hash}"
            tvTime.text = "$formattedTime"

        } else {
            println("Didn't find pow")
            tvHash.text = "Didn't find PoW"
            tvTime.text = "Never"
            progressView.alpha = 0.2f
        }
    }

    companion object {
        val measureFormat = NumberFormat.getNumberInstance()
    }
}
