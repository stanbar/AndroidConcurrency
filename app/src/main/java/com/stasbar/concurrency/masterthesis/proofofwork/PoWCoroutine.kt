package com.stasbar.concurrency.masterthesis.proofofwork

import com.stasbar.concurrency.masterthesis.JobUpdate
import com.stasbar.concurrency.masterthesis.MiningResult
import kotlinx.coroutines.coroutineScope

class PoWCoroutineExecutor(
    val difficulty: UInt,
    poolSize: Int,
    val jobSize: Int,
    val onUpdate: (JobUpdate) -> Unit,
    val onComplete: (MiningResult) -> Unit
) {

    suspend fun execute() = coroutineScope {

    }

}