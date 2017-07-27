package com.stasbar.concurrency.caching

import android.util.Log
import com.stasbar.concurrency.Square
import java.lang.ref.SoftReference

/**
 * Created by stasbar on 27.07.2017
 */
class SimpleCache {
    val mCache: HashMap<String, SoftReference<Square>> = HashMap()
    fun put(key: String, value: Square) {
        synchronized(mCache) {
            mCache.put(key, SoftReference(value))
        }
    }

    fun get(key: String, builder: Square.Builder): Square {
        synchronized(mCache) {
            var value: Square? = null
            val reference = mCache[key]
            if (reference != null) {
                value = reference.get()
            }
            // Not in cache or gc'ed
            if (value == null) {
                Log.d("SimpleCache", "Missed !")
                value = builder.build(key)
                put(key, value)
            } else
                Log.d("SimpleCache", "HIT !")

            return value
        }
    }

    fun read(key: String): Int {
        synchronized(mCache) {
            var value: Square?
            val reference = mCache[key]

            if (reference != null)
                value = reference.get()
            else return 0

            if (value == null) {
                Log.d("SimpleCache", "Missed !")
                return 0
            } else
                Log.d("SimpleCache", "HIT !")
            return 1
        }
    }
}