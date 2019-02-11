package com.stasbar.concurrency.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Locale;

public class LogView extends ScrollView {
    private long mCreationTime = System.currentTimeMillis();

    private TextView mTextView;

    public LogView(Context context) {
        super(context);
        init();
    }

    public LogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LogView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mTextView = new TextView(getContext());
        addView(mTextView);
    }

    @SuppressLint("RestrictedApi")
    public void log(String message) {
        long elapsedTime = System.currentTimeMillis() - mCreationTime;
        if (mTextView.getText().length() > 0) {
            mTextView.append("\n");
        }
        StringBuilder builder = new StringBuilder();
        androidx.core.util.TimeUtils.formatDuration(elapsedTime, builder);
        String formattedLog = String.format(Locale.getDefault(), "%s: %s", builder.toString(), message);
        mTextView.append(formattedLog);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        smoothScrollTo(0, mTextView.getHeight());
    }
}
