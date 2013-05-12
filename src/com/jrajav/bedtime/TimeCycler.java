package com.jrajav.bedtime;

public final class TimeCycler {

    private static final int MINUTES_IN_DAY = 1440;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int MINUTES_IN_SLEEP_CYCLE = 90;

    private final int mMinutes;

    private int mCycles;
    private int mCycleAdjustedMinutes;

    public TimeCycler(int hours, int minutes) {
        this.mMinutes = hours * MINUTES_IN_HOUR + minutes;
        setCycles(0);
    }

    private void updateAdjustedMinutes() {
        mCycleAdjustedMinutes = mMinutes + mCycles * MINUTES_IN_SLEEP_CYCLE;

        if (mCycleAdjustedMinutes < 0) {
            mCycleAdjustedMinutes += MINUTES_IN_DAY;
        } else if (mCycleAdjustedMinutes > MINUTES_IN_DAY) {
            mCycleAdjustedMinutes -= MINUTES_IN_DAY;
        }
    }

    public void setCycles(int cycles) {
        this.mCycles = cycles;
        updateAdjustedMinutes();
    }

    public int hours() {
        return mCycleAdjustedMinutes / MINUTES_IN_HOUR;
    }

    public int minutes() {
        return mCycleAdjustedMinutes % MINUTES_IN_HOUR;
    }

}
