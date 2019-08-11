package com.stasbar.concurrency.executor

import java.util.*
import java.util.concurrent.Executor

class SerialExecutor : Executor {
    val tasks = ArrayDeque<Runnable>()
    var active: Runnable? = null

    override fun execute(command: Runnable) {
        tasks.offer(Runnable {
            try {
                command.run()
            } finally {
                scheduleNext()
            }
        })

        if (active == null)
            scheduleNext()
    }

    private fun scheduleNext() = synchronized(this) {
        val active = tasks.poll()
        this.active = active
        if (active != null) {
            execute(active)
        }

    }


}