package com.jrajav.bedtime;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public final class TimePickerDialogFragment
extends DialogFragment {

    public static final String TAG = "TIME_PICKER_DIALOG_FRAGMENT";

    private static final String KEY_CALLBACK_FRAGMENT_TAG = "CALLBACK_FRAGMENT_TAG";
    private static final String KEY_SHOW_NOW_BUTTON = "SHOW_NOW_BUTTON";

    private Button mSet;
    private Button mCancel;
    private TimePicker mPicker;

    public static TimePickerDialogFragment newInstance(
            String callbackFragmentTag,
            boolean showNowButton) {
        TimePickerDialogFragment fragment = new TimePickerDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putString(KEY_CALLBACK_FRAGMENT_TAG, callbackFragmentTag);
        arguments.putBoolean(KEY_SHOW_NOW_BUTTON, showNowButton);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        View view = inflater.inflate(R.layout.time_picker_dialog, null);

        Bundle arguments = getArguments();
        final String callbackFragmentTag = arguments.getString(KEY_CALLBACK_FRAGMENT_TAG);
        final boolean showNowButton = arguments.getBoolean(KEY_SHOW_NOW_BUTTON);

        mSet = (Button) view.findViewById(R.id.set_button);

        mCancel = (Button) view.findViewById(R.id.cancel_button);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mPicker = (TimePicker) view.findViewById(R.id.time_picker);
        mPicker.setSetButton(mSet);
        if (showNowButton) {
            mPicker.showNowButton();
        }

        mSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = getActivity();
                FragmentManager fragmentManager = activity.getFragmentManager();
                TimeSetFragment timeSetFragment =
                        (TimeSetFragment) fragmentManager.findFragmentByTag(callbackFragmentTag);
                timeSetFragment.setTime(mPicker.getHours(), mPicker.getMinutes());
                dismiss();
            }
        });

        return view;
    }

    public interface TimeSetFragment {
        void setTime(int hours, int minutes);
    }

}
