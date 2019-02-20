package com.stasbar.concurrency.bounded.binder

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.stasbar.concurrency.aidl.KeyGenerator
import com.stasbar.concurrency.aidl.KeyGeneratorCallback
import java.util.*
import kotlin.collections.HashSet

class KeyGenService : Service() {
    var keys: Set<UUID> = HashSet()

    private val mBinder: KeyGenerator.Stub = object : KeyGenerator.Stub() {
        override fun setCallback(callback: KeyGeneratorCallback) {
            val id = synchronized(keys) {
                var id: UUID
                do {
                    id = UUID.randomUUID()
                } while (keys.contains(id))
                keys = keys + id
                id
            }
            callback.sendKey(id.toString())
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }
}
