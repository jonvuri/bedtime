package com.jrajav.bedtime;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public final class AlarmDialogFragment
extends DialogFragment
implements TimePickerDialogFragment.TimeSetFragment {

    public static final String TAG = "ALARM_DIALOG_FRAGMENT";

    private static final String KEY_HOURS = "HOURS_ARG";
    private static final String KEY_MINUTES = "MINUTES_ARG";

    private Button mSet;
    private Button mChange;
    private Button mCancel;
    private DigitalClock mClock;

    private int mHours;
    private int mMinutes;

    public static AlarmDialogFragment newInstance(int hours, int minutes) {
        final AlarmDialogFragment fragment = new AlarmDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(KEY_HOURS, hours);
        arguments.putInt(KEY_MINUTES, minutes);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(final Bundle icicle) {
        super.onSaveInstanceState(icicle);
        icicle.putInt(KEY_HOURS, mHours);
        icicle.putInt(KEY_MINUTES, mMinutes);
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        View rootView = inflater.inflate(R.layout.alarm_dialog, null);
        
        if (icicle != null) {
            mHours = icicle.getInt(KEY_HOURS);
            mMinutes = icicle.getInt(KEY_MINUTES);
        } else {
            Bundle arguments = getArguments();
            mHours = arguments.getInt(KEY_HOURS);
            mMinutes = arguments.getInt(KEY_MINUTES);
        }

        getDialog().setTitle(R.string.alarm_dialog_message);

        mSet = (Button) rootView.findViewById(R.id.alarm_set_button);
        mSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                intent.putExtra(AlarmClock.EXTRA_HOUR, mHours);
                intent.putExtra(AlarmClock.EXTRA_MINUTES, mMinutes);
                intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                startActivity(intent);
                dismiss();
            }
        });

        mChange = (Button) rootView.findViewById(R.id.alarm_change_button);
        mChange.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CommitTransaction")
            @Override
            public void onClick(View view) {
                String tag = TimePickerDialogFragment.TAG;
                
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment previous = fragmentManager.findFragmentByTag(tag);
                if (previous != null) {
                    transaction.remove(previous);
                }
                transaction.addToBackStack(null);

                TimePickerDialogFragment fragment =
                        TimePickerDialogFragment.newInstance(TAG, false);
                fragment.show(transaction, tag);
            }
        });

        mCancel = (Button) rootView.findViewById(R.id.alarm_cancel_button);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mClock = (DigitalClock) rootView.findViewById(R.id.alarm_dialog_clock);
        mClock.setClickable(false);
        mClock.updateTime(mHours, mMinutes);

        return rootView;
    }

    public void setTime(int hours, int minutes) {
        mHours = hours;
        mMinutes = minutes;
        mClock.updateTime(mHours, mMinutes);
    }

}
