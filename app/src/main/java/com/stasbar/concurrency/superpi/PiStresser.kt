package com.stasbar.concurrency.superpi

class PiStresser(private val requestedThreads: Long, private val requestesIterations: Long) {

    @Throws(InterruptedException::class)
    fun start() {
        val threads = List(requestedThreads.toInt()) {
            PiThread(requestesIterations)
        }

        threads.forEach(Thread::start)

        while (!allThreadsEnded(threads)) {
            Thread.sleep(10)
        }
    }

    private fun allThreadsEnded(threads: List<PiThread>): Boolean {
        for (thread in threads) {
            if (!thread.hasEnded()) {
                return false
            }
        }
        return true
    }
}