package com.stasbar.concurrency.proofofwork

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;
import com.stasbar.concurrency.R

import kotlinx.android.synthetic.main.activity_proof_of_work.*
import kotlinx.android.synthetic.main.content_proof_of_work.*
import java.text.SimpleDateFormat
import java.util.*

class ProofOfWorkActivity : AppCompatActivity() {


    var asyncTasks: List<ProofOfWorkAsyncTask> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proof_of_work)
        setSupportActionBar(toolbar)

        btnMineSync.setOnClickListener {
            val difficulty = etDifficulty.text.toString().toInt()
            val result = mine("Hello", difficulty)
            showResults(result)
        }

        btnStartAsync.setOnClickListener {
            val difficulty = etDifficulty.text.toString().toInt()
            val workersSize = etWorkers.text.toString().toInt()
            asyncTasks = List(workersSize) {
                ProofOfWorkAsyncTask(it.toString()) { results ->
                    showResults(results)
                    asyncTasks.forEach { task -> task.cancel(true) }
                }
            }

            val from = Long.MIN_VALUE
            val perWorker = (Long.MAX_VALUE / workersSize) * 2
            asyncTasks.forEach {
                it.execute(PoWParams(LongRange(from, from + perWorker), "Hello", difficulty))
            }
        }
        btnStop.setOnClickListener {
            asyncTasks.forEach { it.cancel(true) }
        }
    }

    var testCounter = 0
    private fun showResults(result: MiningResult) {
        if (result is MiningResult.Success) {
            val formatedTime = measureFormat.format(Date(result.time))
            println("Block Mined!!! : ${result.hash} in $formatedTime")
            tvHash.text = "[$testCounter] ${result.hash}"
            tvTime.text = "[$testCounter] ${formatedTime}"
        } else {
            println("Didn't find pow")
            tvHash.text = "[$testCounter] Didn't find PoW"
            tvTime.text = "[$testCounter] Never"
        }
        testCounter++
    }

    companion object {
        val measureFormat = SimpleDateFormat("mm:ss.SSS")
    }
}
