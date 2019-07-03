package com.stasbar.concurrency.masterthesis.proofofwork

import com.stasbar.concurrency.masterthesis.MiningResult
import com.stasbar.concurrency.masterthesis.mine

class PoWSynchronized(val difficulty: UInt, val onComplete: (MiningResult) -> Unit) {

    fun execute() {
        val result = mine("stasbar", difficulty.toInt())
        onComplete(result)
    }
}