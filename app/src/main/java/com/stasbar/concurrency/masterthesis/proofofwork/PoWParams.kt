package com.stasbar.concurrency.masterthesis.proofofwork

import com.stasbar.concurrency.masterthesis.AsyncParams

data class PoWParams @ExperimentalUnsignedTypes constructor(
    val searchRange: ULongRange,
    val data: String,
    val difficulty: UInt
) : AsyncParams
