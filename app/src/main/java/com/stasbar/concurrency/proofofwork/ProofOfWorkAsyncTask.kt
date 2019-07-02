package com.stasbar.concurrency.proofofwork

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import com.stasbar.concurrency.proofofwork.ProofOfWorkActivity.Companion.measureFormat
import kotlin.system.measureTimeMillis

data class PoWParams @ExperimentalUnsignedTypes constructor(
    val searchRange: ULongRange,
    val data: String,
    val difficulty: UInt
)

class PoWAlreadyFoundException : Exception()

@ExperimentalUnsignedTypes
@SuppressLint("StaticFieldLeak")
class ProofOfWorkAsyncTask(
    val name: String,
    private val progressBar: ProgressBar,
    private val textView: TextView,
    val onPowFound: (MiningResult) -> Unit
) :
    AsyncTask<PoWParams, ULong, MiningResult>() {

    private var currentNonce: ULong = 0u
    private var searchLength: ULong = 0u

    lateinit var updater: Thread
    @SuppressLint("SetTextI18n")
    override fun onPreExecute() {
        super.onPreExecute()
        updater = Thread {
            while (!isCancelled) {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    Log.d("Updater", "InterruptedException")
                }
                val progress = (currentNonce.toDouble() / searchLength.toDouble() * 100).toInt()
                progressBar.post { progressBar.progress = progress }
                textView.post { textView.text = "$currentNonce/$searchLength[$progress]" }
            }
        }.apply { start() }
    }

    override fun doInBackground(vararg params: PoWParams): MiningResult {
        val (searchRange, data, difficulty) = params[0]
        searchLength = searchRange.last - searchRange.first
        Log.d("ProofOfWorkAsyncTask", "Thread name: ${Thread.currentThread().name} searchRange: $searchRange")
        var finalHash: String? = null
        val time = try {
            measureTimeMillis {
                val target = String(CharArray(difficulty.toInt())).replace('\u0000', '0')
                currentNonce = 0u
                for (testNonce in searchRange) {
                    // Stop searching if pow is already found
                    if (isCancelled) throw PoWAlreadyFoundException()

                    val hash = calculateHashOf(data, testNonce)
                    if (hash.substring(0, difficulty.toInt()) == target) {
                        finalHash = hash
                        break
                    }

                    currentNonce++
                }
            }
        } catch (e: PoWAlreadyFoundException) {
            return MiningResult.Failure
        }

        return if (finalHash != null) {
            Log.d("PoWAsyncTask$name", "Found PoW: $finalHash in ${measureFormat.format(time)}")
            MiningResult.Success(finalHash!!, time)
        } else {
            Log.d(
                "PoWAsyncTask$name",
                "Didn't found PoW: $finalHash in $searchRange in time ${measureFormat.format(time)}"
            )
            MiningResult.Failure
        }


    }

    override fun onPostExecute(result: MiningResult) {
        super.onPostExecute(result)
        onPowFound(result)
        updater.interrupt()
    }

    override fun onCancelled() {
        super.onCancelled()
        Log.d("PoWAsyncTask$name", " Cancelled !")
    }
}