package com.stasbar.concurrency.benchmarks

data class JobUpdate(val id: Int, val currentNonce: ULong, val searchLength: ULong)