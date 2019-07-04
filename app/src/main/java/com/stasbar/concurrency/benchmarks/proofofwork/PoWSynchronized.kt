package com.stasbar.concurrency.benchmarks.proofofwork

import com.stasbar.concurrency.benchmarks.MiningResult
import com.stasbar.concurrency.benchmarks.calculateHashOf

@ExperimentalUnsignedTypes
class PoWSynchronized(val difficulty: UInt, val onComplete: (MiningResult) -> Unit) {

    fun execute() {
        val target = String(CharArray(difficulty.toInt())).replace('\u0000', '0')
        val startTime = System.currentTimeMillis()
        var result: MiningResult? = null
        for (testNonce in ULongRange(0u, ULong.MAX_VALUE)) {
            val hash = calculateHashOf("stasbar", testNonce)
            if (hash.substring(0, difficulty.toInt()) == target) {
                result = MiningResult.Success(0, hash, System.currentTimeMillis() - startTime)
                break
            }
        }
        onComplete(result ?: MiningResult.NotFound(0))
    }
}