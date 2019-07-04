package com.stasbar.concurrency.benchmarks

import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch

class AlgorithmTest {

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `test synchronized proof of work`() {
        val algorithm = Algorithm.ProofOfWork
        val method = ProcessingMethod.SYNCHRONIZED
        val lock = CountDownLatch(1)
        algorithm.processOn(method, 4u, 1u, 1u, { update ->
            println(update.toString())
            lock.countDown()
        }, { result ->
            result shouldBeInstanceOf MiningResult.Success::class
            result.id shouldBe 0
            (result as MiningResult.Success).hash shouldEqual "00008c00d20c8f51d50d5a20283875dacda939fe586a748abf3a14df888d11b6"
            println("success ${result.id} ${result.time} ${result.hash}")
            lock.countDown()
        })
        lock.await()
    }

    @Test
    fun `test threaded proof of work`() {
        val algorithm = Algorithm.ProofOfWork
        val method = ProcessingMethod.THREADS
        val lock = CountDownLatch(1)
        algorithm.processOn(method, 4u, 1u, 1u, { update ->
            println(update.toString())
            lock.countDown()
        }, { result ->
            result shouldBeInstanceOf MiningResult.Success::class
            result.id shouldBe 0
            (result as MiningResult.Success).hash shouldEqual "00008c00d20c8f51d50d5a20283875dacda939fe586a748abf3a14df888d11b6"
            println("success ${result.id} ${result.time} ${result.hash}")
            lock.countDown()
        })
        lock.await()
    }
}