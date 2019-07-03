package com.stasbar.concurrency.masterthesis

sealed class MiningResult(val id: Int) {
    class Success(val id: Int, val hash: String, val time: Long) : MiningResult(id)
    class NotFound(val id: Int) : MiningResult(id)
    class Cancelled(val id: Int) : MiningResult(id)
}