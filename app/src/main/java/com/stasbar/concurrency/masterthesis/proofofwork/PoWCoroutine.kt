package com.stasbar.concurrency.masterthesis.proofofwork

import android.util.Log
import com.stasbar.concurrency.masterthesis.JobUpdate
import com.stasbar.concurrency.masterthesis.MiningResult
import com.stasbar.concurrency.masterthesis.ProofOfWorkActivity
import com.stasbar.concurrency.masterthesis.calculateHashOf
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

@ExperimentalUnsignedTypes
class PoWCoroutineExecutor(
    difficulty: UInt,
    poolSize: UInt,
    jobSize: UInt,
    val onUpdate: (JobUpdate) -> Unit,
    val onComplete: (MiningResult) -> Unit
) : PoWExecutor(difficulty, poolSize, jobSize) {


    private val executorCoroutineDispatcher = threadPool.asCoroutineDispatcher()

    val parent = SupervisorJob()

    override fun execute() = runBlocking<Unit>(parent) {
        var from = ULong.MIN_VALUE

        List(jobSize) {
            worker(executorCoroutineDispatcher, it,
                PoWParams(ULongRange(from, from + calculationsPerWorker), "stasbar", difficulty),
                onUpdate,
                { result ->
                    onComplete(result)
                    parent.cancel()
                })

            from += calculationsPerWorker
        }
    }

    override fun cancel() {
        parent.cancel()
    }

    private fun CoroutineScope.worker(
        context: ExecutorCoroutineDispatcher,
        id: Int,
        arguments: PoWParams,
        onUpdate: (JobUpdate) -> Unit,
        onComplete: (MiningResult) -> Unit
    ) = launch(context) {

        val (searchRange, data, difficulty) = arguments
        val searchLength = searchRange.last - searchRange.first

        val updateChannel = Channel<Unit>(Channel.CONFLATED)
        updater(id, searchLength, onUpdate, updateChannel)
        Log.d("PoWCoroutine-$id", "Thread name: ${Thread.currentThread().name} searchRange: $searchRange")

        var finalHash: String? = null
        try {
            val time = measureTimeMillis {
                val target = String(CharArray(difficulty.toInt())).replace('\u0000', '0')
                for (testNonce in searchRange) {
                    // Stop searching if pow is already found
                    if (!isActive) throw PoWAlreadyFoundException()

                    val hash = calculateHashOf(data, testNonce)
                    if (hash.substring(0, difficulty.toInt()) == target) {
                        finalHash = hash
                        break
                    }
                    updateChannel.offer(Unit)
                }
            }

            val result = if (finalHash != null) {
                Log.d("PoWCoroutine-$id", "Found PoW: $finalHash in ${ProofOfWorkActivity.measureFormat.format(time)}")
                MiningResult.Success(finalHash!!, time)
            } else {
                Log.d(
                    "PoWCoroutine-$id",
                    "Didn't found PoW: $finalHash in $searchRange in time ${ProofOfWorkActivity.measureFormat.format(
                        time
                    )}"
                )
                MiningResult.NotFound
            }
            onComplete(result)
        } catch (e: PoWAlreadyFoundException) {
            onComplete(MiningResult.Cancelled)
        }
    }

    private fun CoroutineScope.updater(
        id: Int,
        searchLength: ULong = 0u,
        onUpdate: (JobUpdate) -> Unit,
        receiveChannel: ReceiveChannel<Unit>
    ) = launch(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
        var currentNonce: ULong = 0u
        for (increment in receiveChannel) {
            currentNonce++
            onUpdate(JobUpdate(id, currentNonce, searchLength))
        }
    }
}