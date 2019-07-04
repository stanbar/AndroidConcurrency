package com.stasbar.concurrency.benchmarks.proofofwork

import com.stasbar.concurrency.benchmarks.MiningResult
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldHaveSize
import org.junit.Test
import java.util.concurrent.CountDownLatch

class PoWThreadExecutorTest {

    @Test
    fun `run single asks in executor`() {
        val lock = CountDownLatch(1)
        val executor = PoWThreadExecutor(4u, 4u, 8u, { update ->
            println("update ${update.id} ${update.currentNonce}/${update.searchLength}")
        }, { result ->
            result shouldBeInstanceOf MiningResult.Success::class
            result.id shouldEqual 0
            result as MiningResult.Success
            result.hash shouldEqual "00008c00d20c8f51d50d5a20283875dacda939fe586a748abf3a14df888d11b6"
            println("result: ${result.id} ${result.hash} ${result.time}")
            lock.countDown()
        })
        executor.tasks shouldHaveSize 8

        executor.threadPool.submit(executor.tasks[0])
        lock.await()
    }

    @Test
    fun `run all tasks in executor`() {
        val poolSize = 4u
        val jobSize = 8u
        val successLock = CountDownLatch(1)
        val cancelledLock = CountDownLatch(poolSize.toInt() - 1)

        PoWThreadExecutor(4u, poolSize, jobSize,
            { update ->
                println("update ${update.id} ${update.currentNonce}/${update.searchLength}")
            }, { result ->
                when (result) {
                    is MiningResult.Success -> {
                        println("result success: ${result.id} ${result.hash} ${result.time}")
                        TODO("Handle case when 2 thread find solution is 'the same time'")
                        successLock.countDown()
                    }
                    is MiningResult.Cancelled -> {
                        println("result cancelled: ${result.id}")
                        cancelledLock.countDown()
                    }
                    is MiningResult.NotFound -> error("This should not happend")
                }
            }).execute()

        successLock.await()
        cancelledLock.await()
    }

}