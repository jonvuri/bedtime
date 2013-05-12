package com.jrajav.bedtime;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Typeface;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.TimeZone;

public final class DigitalClock
extends LinearLayout {

    private static final String HOURS_24 = "kk";
    private static final String HOURS = "h";
    private static final String MINUTES = ":mm";

    private Calendar mCalendar;
    private String mHoursFormat;
    private TextView mTimeDisplayHours;
    private TextView mTimeDisplayMinutes;
    private AmPm mAmPm;
    private ContentObserver mFormatChangeObserver;
    private boolean mAttached;
    private final Typeface mRobotoThin;
    private String mTimeZoneId;

    static class AmPm {
        private final TextView mAmPm;
        private final String mAmString;
        private final String mPmString;

        AmPm(View parent, int id) {
            mAmPm = (TextView) parent.findViewById(id);

            String[] ampm = new DateFormatSymbols().getAmPmStrings();
            mAmString = ampm[0];
            mPmString = ampm[1];
        }

        void setShowAmPm(boolean show) {
            mAmPm.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        void setIsMorning(boolean isMorning) {
            mAmPm.setText(isMorning ? mAmString : mPmString);
        }

        CharSequence getAmPmText() {
            return mAmPm.getText();
        }
    }

    private class FormatChangeObserver extends ContentObserver {
        public FormatChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            setDateFormat();
            updateTime();
        }
    }

    public DigitalClock(Context context) {
        this(context, null);
    }

    public DigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRobotoThin = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
    }

    private void initSubViews() {
        int hoursView;
        int minutesView;
        int amPmView;

        switch (getId()) {
        case R.id.wake_input_clock:
            hoursView = R.id.timeDisplayHours_wake_input;
            minutesView = R.id.timeDisplayMinutes_wake_input;
            amPmView = R.id.am_pm_wake_input;
            break;

        case R.id.wake_result_1:
            hoursView = R.id.timeDisplayHours_wake_1;
            minutesView = R.id.timeDisplayMinutes_wake_1;
            amPmView = R.id.am_pm_wake_1;
            break;

        case R.id.wake_result_2:
            hoursView = R.id.timeDisplayHours_wake_2;
            minutesView = R.id.timeDisplayMinutes_wake_2;
            amPmView = R.id.am_pm_wake_2;
            break;

        case R.id.wake_result_3:
            hoursView = R.id.timeDisplayHours_wake_3;
            minutesView = R.id.timeDisplayMinutes_wake_3;
            amPmView = R.id.am_pm_wake_3;
            break;

        case R.id.wake_result_4:
            hoursView = R.id.timeDisplayHours_wake_4;
            minutesView = R.id.timeDisplayMinutes_wake_4;
            amPmView = R.id.am_pm_wake_4;
            break;

        case R.id.sleep_main_clock:
            hoursView = R.id.timeDisplayHours_sleep_main;
            minutesView = R.id.timeDisplayMinutes_sleep_main;
            amPmView = R.id.am_pm_sleep_main;
            break;

        case R.id.sleep_result_1:
            hoursView = R.id.timeDisplayHours_sleep_1;
            minutesView = R.id.timeDisplayMinutes_sleep_1;
            amPmView = R.id.am_pm_sleep_1;
            break;

        case R.id.sleep_result_2:
            hoursView = R.id.timeDisplayHours_sleep_2;
            minutesView = R.id.timeDisplayMinutes_sleep_2;
            amPmView = R.id.am_pm_sleep_2;
            break;

        case R.id.sleep_result_3:
            hoursView = R.id.timeDisplayHours_sleep_3;
            minutesView = R.id.timeDisplayMinutes_sleep_3;
            amPmView = R.id.am_pm_sleep_3;
            break;

        case R.id.sleep_result_4:
            hoursView = R.id.timeDisplayHours_sleep_4;
            minutesView = R.id.timeDisplayMinutes_sleep_4;
            amPmView = R.id.am_pm_sleep_4;
            break;

        case R.id.alarm_dialog_clock:
            hoursView = R.id.timeDisplayHours_alarm_dialog;
            minutesView = R.id.timeDisplayMinutes_alarm_dialog;
            amPmView = R.id.am_pm_alarm_dialog;
            break;

        default:
            throw new RuntimeException("DigitalClock instantiated with unknown ID");
        }

        mTimeDisplayHours = (TextView) findViewById(hoursView);
        mTimeDisplayMinutes = (TextView) findViewById(minutesView);
        mAmPm = new AmPm(this, amPmView);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initSubViews();
        mTimeDisplayMinutes.setTypeface(mRobotoThin);
        mCalendar = Calendar.getInstance();
        setDateFormat();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mAttached) {
            return;
        }
        mAttached = true;

        // monitor 12/24-hour display preference
        mFormatChangeObserver = new FormatChangeObserver();
        getContext().getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI, true, mFormatChangeObserver);

        updateTime();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (!mAttached) {
            return;
        }
        mAttached = false;

        getContext().getContentResolver().unregisterContentObserver(mFormatChangeObserver);
    }

    void updateTime(Calendar calendar) {
        mCalendar = calendar;
        updateTime();
    }

    public void updateTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        mCalendar = calendar;
        updateTime();
    }

    private void updateTime() {
        if (mTimeZoneId != null) {
            mCalendar.setTimeZone(TimeZone.getTimeZone(mTimeZoneId));
        }

        StringBuilder fullTimeString = new StringBuilder();
        CharSequence newTime = DateFormat.format(mHoursFormat, mCalendar);
        mTimeDisplayHours.setText(newTime);
        fullTimeString.append(newTime);
        newTime = DateFormat.format(MINUTES, mCalendar);
        fullTimeString.append(newTime);
        mTimeDisplayMinutes.setText(newTime);

        boolean isMorning = mCalendar.get(Calendar.AM_PM) == 0;
        mAmPm.setIsMorning(isMorning);
        if (!get24HourMode(getContext())) {
            fullTimeString.append(mAmPm.getAmPmText());
        }

        // Update accessibility string.
        setContentDescription(fullTimeString);
    }

    public Calendar getTime() {
        return mCalendar;
    }

    private void setDateFormat() {
        mHoursFormat = get24HourMode(getContext()) ? HOURS_24 : HOURS;
        mAmPm.setShowAmPm(!get24HourMode(getContext()));
    }

    public static boolean get24HourMode(final Context context) {
        return android.text.format.DateFormat.is24HourFormat(context);
    }

    public void setTimeZone(String id) {
        mTimeZoneId = id;
        updateTime();
    }

}
