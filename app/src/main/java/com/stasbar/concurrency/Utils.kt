package com.stasbar.concurrency

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.util.TimeUtils
import androidx.fragment.app.FragmentActivity
import kotlin.system.measureTimeMillis

/**
 * Created by stasbar on 09.07.2017
 */
object Utils {

    @SuppressLint("RestrictedApi")
    fun measureTimeAndPrint(tag: String, body: () -> Unit) {
        val time = measureTimeMillis { body() }
        val stringBuilder = StringBuilder()
        TimeUtils.formatDuration(time, stringBuilder)

        val logTag = if ("measureTime $tag".length > 23) "measureTime $tag".substring(0, 23) else "measureTime $tag"
        Log.d(logTag, stringBuilder.toString())
    }

    fun hideKeyboard(activity: FragmentActivity) {
        if (activity.currentFocus != null && activity.currentFocus?.windowToken != null) {
            val inputMethodManager = activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus?.windowToken, 0
            )
        }
    }
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}


fun EditText.onTextChanged(textChanged: (CharSequence, Int, Int, Int) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            textChanged(s, start, before, count)
        }

    })
}

fun Activity.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

inline fun <reified T> Activity.startActivity() = Intent(this, T::class.java).also { startActivity(it) }