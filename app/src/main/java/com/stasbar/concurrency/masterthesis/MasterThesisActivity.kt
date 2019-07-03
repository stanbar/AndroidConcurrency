package com.stasbar.concurrency.masterthesis

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.stasbar.concurrency.R
import com.stasbar.concurrency.masterthesis.widget.ProgressView
import kotlinx.android.synthetic.main.activity_proof_of_work.*
import kotlinx.android.synthetic.main.content_proof_of_work.*
import java.text.NumberFormat

enum class ProcessingMethod(@IdRes val radioButtonId: Int) {
    SYNCHRONIZED(R.id.rbSynchronized) {
        override fun process(algorithm: Algorithm, difficulty: Int, poolSize: UInt, jobSize: Int): MiningResult {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    },
    THREADS(R.id.rbThreads) {
        override fun process(difficulty: Int, poolSize: UInt, jobSize: Int): MiningResult {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    },
    ASYNCTASKS(R.id.rbAsyncTask) {
        override fun process(algorithm: Algorithm, difficulty: Int, poolSize: UInt, jobSize: Int): MiningResult {

        }
    },
    COROUTINES(R.id.rbCoroutines) {
        override fun process(difficulty: Int, poolSize: UInt, jobSize: Int): MiningResult {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    };

    abstract fun process(algorithm: Algorithm, difficulty: Int, poolSize: UInt, jobSize: Int): MiningResult

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
            val algorithm = Algorithm.forButtonId(radioGroupMethod.checkedRadioButtonId)

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
        algorithm.processOn(method, difficulty, poolSize, jobSize, { update ->
            val (id, currentNonce, searchLength) = update

            val progress = (currentNonce.toDouble() / searchLength.toDouble() * 100).toInt()
            progressViews[id].progressBar.progress = progress
            progressViews[id].tvCounter.text = "$currentNonce/$searchLength[$progress]"
        }, { result ->

            showResults(result, progressViews[result.id])
            // TODO cancel all other
        })
    }


    private var testCounter = 0
    @SuppressLint("SetTextI18n")
    private fun showResults(result: MiningResult, progressView: ProgressView) {
        if (result is MiningResult.Success) {
            val formattedTime = measureFormat.format(result.time)
            println("Block Mined!!! : ${result.hash} in $formattedTime")
            tvHash.text = "[$testCounter] ${result.hash}"
            tvTime.text = "[$testCounter] $formattedTime"

        } else {
            println("Didn't find pow")
            tvHash.text = "[$testCounter] Didn't find PoW"
            tvTime.text = "[$testCounter] Never"
            progressView.alpha = 0.2f
        }
        testCounter++
    }

    companion object {
        val measureFormat = NumberFormat.getNumberInstance()
    }
}
