package com.stasbar.concurrency.masterthesis

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.stasbar.concurrency.R
import com.stasbar.concurrency.masterthesis.proofofwork.PoWParams
import com.stasbar.concurrency.masterthesis.proofofwork.ProofOfWorkAsyncTask
import com.stasbar.concurrency.masterthesis.widget.ProgressView
import kotlinx.android.synthetic.main.activity_proof_of_work.*
import kotlinx.android.synthetic.main.content_proof_of_work.*
import org.jetbrains.anko.toast
import java.text.NumberFormat

enum class ProcessingMethod(@IdRes val radioButtonId: Int) {
    SYNCHRONIZED(R.id.rbSynchronized) {

        override fun process(difficulty: Int, poolSize: Int, jobSize: Int) = mine("Hello", difficulty)

    },
    THREADS(R.id.rbThreads) {
        override fun process(difficulty: Int, poolSize: Int, jobSize: Int): MiningResult {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    },
    ASYNCTASKS(R.id.rbAsyncTask) {
        override fun process(algorithm: Algorithm, difficulty: Int, poolSize: Int, jobSize: Int): MiningResult {

        }
    },
    COROUTINES(R.id.rbCoroutines) {
        override fun process(difficulty: Int, poolSize: Int, jobSize: Int): MiningResult {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    };

    abstract fun process(algorithm: Algorithm, difficulty: Int, poolSize: Int, jobSize: Int): MiningResult

    companion object {
        fun forButtonId(@IdRes radioButtonId: Int) =
            values().find { it.radioButtonId == radioButtonId } ?: error("No matching method")
    }
}

@ExperimentalUnsignedTypes
class ProofOfWorkActivity : AppCompatActivity() {

    private var asyncTasks: List<ProofOfWorkAsyncTask> = listOf()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proof_of_work)
        setSupportActionBar(toolbar)
        tvCpuSize.text = "Available CPUs: ${Runtime.getRuntime().availableProcessors()}"

        btnStart.setOnClickListener {
            val method = ProcessingMethod.forButtonId(radioGroupMethod.checkedRadioButtonId)
            val algorithm = Algorithm.forButtonId(radioGroupMethod.checkedRadioButtonId)

            val difficulty = etDifficulty.text.toString().toInt()
            val poolSize = etPoolSize.text.toString().toInt()
            val jobSize = etJobSize.text.toString().toInt()

            startProcessing(method, algorithm, difficulty, poolSize, jobSize)
        }

        btnStart.setOnClickListener {
            progressContainer.removeAllViews()
            val difficulty = etDifficulty.text.toString().toUInt()
            val workersSize = etJobSize.text.toString().toUInt()
            createAsyncTasks(workersSize)
            startAsyncTasks(workersSize, difficulty)
        }
        btnStop.setOnClickListener {
            asyncTasks.forEach { it.cancel(true) }
            progressContainer.removeAllViews()
        }
    }

    private fun startProcessing(
        method: ProcessingMethod,
        algorithm: Algorithm,
        difficulty: Int,
        poolSize: Int,
        jobSize: Int
    ) {
        progressContainer.removeAllViews()

        method.process(algorithm, difficulty, poolSize, jobSize)
        algorithm.createAsyncTask()
        method.process(difficulty, poolSize, jobSize)

    }

    private fun startAsyncTasks(workersSize: UInt, difficulty: UInt) {
        var from = ULong.MIN_VALUE
        val perWorker = (ULong.MAX_VALUE / 1_000_000_000_000u / workersSize.toUInt())
        toast("Range perWorker: $perWorker")
        asyncTasks.forEach {
            it.executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR,
                PoWParams(
                    ULongRange(from, from + perWorker),
                    "Hello",
                    difficulty
                )
            )
            from += perWorker
        }
    }

    private fun createAsyncTasks(workersSize: UInt) {
        asyncTasks = List(workersSize.toInt()) {

            val progressView = ProgressView(this)
            val container =
                layoutInflater.inflate(R.layout.progress_view, progressContainer, false) as LinearLayout


            progressContainer.addView(container)

            ProofOfWorkAsyncTask(it.toString(), { update ->
                progressView.progressBar.progress = update.progressView.tvCounter
            }, { result ->

            })
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
