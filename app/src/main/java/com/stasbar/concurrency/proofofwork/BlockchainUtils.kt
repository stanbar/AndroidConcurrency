package com.stasbar.concurrency.proofofwork

import java.security.MessageDigest

sealed class MiningResult {
    class Success(val hash: String, val time: Long) : MiningResult()
    object Failure : MiningResult()
}

@ExperimentalUnsignedTypes
fun mine(
    data: String,
    difficulty: Int,
    searchNonce: ULongRange = ULongRange(ULong.MIN_VALUE, ULong.MAX_VALUE)
): MiningResult {
    val target = String(CharArray(difficulty)).replace('\u0000', '0')
    val startTime = System.currentTimeMillis()
    for (testNonce in searchNonce) {
        val hash = calculateHashOf(data, testNonce)
        if (hash.substring(0, difficulty) == target)
            return MiningResult.Success(hash, System.currentTimeMillis() - startTime)
    }
    return MiningResult.Failure
}

@ExperimentalUnsignedTypes
fun calculateHashOf(data: String, nonce: ULong): String {
    val input = data + nonce
    try {
        val digest = MessageDigest.getInstance("SHA-256")
        //Applies sha256 to our input,
        val hash = digest.digest(input.toByteArray(charset("UTF-8")))
        val hexString = StringBuilder() // This will contain hash as hexidecimal
        for (hash1 in hash) {
            val hex = Integer.toHexString(0xff and hash1.toInt())
            if (hex.length == 1) hexString.append('0')
            hexString.append(hex)
        }
        return hexString.toString()
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}


