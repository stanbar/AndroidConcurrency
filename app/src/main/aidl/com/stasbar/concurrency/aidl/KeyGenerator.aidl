// KeyGenerator.aidl
package com.stasbar.concurrency.aidl;

// Declare any non-default types here with import statements
import com.stasbar.concurrency.aidl.KeyGeneratorCallback;

interface KeyGenerator {
    oneway void setCallback(in KeyGeneratorCallback callback);
}
