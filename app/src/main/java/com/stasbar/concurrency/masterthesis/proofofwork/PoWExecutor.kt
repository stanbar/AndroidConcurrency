package com.stasbar.concurrency.masterthesis.proofofwork

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

@ExperimentalUnsignedTypes
abstract class PoWExecutor(
    val difficulty: UInt,
    poolSize: Int,
    val jobSize: Int
) {
    protected val threadFactory = ThreadFactory {
        Thread().apply {
            priority = Thread.MAX_PRIORITY
        }
    }
    protected val threadPool =
        ThreadPoolExecutor(poolSize, poolSize, 0, TimeUnit.MILLISECONDS, LinkedBlockingQueue(), threadFactory)

    protected val calculationsPerWorker = (ULong.MAX_VALUE / 1_000_000_000_000u / jobSize.toUInt())

    abstract fun execute()
    abstract fun cancel()
}