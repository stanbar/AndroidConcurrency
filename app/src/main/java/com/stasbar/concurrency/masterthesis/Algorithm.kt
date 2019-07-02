package com.stasbar.concurrency.masterthesis

import android.os.AsyncTask
import androidx.annotation.IdRes
import com.stasbar.concurrency.R
import com.stasbar.concurrency.masterthesis.proofofwork.PoWParams
import com.stasbar.concurrency.masterthesis.proofofwork.PoWThread
import com.stasbar.concurrency.masterthesis.proofofwork.ProofOfWorkAsyncTask
import kotlinx.coroutines.Job

enum class Algorithm(val radioButtonId: Int) {
    ProofOfWork(R.id.rbProofOfWork) {
        override fun processOn(
            processingMethod: ProcessingMethod,
            params: AsyncParams,
            onUpdate: (JobUpdate) -> Unit,
            onComplete: (MiningResult) -> Unit
        ) {
            val (searchRange, data, difficulty) = params as PoWParams

            when (processingMethod) {
                ProcessingMethod.SYNCHRONIZED -> {
                }
                ProcessingMethod.THREADS -> {
                }
                ProcessingMethod.ASYNCTASKS -> {
                }
            }


        }

        @ExperimentalUnsignedTypes
        override fun createAsyncTask(
            id: String,
            params: AsyncParams,
            onUpdate: (JobUpdate) -> Unit,
            onComplete: (MiningResult) -> Unit
        ) = ProofOfWorkAsyncTask(
            id,
            params as PoWParams,
            onUpdate,
            onComplete
        )

        @ExperimentalUnsignedTypes
        override fun createThread(
            id: String,
            params: AsyncParams,
            onUpdate: (JobUpdate) -> Unit,
            onComplete: (MiningResult) -> Unit
        ): Thread {
            return PoWThread(
                id,
                params as PoWParams,
                onUpdate,
                onComplete
            )
        }

        override fun createCoroutine(): Job {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun start(difficulty: Long) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    },
    SuperPi(R.id.rbSuperPi) {
        override fun start(difficulty: Long) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    },
    Sorting(R.id.rbSorting) {
        override fun start(difficulty: Long) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    };

    abstract fun processOn(
        method: ProcessingMethod, params: AsyncParams, onUpdate: (JobUpdate) -> Unit,
        onComplete: (MiningResult) -> Unit
    )

    abstract fun start(difficulty: Long)

    abstract fun createAsyncTask(
        id: String, params: AsyncParams, onUpdate: (JobUpdate) -> Unit,
        onComplete: (MiningResult) -> Unit
    ): AsyncTask<*, *, *>

    abstract fun createThread(
        id: String, params: AsyncParams, onUpdate: (JobUpdate) -> Unit,
        onComplete: (MiningResult) -> Unit
    ): Thread

    abstract fun createCoroutine(): Job


    companion object {
        fun forButtonId(@IdRes radioButtonId: Int) =
            values().find { it.radioButtonId == radioButtonId } ?: error("No matching method")
    }
}