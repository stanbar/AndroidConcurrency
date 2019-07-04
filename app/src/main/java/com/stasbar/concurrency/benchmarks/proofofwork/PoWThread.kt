package com.stasbar.concurrency.benchmarks.proofofwork

import com.stasbar.concurrency.benchmarks.*
import timber.log.Timber
import kotlin.system.measureTimeMillis

@ExperimentalUnsignedTypes
class PoWThreadExecutor(
    difficulty: UInt,
    poolSize: UInt,
    jobSize: UInt,
    val onUpdate: (JobUpdate) -> Unit,
    val onComplete: (MiningResult) -> Unit
) : PoWExecutor(difficulty, poolSize, jobSize) {

    val tasks = mutableListOf<Runnable>()

    init {
        var from = ULong.MIN_VALUE
        repeat(jobSize.toInt()) {
            val task = PoWThread(
                it,
                PoWParams(ULongRange(from, from + calculationsPerWorker), "stasbar", difficulty),
                onUpdate,
                { result ->
                    onComplete(result)
                    if (result is MiningResult.Success) cancel()
                })
            tasks.add(task)
            from += calculationsPerWorker
        }
    }

    override fun execute() {
        tasks.forEach { threadPool.submit(it) }
    }

    override fun cancel() {
        threadPool.shutdownNow()
    }
}

@ExperimentalUnsignedTypes
class PoWThread(
    val id: Int,
    val arguments: PoWParams,
    val onUpdate: (JobUpdate) -> Unit,
    val onComplete: (MiningResult) -> Unit
) : Runnable {

    //TODO include in analisis
    private lateinit var updater: Updater

    override fun run() {
        val (searchRange, data, difficulty) = arguments
        val searchLength = searchRange.last - searchRange.first
        updater = Updater(id, searchLength, onUpdate)
        updater.start()

        Timber.d("Thread name: ${Thread.currentThread().name} searchRange: $searchRange")
        var finalHash: String? = null
        val time = try {
            measureTimeMillis {
                val target = String(CharArray(difficulty.toInt())).replace('\u0000', '0')
                for (testNonce in searchRange) {
                    // Stop searching if pow is already found
                    if (Thread.currentThread().isInterrupted) throw PoWAlreadyFoundException()

                    val hash = calculateHashOf(data, testNonce)
                    if (hash.substring(0, difficulty.toInt()) == target) {
                        finalHash = hash
                        break
                    }
                    updater.incNonce()
                }
            }
        } catch (e: PoWAlreadyFoundException) {
            onComplete(MiningResult.Cancelled(id))
            Timber.e(e, "[${Thread.currentThread().name}] cancelled thread work")
            return
        }

        val result = if (finalHash != null) {
            Timber.d(
                "[${Thread.currentThread().name}] Found PoW: $finalHash in ${ProofOfWorkActivity.measureFormat.format(
                    time
                )}"
            )
            MiningResult.Success(id, finalHash!!, time)
        } else {
            Timber.d(
                "[${Thread.currentThread().name}] Didn't found PoW: $finalHash in $searchRange in time ${ProofOfWorkActivity.measureFormat.format(
                    time
                )}"
            )
            MiningResult.NotFound(id)
        }

        onComplete(result)
    }

}