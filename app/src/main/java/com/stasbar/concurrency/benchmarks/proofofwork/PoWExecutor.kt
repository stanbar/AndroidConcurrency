package com.stasbar.concurrency.benchmarks.proofofwork

import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

@ExperimentalUnsignedTypes
abstract class PoWExecutor(
    val difficulty: UInt,
    poolSize: UInt,
    val jobSize: UInt
) {
    private val threadFactory = ThreadFactory {
        Thread(it).apply {
            priority = Thread.NORM_PRIORITY
        }
    }
    val threadPool =
        Executors.newFixedThreadPool(poolSize.toInt(), threadFactory)

    protected val calculationsPerWorker = (ULong.MAX_VALUE / 1_000_000_000_000u / jobSize.toUInt())
    protected val searchStart: ULong = 0u
    abstract fun execute()
    abstract fun cancel()
}