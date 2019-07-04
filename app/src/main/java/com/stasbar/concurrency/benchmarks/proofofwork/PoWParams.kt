package com.stasbar.concurrency.benchmarks.proofofwork

import com.stasbar.concurrency.benchmarks.AsyncParams

data class PoWParams @ExperimentalUnsignedTypes constructor(
    val searchRange: ULongRange,
    val data: String,
    val difficulty: UInt
) : AsyncParams
