package com.stasbar.concurrency.benchmarks.proofofwork

import com.stasbar.concurrency.benchmarks.MiningResult
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.junit.Test
import java.util.concurrent.CountDownLatch

class PoWThreadTest {

    @Test
    fun `run thread run`() {
        val lock = CountDownLatch(1)
        val searchRange = ULongRange(0u, ULong.MAX_VALUE)
        val runnable = PoWThread(0, PoWParams(searchRange, "stasbar", 4u),
            { update ->
                println("update ${update.id} ${update.currentNonce}/${update.searchLength}")
            },
            { result ->
                result shouldBeInstanceOf MiningResult.Success::class
                result.id shouldEqual 0
                result as MiningResult.Success
                result.hash shouldEqual "00008c00d20c8f51d50d5a20283875dacda939fe586a748abf3a14df888d11b6"
                println("result: ${result.id} ${result.hash} ${result.time}")
                lock.countDown()
            })
        Thread(runnable).start()
        lock.await()

    }
}