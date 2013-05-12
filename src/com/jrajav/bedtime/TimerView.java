package com.jrajav.bedtime;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class TimerView
extends LinearLayout {
    
    private ZeroTopPaddingTextView mHoursOnes, mMinutesOnes;
    private ZeroTopPaddingTextView mHoursTens, mMinutesTens;
    private TextView mSeconds;
    private final Typeface mAndroidClockMonoThin;
    private Typeface mOriginalHoursTypeface;
    private final int mWhiteColor, mGrayColor;

    public TimerView(Context context) {
        this(context, null);
    }

    public TimerView(Context context, AttributeSet attributes) {
        super(context, attributes);

        mAndroidClockMonoThin =
                Typeface.createFromAsset(context.getAssets(), "fonts/AndroidClockMono-Thin.ttf");

        mWhiteColor = context.getResources().getColor(R.color.clock_white);
        mGrayColor = context.getResources().getColor(R.color.clock_gray);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mHoursTens = (ZeroTopPaddingTextView) findViewById(R.id.hours_tens);
        mMinutesTens = (ZeroTopPaddingTextView) findViewById(R.id.minutes_tens);
        mHoursOnes = (ZeroTopPaddingTextView) findViewById(R.id.hours_ones);
        mMinutesOnes = (ZeroTopPaddingTextView) findViewById(R.id.minutes_ones);
        mSeconds = (TextView) findViewById(R.id.seconds);
        if (mHoursOnes != null) {
            mOriginalHoursTypeface = mHoursOnes.getTypeface();
        }
        // Set the lowest time unit with thin font (excluding hundredths)
        if (mSeconds != null) {
            mSeconds.setTypeface(mAndroidClockMonoThin);
        } else  {
            if (mMinutesTens != null) {
                mMinutesTens.setTypeface(mAndroidClockMonoThin);
                mMinutesTens.updatePadding();
            }
            if (mMinutesOnes != null) {
                mMinutesOnes.setTypeface(mAndroidClockMonoThin);
                mMinutesOnes.updatePadding();
            }
        }

    }

    public void setTime(
            int hoursTens,
            int hoursOnes,
            int minutesTens,
            int minutesOnes,
            int seconds) {
        if (mHoursTens != null) {
            // Hide digit
            if (hoursTens == -2) {
                mHoursTens.setVisibility(View.INVISIBLE);
            } else if (hoursTens == -1) {
                mHoursTens.setText("-");
                mHoursTens.setTypeface(mAndroidClockMonoThin);
                mHoursTens.setTextColor(mGrayColor);
                mHoursTens.updatePadding();
                mHoursTens.setVisibility(View.VISIBLE);
            } else {
                mHoursTens.setText(String.format("%d", hoursTens));
                mHoursTens.setTypeface(mOriginalHoursTypeface);
                mHoursTens.setTextColor(mWhiteColor);
                mHoursTens.updatePadding();
                mHoursTens.setVisibility(View.VISIBLE);
            }
        }

        if (mHoursOnes != null) {
            if (hoursOnes == -1) {
                mHoursOnes.setText("-");
                mHoursOnes.setTypeface(mAndroidClockMonoThin);
                mHoursOnes.setTextColor(mGrayColor);
                mHoursOnes.updatePadding();
            } else {
                mHoursOnes.setText(String.format("%d", hoursOnes));
                mHoursOnes.setTypeface(mOriginalHoursTypeface);
                mHoursOnes.setTextColor(mWhiteColor);
                mHoursOnes.updatePadding();
            }
        }

        if (mMinutesTens != null) {
            if (minutesTens == -1) {
                mMinutesTens.setText("-");
                mMinutesTens.setTextColor(mGrayColor);
            } else {
                mMinutesTens.setTextColor(mWhiteColor);
                mMinutesTens.setText(String.format("%d", minutesTens));
            }
        }

        if (mMinutesOnes != null) {
            if (minutesOnes == -1) {
                mMinutesOnes.setText("-");
                mMinutesOnes.setTextColor(mGrayColor);
            } else {
                mMinutesOnes.setText(String.format("%d", minutesOnes));
                mMinutesOnes.setTextColor(mWhiteColor);
            }
        }

        if (mSeconds != null) {
            mSeconds.setText(String.format("%02d", seconds));
        }
    }

}
