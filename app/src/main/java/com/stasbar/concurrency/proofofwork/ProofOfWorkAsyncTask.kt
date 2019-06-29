package com.stasbar.concurrency.proofofwork

import android.os.AsyncTask
import android.util.Log
import com.stasbar.concurrency.proofofwork.ProofOfWorkActivity.Companion.measureFormat
import java.util.*
import kotlin.system.measureTimeMillis

data class PoWParams(val searchRange: LongRange, val data: String, val difficulty: Int)

class PoWAlreadyFoundException : Exception()

class ProofOfWorkAsyncTask(val onPowFound: (MiningResult) -> Unit) : AsyncTask<PoWParams, Long, MiningResult>() {

    override fun doInBackground(vararg params: PoWParams): MiningResult {
        val (searchRange, block, difficulty) = params[0]

        var finalHash: String? = null
        val time = try {
            measureTimeMillis {
                val target = String(CharArray(difficulty)).replace('\u0000', '0')
                for (testNonce in searchRange) {
                    // Stop searching if pow is already found
                    if (isCancelled) throw PoWAlreadyFoundException()

                    val hash = calculateHashOf(block, testNonce)
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
            Log.d("ProofOfWorkAsyncTask", "Found PoW: $finalHash in ${measureFormat.format(Date(time))}")
            MiningResult.Success(finalHash!!, time)
        } else {
            Log.d(
                "ProofOfWorkAsyncTask",
                "Didn't found PoW: $finalHash in $searchRange in time ${measureFormat.format(Date(time))}"
            )
            MiningResult.Failure
        }


    }

    override fun onPostExecute(result: MiningResult) {
        super.onPostExecute(result)
        onPowFound(result)
    }
}