// KeyGeneratorCallback.aidl
package com.stasbar.concurrency.aidl;

// Declare any non-default types here with import statements

interface KeyGeneratorCallback {
    oneway void sendKey(in String key);
}
