package com.jrajav.bedtime;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public final class WakeTimeFragment
extends AbstractTimeFragment {

    private static final int RESULT1_CYCLES = -6;
    private static final int RESULT2_CYCLES = -5;
    private static final int RESULT3_CYCLES = -4;
    private static final int RESULT4_CYCLES = -3;

    protected void setupInputClock(final DigitalClock inputClock) {
        inputClock.setClickable(true);
        inputClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogFragment(
                        TimePickerDialogFragment.newInstance(getTag(), false),
                        TimePickerDialogFragment.TAG);
            }
        });
    }

    protected void setupResultClock(final DigitalClock resultClock) {
        resultClock.setClickable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        View rootView = inflater.inflate(R.layout.fragment_wake, container, false);

        setInputClock((DigitalClock) rootView.findViewById(R.id.wake_input_clock));

        setResultLabel((TextView) rootView.findViewById(R.id.wake_sleep_label));

        setResultClock1((DigitalClock) rootView.findViewById(R.id.wake_result_1));
        setResultClock2((DigitalClock) rootView.findViewById(R.id.wake_result_2));
        setResultClock3((DigitalClock) rootView.findViewById(R.id.wake_result_3));
        setResultClock4((DigitalClock) rootView.findViewById(R.id.wake_result_4));

        setResultCycles1(RESULT1_CYCLES);
        setResultCycles2(RESULT2_CYCLES);
        setResultCycles3(RESULT3_CYCLES);
        setResultCycles4(RESULT4_CYCLES);

        restoreViews(icicle);

        return rootView;
    }

}
