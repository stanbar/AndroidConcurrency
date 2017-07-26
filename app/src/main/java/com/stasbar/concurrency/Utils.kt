package com.stasbar.concurrency

import android.annotation.SuppressLint
import android.app.Activity
import android.support.v4.app.FragmentActivity
import android.support.v4.util.TimeUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager

/**
 * Created by stasbar on 09.07.2017
 */
object Utils {
    fun measureTime(body: () -> Unit): Long {
        val start = System.currentTimeMillis()
        body()
        val end = System.currentTimeMillis()
        return end - start
    }

    @SuppressLint("RestrictedApi")
    fun measureTimeAndPrint(tag: String, body: () -> Unit) {
        val time = measureTime(body)
        val stringBuilder = StringBuilder()
        TimeUtils.formatDuration(time, stringBuilder)

        val logTag = if ("measureTime $tag".length > 23) "measureTime $tag".substring(0, 23) else "measureTime $tag"
        Log.d(logTag, stringBuilder.toString())
    }

    fun hideKeyboard(activity: FragmentActivity) {
        if (activity.currentFocus != null && activity.currentFocus.windowToken != null) {
            val inputMethodManager = activity.getSystemService(
                    Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                    activity.currentFocus.windowToken, 0)
        }
    }
}