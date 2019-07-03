package com.stasbar.concurrency.masterthesis

data class JobUpdate(val id: Int, val currentNonce: ULong, val searchLength: ULong)