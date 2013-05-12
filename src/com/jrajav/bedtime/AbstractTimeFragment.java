package com.jrajav.bedtime;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public abstract class AbstractTimeFragment
extends Fragment
implements TimePickerDialogFragment.TimeSetFragment {

    private static final String KEY_RESULTS_VISIBLE = "RESULTS_VISIBLE";
    private static final String KEY_INPUT_CAL = "INPUT_CAL";
    private static final String KEY_RESULT1_CAL = "RESULT1_CAL";
    private static final String KEY_RESULT2_CAL = "RESULT2_CAL";
    private static final String KEY_RESULT3_CAL = "RESULT3_CAL";
    private static final String KEY_RESULT4_CAL = "RESULT4_CAL";

    private TextView mResultLabel;
    private DigitalClock mInputClock;
    private DigitalClock mResultClock1;
    private DigitalClock mResultClock2;
    private DigitalClock mResultClock3;
    private DigitalClock mResultClock4;

    private boolean mResultsVisible = false;
    private int mResultCycles1;
    private int mResultCycles2;
    private int mResultCycles3;
    private int mResultCycles4;

    protected abstract void setupInputClock(DigitalClock inputClock);

    protected abstract void setupResultClock(DigitalClock resultClock);

    protected final void setResultLabel(TextView label) {
        mResultLabel = label;
    }

    protected final void setInputClock(DigitalClock clock) {
        mInputClock = clock;
        setupInputClock(mInputClock);
    }

    protected final void setResultClock1(DigitalClock clock) {
        mResultClock1 = clock;
        setupResultClock(mResultClock1);
    }

    protected final void setResultClock2(DigitalClock clock) {
        mResultClock2 = clock;
        setupResultClock(mResultClock2);
    }

    protected final void setResultClock3(DigitalClock clock) {
        mResultClock3 = clock;
        setupResultClock(mResultClock3);
    }

    protected final void setResultClock4(DigitalClock clock) {
        mResultClock4 = clock;
        setupResultClock(mResultClock4);
    }

    protected final void setResultCycles1(int cycles) {
        mResultCycles1 = cycles;
    }

    protected final void setResultCycles2(int cycles) {
        mResultCycles2 = cycles;
    }

    protected final void setResultCycles3(int cycles) {
        mResultCycles3 = cycles;
    }

    protected final void setResultCycles4(int cycles) {
        mResultCycles4 = cycles;
    }

    // CommitTransaction lint message current bug in ADT
    @SuppressLint("CommitTransaction")
    protected final void showDialogFragment(DialogFragment fragment, String tag) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment previous = fragmentManager.findFragmentByTag(tag);
        if (previous != null) {
            transaction.remove(previous);
        }
        transaction.addToBackStack(null);
        fragment.show(transaction, tag);
    }

    public final void setTime(int hours, int minutes) {
        mInputClock.updateTime(hours, minutes);

        if (!mResultsVisible) {
            showViews();
            mResultsVisible = true;
        }

        TimeCycler cycler = new TimeCycler(hours, minutes);

        cycler.setCycles(mResultCycles1);
        mResultClock1.updateTime(cycler.hours(), cycler.minutes());

        cycler.setCycles(mResultCycles2);
        mResultClock2.updateTime(cycler.hours(), cycler.minutes());

        cycler.setCycles(mResultCycles3);
        mResultClock3.updateTime(cycler.hours(), cycler.minutes());

        cycler.setCycles(mResultCycles4);
        mResultClock4.updateTime(cycler.hours(), cycler.minutes());
    }

    protected final void showViews() {
        mResultLabel.setVisibility(View.VISIBLE);
        mResultClock1.setVisibility(View.VISIBLE);
        mResultClock2.setVisibility(View.VISIBLE);
        mResultClock3.setVisibility(View.VISIBLE);
        mResultClock4.setVisibility(View.VISIBLE);
    }

    protected final void restoreViews(Bundle icicle) {
        if (icicle != null) {
            Calendar inputCal = (Calendar) icicle.getSerializable(KEY_INPUT_CAL);
            mInputClock.updateTime(inputCal);

            mResultsVisible = icicle.getBoolean(KEY_RESULTS_VISIBLE);

            if (mResultsVisible) {
                Calendar resultCal1 = (Calendar) icicle.getSerializable(KEY_RESULT1_CAL);
                mResultClock1.updateTime(resultCal1);

                Calendar resultCal2 = (Calendar) icicle.getSerializable(KEY_RESULT2_CAL);
                mResultClock2.updateTime(resultCal2);

                Calendar resultCal3 = (Calendar) icicle.getSerializable(KEY_RESULT3_CAL);
                mResultClock3.updateTime(resultCal3);

                Calendar resultCal4 = (Calendar) icicle.getSerializable(KEY_RESULT4_CAL);
                mResultClock4.updateTime(resultCal4);

                showViews();
            }
        } else {
            mInputClock.updateTime(0, 0);
        }
    }

    @Override
    public final void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
        icicle.putBoolean(KEY_RESULTS_VISIBLE, mResultsVisible);
        icicle.putSerializable(KEY_INPUT_CAL, mInputClock.getTime());
        icicle.putSerializable(KEY_RESULT1_CAL, mResultClock1.getTime());
        icicle.putSerializable(KEY_RESULT2_CAL, mResultClock2.getTime());
        icicle.putSerializable(KEY_RESULT3_CAL, mResultClock3.getTime());
        icicle.putSerializable(KEY_RESULT4_CAL, mResultClock4.getTime());
    }

}
