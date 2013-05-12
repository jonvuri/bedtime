package com.jrajav.bedtime;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public final class TimePicker
extends TimerSetupView
implements Button.OnClickListener {

    private static final int AMPM_NOT_SELECTED = 0;
    private static final int PM_SELECTED = 1;
    private static final int AM_SELECTED = 2;
    private static final int HOURS24_MODE = 3;

    private TextView mAmPmLabel;
    private String[] mAmpm;
    private final String mNoAmPmLabel;
    private int mAmPmState;
    private Button mSetButton;
    private ImageButton mNowButton;
    private final boolean mIs24HoursMode;

    public TimePicker(Context context) {
        this(context, null);
    }

    public TimePicker(Context context, AttributeSet attributes) {
        super(context, attributes);
        mInputSize = 4;
        mNoAmPmLabel = context.getResources().getString(R.string.time_picker_ampm_label);
        mIs24HoursMode = android.text.format.DateFormat.is24HourFormat(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.time_picker_view;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Resources resources = mContext.getResources();
        mAmpm = new DateFormatSymbols().getAmPmStrings();

        if (mIs24HoursMode) {
            mLeft.setText(resources.getString(R.string.time_picker_00_label));
            mRight.setText(resources.getString(R.string.time_picker_30_label));
        } else {
            mLeft.setText(mAmpm[0]);
            mRight.setText(mAmpm[1]);
        }
        mLeft.setOnClickListener(this);
        mRight.setOnClickListener(this);
        mAmPmLabel = (TextView) findViewById(R.id.ampm_label);
        mAmPmState = AMPM_NOT_SELECTED;
        mNowButton = (ImageButton) findViewById(R.id.now_button);
        mNowButton.setOnClickListener(this);
        updateKeypad();
    }

    @Override
    public void onClick(View view) {
        doOnClick(view);
    }

    @Override
    protected void doOnClick(View view) {
        Integer value = (Integer) view.getTag(R.id.numbers_key);

        // A number was pressed
        if (value != null) {
            addClickedNumber(value);
        } else if (view == mDelete) {
            // Pressing delete when AM or PM is selected, clears the AM/PM
            // selection
            if (!mIs24HoursMode && mAmPmState != AMPM_NOT_SELECTED) {
                mAmPmState = AMPM_NOT_SELECTED;
            } else if (mInputPointer >= 0) {
                for (int i = 0; i < mInputPointer; i++) {
                    mInput[i] = mInput[i + 1];
                }
                mInput[mInputPointer] = 0;
                mInputPointer--;
            }
        } else if (view == mLeft) {
            onLeftClicked();
        } else if (view == mRight) {
            onRightClicked();
        } else if (view == mNowButton) {
            onNowButtonClicked();
        }

        updateKeypad();
    }

    @Override
    public boolean onLongClick(View view) {
        if (view == mDelete) {
            mAmPmState = AMPM_NOT_SELECTED;
            reset();
            updateKeypad();
            return true;
        }
        return false;
    }

    private void updateKeypad() {
        // Update state of keypad
        // Set the correct AM/PM state
        showAmPm();
        // Update the time
        updateLeftRightButtons();
        updateTime();
        // enable/disable numeric keys according to the numbers entered already
        updateNumericKeys();
        // enable/disable the "set" key
        enableSetButton();
        // Update the backspace button
        updateDeleteButton();
    }

    // Update the time displayed in the picker:
    // Special cases:
    // 1. show "-" for digits not entered yet.
    // 2. hide the hours digits when it is not relevant
    @Override
    protected void updateTime() {
        // Put "-" in digits that was not entered by passing -1
        // Hide digit by passing -2 (for highest hours digit only);

        int  hours1 = -1;
        //int time = getEnteredTime();
        // If the user entered 2 to 9 or 13 to 15 , there is no need for a 4th digit (AM/PM mode)
        // If the user entered 3 to 9 or 24 to 25 , there is no need for a 4th digit (24 hours mode)
        if (mInputPointer > -1) {
            // Test to see if the highest digit is 2 to 9 for AM/PM or 3 to 9 for 24 hours mode
            if (mInputPointer >= 0) {
                int digit = mInput[mInputPointer];
                if ((mIs24HoursMode && digit >= 3 && digit <= 9)
                        || (!mIs24HoursMode && digit >= 2 && digit <= 9)) {
                    hours1 = -2;
                }
            }
            // Test to see if the 2 highest digits are 13 to 15 for AM/PM or 24 to 25 for 24 hours
            // mode
            if (mInputPointer > 0 && mInputPointer < 3 && hours1 != -2) {
                int digits = mInput[mInputPointer] * 10 + mInput[mInputPointer - 1];
                if ((mIs24HoursMode && digits >= 24 && digits <= 25)
                        || (!mIs24HoursMode && digits >= 13 && digits <= 15)) {
                    hours1 = -2;
                }
            }
            // If we have a digit show it
            if (mInputPointer == 3) {
                hours1 = mInput[3];
            }
        } else {
            hours1 = -1;
        }
        int hours2 = (mInputPointer < 2) ? -1 :  mInput[2];
        int minutes1 = (mInputPointer < 1) ? -1 :  mInput[1];
        int minutes2 = (mInputPointer < 0) ? -1 :  mInput[0];
        mEnteredTime.setTime(hours1, hours2, minutes1, minutes2, -1);
    }

    private void showAmPm() {
        if (!mIs24HoursMode) {
            switch(mAmPmState) {
            case AMPM_NOT_SELECTED:
                mAmPmLabel.setText(mNoAmPmLabel);
                break;
            case AM_SELECTED:
                mAmPmLabel.setText(mAmpm[0]);
                break;
            case PM_SELECTED:
                mAmPmLabel.setText(mAmpm[1]);
                break;
            default:
                break;
            }
        } else {
            mAmPmLabel.setVisibility(View.INVISIBLE);
            mAmPmState = HOURS24_MODE;
        }
    }

    private void addClickedNumber(int value) {
        if (mInputPointer < mInputSize - 1) {
            for (int i = mInputPointer; i >= 0; i--) {
                mInput[i + 1] = mInput[i];
            }
            mInputPointer++;
            mInput[0] = value;
        }
    }

    // Clicking on the bottom left button will add "00" to the time
    // In AM/PM mode is will also set the time to AM.
    private void onLeftClicked() {
        if (!mIs24HoursMode) {
            if (canAddDigits()) {
                addClickedNumber(0);
                addClickedNumber(0);
            }
            mAmPmState = AM_SELECTED;
        } else if (canAddDigits()) {
            addClickedNumber(0);
            addClickedNumber(0);
        }
    }

    // Clicking on the bottom right button will add "00" to the time in AM/PM
    // mode and "30" is 24 hours mode.
    // In AM/PM mode is will also set the time to PM.
    private void onRightClicked() {
        if (!mIs24HoursMode) {
            if (canAddDigits()) {
                addClickedNumber(0);
                addClickedNumber(0);
            }
            mAmPmState = PM_SELECTED;
        } else {
            if (canAddDigits()) {
                addClickedNumber(3);
                addClickedNumber(0);
            }
        }
    }

    private void onNowButtonClicked() {
        setKeypadTime(Calendar.getInstance());
    }

    private void setKeypadTime(Calendar calendar) {
        int hours;
        int minutes;

        if (mIs24HoursMode) {
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minutes = calendar.get(Calendar.MINUTE);
        } else {
            hours = calendar.get(Calendar.HOUR);
            minutes = calendar.get(Calendar.MINUTE);

            if (calendar.get(Calendar.AM_PM) == Calendar.AM) {
                mAmPmState = AM_SELECTED;
            } else {
                mAmPmState = PM_SELECTED;
            }
        }

        mInput[0] = minutes % 10;
        mInput[1] = minutes / 10;
        mInput[2] = hours % 10;

        if (hours > 9) {
            mInput[3] = hours / 10;
            mInputPointer = 3;
        } else {
            mInput[3] = 0;
            mInputPointer = 2;
        }
        
        updateKeypad();
    }

    // Checks if the user allowed to click on the left or right button that enters "00" or "30"
    private boolean canAddDigits() {
        int time = getEnteredTime();
        // For AM/PM mode , can add "00" if an hour between 1 and 12 was entered
        if (!mIs24HoursMode) {
            return time >= 1 && time <= 12;
        }
        // For 24 hours mode , can add "00"/"30" if an hour between 0 and 23 was entered
        return time >= 0 && time <= 23 && mInputPointer > -1 && mInputPointer < 2;
    }

    // Enable/disable keys in the numeric key pad according to the data entered
    private void updateNumericKeys() {
        int time = getEnteredTime();
        if (mIs24HoursMode) {
            if (mInputPointer >= 3) {
                setKeyRange(-1);
            } else if (time == 0) {
                if (mInputPointer == -1 || mInputPointer == 0 || mInputPointer == 2) {
                    setKeyRange(9);
                } else if (mInputPointer == 1) {
                    setKeyRange(5);
                } else {
                    setKeyRange(-1);
                }
            } else if (time == 1) {
                if (mInputPointer == 0 || mInputPointer == 2) {
                    setKeyRange(9);
                } else  if (mInputPointer == 1) {
                    setKeyRange(5);
                } else {
                    setKeyRange(-1);
                }
            } else if (time == 2) {
                if (mInputPointer == 2 || mInputPointer == 1) {
                    setKeyRange(9);
                } else  if (mInputPointer == 0) {
                    setKeyRange(3);
                } else {
                    setKeyRange(-1);
                }
            } else if (time <= 5) {
                setKeyRange(9);
            } else if (time <= 9) {
                setKeyRange(5);
            } else if (time >= 10 && time <= 15) {
                setKeyRange(9);
            } else if (time >= 16 && time <= 19) {
                setKeyRange(5);
            } else if (time >= 20 && time <= 25) {
                setKeyRange(9);
            } else if (time >= 26 && time <= 29) {
                setKeyRange(-1);
            } else if (time >= 30 && time <= 35) {
                setKeyRange(9);
            } else if (time >= 36 && time <= 39) {
                setKeyRange(-1);
            } else if (time >= 40 && time <= 45) {
                setKeyRange(9);
            } else if (time >= 46 && time <= 49) {
                setKeyRange(-1);
            } else if (time >= 50 && time <= 55) {
                setKeyRange(9);
            } else if (time >= 56 && time <= 59) {
                setKeyRange(-1);
            } else if (time >= 60 && time <= 65) {
                setKeyRange(9);
            } else if (time >= 70 && time <= 75) {
                setKeyRange(9);
            } else if (time >= 80 && time <= 85) {
                setKeyRange(9);
            } else if (time >= 90 && time <= 95) {
                setKeyRange(9);
            } else if (time >= 100 && time <= 105) {
                setKeyRange(9);
            } else if (time >= 106 && time <= 109) {
                setKeyRange(-1);
            } else if (time >= 110 && time <= 115) {
                setKeyRange(9);
            } else if (time >= 116 && time <= 119) {
                setKeyRange(-1);
            } else if (time >= 120 && time <= 125) {
                setKeyRange(9);
            } else if (time >= 126 && time <= 129) {
                setKeyRange(-1);
            } else if (time >= 130 && time <= 135) {
                setKeyRange(9);
            } else if (time >= 136 && time <= 139) {
                setKeyRange(-1);
            } else if (time >= 140 && time <= 145) {
                setKeyRange(9);
            } else if (time >= 146 && time <= 149) {
                setKeyRange(-1);
            } else if (time >= 150 && time <= 155) {
                setKeyRange(9);
            } else if (time >= 156 && time <= 159) {
                setKeyRange(-1);
            } else if (time >= 160 && time <= 165) {
                setKeyRange(9);
            } else if (time >= 166 && time <= 169) {
                setKeyRange(-1);
            } else if (time >= 170 && time <= 175) {
                setKeyRange(9);
            } else if (time >= 176 && time <= 179) {
                setKeyRange(-1);
            } else if (time >= 180 && time <= 185) {
                setKeyRange(9);
            } else if (time >= 186 && time <= 189) {
                setKeyRange(-1);
            } else if (time >= 190 && time <= 195) {
                setKeyRange(9);
            } else if (time >= 196 && time <= 199) {
                setKeyRange(-1);
            } else if (time >= 200 && time <= 205) {
                setKeyRange(9);
            } else if (time >= 206 && time <= 209) {
                setKeyRange(-1);
            } else if (time >= 210 && time <= 215) {
                setKeyRange(9);
            } else if (time >= 216 && time <= 219) {
                setKeyRange(-1);
            } else if (time >= 220 && time <= 225) {
                setKeyRange(9);
            } else if (time >= 226 && time <= 229) {
                setKeyRange(-1);
            } else if (time >= 230 && time <= 235) {
                setKeyRange(9);
            } else if (time >= 236) {
                setKeyRange(-1);
            }
        } else {
            // Selecting AM/PM disabled the keypad
            if (mAmPmState != AMPM_NOT_SELECTED) {
                setKeyRange(-1);
            } else if (time == 0) {
                setKeyRange(9);
                mNumbers[0].setEnabled(false);
            } else if (time <= 9) {
                setKeyRange(5);
            } else if (time <= 95) {
                setKeyRange(9);
            } else if (time >= 100 && time <= 105) {
                setKeyRange(9);
            } else if (time >= 106 && time <= 109) {
                setKeyRange(-1);
            } else if (time >= 110 && time <= 115) {
                setKeyRange(9);
            } else if (time >= 116 && time <= 119) {
                setKeyRange(-1);
            } else if (time >= 120 && time <= 125) {
                setKeyRange(9);
            } else if (time >= 126) {
                setKeyRange(-1);
            }
        }
    }

    // Returns the time already entered in decimal representation. if time is H1 H2 : M1 M2
    // the value returned is H1*1000+H2*100+M1*10+M2
    private int getEnteredTime() {
        return  mInput[3] * 1000 + mInput[2] * 100 + mInput[1] * 10 + mInput[0];
    }

    // Enables a range of numeric keys from zero to maxKey. The rest of the keys will be disabled
    private void setKeyRange(int maxKey) {
        for (int i = 0; i < mNumbers.length; i++) {
            mNumbers[i].setEnabled(i <= maxKey);
        }
    }

    private void updateLeftRightButtons() {
        int time = getEnteredTime();
        if (mIs24HoursMode) {
            boolean enable = canAddDigits();
            mLeft.setEnabled(enable);
            mRight.setEnabled(enable);
        } else {
            // You can use the AM/PM if time entered is 0 to 12 or it is 3 digits or more
            if ((time > 12 && time < 100) || time == 0 || mAmPmState != AMPM_NOT_SELECTED) {
                mLeft.setEnabled(false);
                mRight.setEnabled(false);
            } else {
                mLeft.setEnabled(true);
                mRight.setEnabled(true);
            }
        }
    }

    // Enable/disable the set key
    private void enableSetButton() {
        if (mSetButton == null) {
            return;
        }

        // Nothing entered - disable
        if (mInputPointer == -1) {
            mSetButton.setEnabled(false);
            return;
        }
        // If the user entered 3 digits or more but not 060 to 095
        // it is a legal time and the set key should be enabled.
        if (mIs24HoursMode) {
            int time = getEnteredTime();
            mSetButton.setEnabled(mInputPointer >= 2 && (time < 60 || time > 95));
        } else {
            // If AM/PM mode , enable the set button if AM/PM was selected
            mSetButton.setEnabled(mAmPmState != AMPM_NOT_SELECTED);
        }
    }

    public void setSetButton(Button button) {
        mSetButton = button;
        enableSetButton();
    }

    public void showNowButton() {
        mNowButton.setVisibility(View.VISIBLE);
    }

    public int getHours() {
        int hours = mInput[3] * 10 + mInput[2];
        
        if (hours == 12) {
            switch (mAmPmState) {
            case PM_SELECTED:
                return 12;
            case AM_SELECTED:
                return 0;
            case HOURS24_MODE:
                return hours;
            default:
                break;
            }
        }

        return hours + (mAmPmState == PM_SELECTED ? 12 : 0);
    }

    public int getMinutes() {
        return mInput[1] * 10 + mInput[0];
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable parcel = super.onSaveInstanceState();
        SavedState state = new SavedState(parcel);
        state.mInput = mInput;
        state.mAmPmState = mAmPmState;
        state.mInputPointer = mInputPointer;
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        mInputPointer = savedState.mInputPointer;
        mInput = savedState.mInput;
        if (mInput == null) {
            mInput = new int[mInputSize];
            mInputPointer = -1;
        }
        mAmPmState = savedState.mAmPmState;
        updateKeypad();
    }

    private static class SavedState extends BaseSavedState {

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<SavedState> CREATOR =
            new Parcelable.Creator<SavedState>() {
                public SavedState createFromParcel(Parcel in) {
                    return new SavedState(in);
                }
    
                public SavedState[] newArray(int size) {
                    return new SavedState[size];
                }
            };

        private int mInputPointer;
        private int[] mInput;
        private int mAmPmState;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mInputPointer = in.readInt();
            in.readIntArray(mInput);
            mAmPmState = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mInputPointer);
            out.writeIntArray(mInput);
            out.writeInt(mAmPmState);
        }

    }

}
