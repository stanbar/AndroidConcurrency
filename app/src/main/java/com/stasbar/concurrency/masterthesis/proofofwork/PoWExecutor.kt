package com.stasbar.concurrency.masterthesis.proofofwork

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

@ExperimentalUnsignedTypes
abstract class PoWExecutor(
    val difficulty: UInt,
    poolSize: UInt,
    val jobSize: UInt
) {
    protected val threadFactory = ThreadFactory {
        Thread().apply {
            priority = Thread.MAX_PRIORITY
        }
    }
    protected val threadPool =
        ThreadPoolExecutor(
            poolSize.toInt(),
            poolSize.toInt(),
            0,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue(),
            threadFactory
        )

    protected val calculationsPerWorker = (ULong.MAX_VALUE / 1_000_000_000_000u / jobSize.toUInt())

    abstract fun execute()
    abstract fun cancel()
}