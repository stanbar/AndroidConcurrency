package com.stasbar.concurrency.masterthesis

import android.os.AsyncTask
import com.stasbar.concurrency.masterthesis.proofofwork.JobUpdate
import com.stasbar.concurrency.masterthesis.proofofwork.PoWParams
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

@ExperimentalUnsignedTypes
class ProcessingAsyncTask(
    val algorithm: Algorithm,
    difficulty: UInt,
    poolSize: Int,
    jobs: Int,
    onUpdate: (JobUpdate) -> Unit,
    onComplete: (MiningResult) -> Unit
) {
    init {
        val threadFactory = ThreadFactory {
            Thread().apply {
                priority = Thread.MAX_PRIORITY
            }
        }
        val threadPool =
            ThreadPoolExecutor(poolSize, poolSize, 0, TimeUnit.MILLISECONDS, LinkedBlockingQueue(), threadFactory)

        val calculationsPerWorker = (ULong.MAX_VALUE / 1_000_000_000_000u / jobs.toUInt())
        var from = ULong.MIN_VALUE

        val asyncTasks = mutableListOf<AsyncTask<*, *, *>>()
        repeat(jobs) {
            val task = algorithm.createAsyncTask(
                it.toString(),
                PoWParams(
                    ULongRange(
                        from,
                        from + calculationsPerWorker
                    ), "stasbar", difficulty
                ),
                onUpdate,
                { result ->
                    onComplete(result)
                    asyncTasks.forEach { asyncTask -> asyncTask.cancel(true) }
                })
            asyncTasks.add(task)
            task.executeOnExecutor(threadPool)
            from += calculationsPerWorker
        }
    }
}


