package com.stasbar.concurrency.masterthesis

import java.security.MessageDigest
import java.util.*

class Block(private val data: String, var previousHash: String) {
    override fun toString() = previousHash + timeStamp + nonce + data
    private val timeStamp = Date().time
    var nonce: Long = 0
    var hash: String = calculateHash() //Making sure we do this after we set the other values.


    //Calculate new hash based on blocks contents
    private fun calculateHash(): String {
        return applySha256(
            previousHash +
                    timeStamp +
                    nonce +
                    data
        )
    }

    fun mineBlock(difficulty: Int, searchRange: LongRange): String? {
        val target = String(CharArray(difficulty)).replace('\u0000', '0') //Create a string with difficulty * "0"
        for (testNonce in searchRange) {
            nonce = testNonce
            hash = calculateHash()
            if (hash.substring(0, difficulty) == target)
                return hash
        }
        return null
    }

    companion object {

        fun applySha256(input: String): String {
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
    }
}