package com.stasbar.concurrency.masterthesis

import androidx.annotation.IdRes
import com.stasbar.concurrency.R
import com.stasbar.concurrency.masterthesis.proofofwork.PoWAsyncTaskExecutor
import com.stasbar.concurrency.masterthesis.proofofwork.PoWCoroutineExecutor
import com.stasbar.concurrency.masterthesis.proofofwork.PoWSynchronized
import com.stasbar.concurrency.masterthesis.proofofwork.PoWThreadExecutor

enum class Algorithm(val radioButtonId: Int) {
    ProofOfWork(R.id.rbProofOfWork) {
        @ExperimentalUnsignedTypes
        override fun processOn(
            method: ProcessingMethod,
            difficulty: UInt,
            poolSize: UInt,
            jobSize: UInt,
            onUpdate: (JobUpdate) -> Unit,
            onComplete: (MiningResult) -> Unit
        ) = when (method) {
            ProcessingMethod.SYNCHRONIZED ->
                PoWSynchronized(difficulty, onComplete).execute()

            ProcessingMethod.THREADS ->
                PoWThreadExecutor(difficulty, poolSize, jobSize, onUpdate, onComplete).execute()

            ProcessingMethod.ASYNCTASKS ->
                PoWAsyncTaskExecutor(difficulty, poolSize, jobSize, onUpdate, onComplete).execute()

            ProcessingMethod.COROUTINES ->
                PoWCoroutineExecutor(difficulty, poolSize, jobSize, onUpdate, onComplete).execute()
        }
    }
//    ,
//    SuperPi(R.id.rbSuperPi) {
//    },
//    Sorting(R.id.rbSorting) {
//    }
    ;

    @ExperimentalUnsignedTypes
    abstract fun processOn(
        method: ProcessingMethod,
        difficulty: UInt,
        poolSize: UInt,
        jobSize: UInt,
        onUpdate: (JobUpdate) -> Unit,
        onComplete: (MiningResult) -> Unit
    )

    companion object {
        fun forButtonId(@IdRes radioButtonId: Int) =
            values().find { it.radioButtonId == radioButtonId } ?: error("No matching method")
    }
}