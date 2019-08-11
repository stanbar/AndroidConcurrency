package com.stasbar.concurrency.executor

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class TaskTrackingThreadPool
    : ThreadPoolExecutor(
    3,
    3,
    0,
    TimeUnit.SECONDS,
    LinkedBlockingQueue<Runnable>()
) {
    private val taskCount = AtomicInteger(0)

    override fun beforeExecute(t: Thread?, r: Runnable?) {
        super.beforeExecute(t, r)
        taskCount.getAndIncrement()
    }

    override fun afterExecute(r: Runnable?, t: Throwable?) {
        super.afterExecute(r, t)
        taskCount.getAndDecrement()
    }
}