package com.stasbar.concurrency.contentProvider

import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by stasbar on 13.02.2018
 */
data class DataRecord(val id: Int = idCounter.getAndIncrement(),val name: String) {


    companion object {
        val idCounter: AtomicInteger = AtomicInteger(1)
    }
}