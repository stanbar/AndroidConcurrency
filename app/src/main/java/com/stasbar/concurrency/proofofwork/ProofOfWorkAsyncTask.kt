package com.stasbar.concurrency.proofofwork

import android.os.AsyncTask
import android.util.Log
import com.stasbar.concurrency.proofofwork.ProofOfWorkActivity.Companion.measureFormat
import java.util.*
import kotlin.system.measureTimeMillis

data class PoWParams(val searchRange: LongRange, val data: String, val difficulty: Int)

class PoWAlreadyFoundException : Exception()

class ProofOfWorkAsyncTask(val name: String, val onPowFound: (MiningResult) -> Unit) :
    AsyncTask<PoWParams, Long, MiningResult>() {

    override fun doInBackground(vararg params: PoWParams): MiningResult {
        val (searchRange, data, difficulty) = params[0]
        Log.d("ProofOfWorkAsyncTask", "Thread name: ${Thread.currentThread().name} searchRange: $searchRange")
        var finalHash: String? = null
        val time = try {
            measureTimeMillis {
                val target = String(CharArray(difficulty)).replace('\u0000', '0')
                for (testNonce in searchRange) {
                    // Stop searching if pow is already found
                    if (isCancelled) throw PoWAlreadyFoundException()

                    val hash = calculateHashOf(data, testNonce)
                    if (hash.substring(0, difficulty) == target) {
                        finalHash = hash
                        break
                    }
                }
            }
        } catch (e: PoWAlreadyFoundException) {
            return MiningResult.Failure
        }

        return if (finalHash != null) {
            Log.d("PoWAsyncTask$name", "Found PoW: $finalHash in ${measureFormat.format(Date(time))}")
            MiningResult.Success(finalHash!!, time)
        } else {
            Log.d(
                "PoWAsyncTask$name",
                "Didn't found PoW: $finalHash in $searchRange in time ${measureFormat.format(Date(time))}"
            )
            MiningResult.Failure
        }


    }

    override fun onPostExecute(result: MiningResult) {
        super.onPostExecute(result)
        onPowFound(result)
    }

    override fun onCancelled() {
        super.onCancelled()
        Log.d("PoWAsyncTask$name", " Cancelled !")
    }
}