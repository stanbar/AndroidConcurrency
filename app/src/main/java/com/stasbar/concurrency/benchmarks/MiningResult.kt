package com.stasbar.concurrency.benchmarks

sealed class MiningResult(val id: Int) {
    class Success(id: Int, val hash: String, val time: Long) : MiningResult(id)
    class NotFound(id: Int) : MiningResult(id)
    class Cancelled(id: Int) : MiningResult(id)
}