package com.stasbar.concurrency.masterthesis.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.stasbar.concurrency.R

class ProgressView : LinearLayout {
    constructor(context: Context) : super(context, null)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    val tvCounter: TextView
    val progressBar: ProgressBar

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        orientation = HORIZONTAL

        LayoutInflater.from(context).inflate(R.layout.progress_bar, this, true)

        tvCounter = findViewById(R.id.tvCounter)
        progressBar = findViewById(R.id.progressBar)
    }
}