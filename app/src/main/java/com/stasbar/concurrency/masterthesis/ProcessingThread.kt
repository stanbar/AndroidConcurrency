package com.stasbar.concurrency.masterthesis

class ProcessingThread(
    private val algorithm: Algorithm,
    private val difficulty: Long,
    threadName: String
) : Thread(threadName) {
    override fun run() {
        algorithm.start(difficulty)
    }
}