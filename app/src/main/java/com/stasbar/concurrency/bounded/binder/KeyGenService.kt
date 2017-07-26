package com.stasbar.concurrency.bounded.binder

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import com.stasbar.concurrency.aidl.KeyGenerator
import com.stasbar.concurrency.aidl.KeyGeneratorCallback
import java.util.*
import kotlin.collections.HashSet

class KeyGenService : Service() {
    var keys: Set<UUID> = HashSet()

    val mBinder: KeyGenerator.Stub = object : KeyGenerator.Stub() {
        override fun setCallback(callback: KeyGeneratorCallback) {
            var id: UUID? = null
            synchronized(keys) {
                do {
                    id = UUID.randomUUID()
                } while (keys.contains(id))
                keys += id!!
            }
            val key = id.toString()
            callback.sendKey(key)
        }

    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }
}
