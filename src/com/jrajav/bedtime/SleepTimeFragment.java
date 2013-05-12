package com.jrajav.bedtime;

import java.util.Calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public final class SleepTimeFragment
extends AbstractTimeFragment {

    private static final int RESULT1_CYCLES = 3;
    private static final int RESULT2_CYCLES = 4;
    private static final int RESULT3_CYCLES = 5;
    private static final int RESULT4_CYCLES = 6;

    protected void setupInputClock(final DigitalClock inputClock) {
        inputClock.setClickable(true);
        inputClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogFragment(
                        TimePickerDialogFragment.newInstance(getTag(), true),
                        TimePickerDialogFragment.TAG);
            }
        });
    }

    protected void setupResultClock(final DigitalClock resultClock) {
        resultClock.setClickable(true);
        resultClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = resultClock.getTime();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                showDialogFragment(
                        AlarmDialogFragment.newInstance(hour, minute),
                        AlarmDialogFragment.TAG);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        View rootView = inflater.inflate(R.layout.fragment_sleep, container, false);

        setInputClock((DigitalClock) rootView.findViewById(R.id.sleep_main_clock));

        setResultLabel((TextView) rootView.findViewById(R.id.sleep_wake_label));

        setResultClock1((DigitalClock) rootView.findViewById(R.id.sleep_result_1));
        setResultClock2((DigitalClock) rootView.findViewById(R.id.sleep_result_2));
        setResultClock3((DigitalClock) rootView.findViewById(R.id.sleep_result_3));
        setResultClock4((DigitalClock) rootView.findViewById(R.id.sleep_result_4));

        setResultCycles1(RESULT1_CYCLES);
        setResultCycles2(RESULT2_CYCLES);
        setResultCycles3(RESULT3_CYCLES);
        setResultCycles4(RESULT4_CYCLES);

        restoreViews(icicle);

        return rootView;
    }

}
