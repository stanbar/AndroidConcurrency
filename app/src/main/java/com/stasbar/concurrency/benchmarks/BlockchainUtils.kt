package com.stasbar.concurrency.benchmarks

import java.security.MessageDigest


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


