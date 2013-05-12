package com.jrajav.bedtime;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public final class InputActivity
extends Activity {

    private static final int WAKE_TAB_INDEX = 0;
    private static final int SLEEP_TAB_INDEX = 1;
    private static final int NUMBER_OF_TABS = 2;
    private static final String KEY_SELECTED_TAB = "SELECTED_TAB";

    private ViewPager mViewPager;
    private ActionBar mActionBar;
    private Tab mWakeTimeTab;
    private Tab mSleepTimeTab;

    private int mSelectedTab;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        if (icicle != null) {
            mSelectedTab = icicle.getInt(KEY_SELECTED_TAB, WAKE_TAB_INDEX);
        } else {
            mSelectedTab = WAKE_TAB_INDEX;
        }

        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.pager);

        new TabsAdapter(this, mViewPager);

        setContentView(mViewPager);

        mActionBar = getActionBar();
        mActionBar.setSelectedNavigationItem(mSelectedTab);
    }

    @Override
    protected void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
        icicle.putInt(KEY_SELECTED_TAB, mActionBar.getSelectedNavigationIndex());
    }

    private final class TabsAdapter
    extends FragmentPagerAdapter
    implements ActionBar.TabListener, ViewPager.OnPageChangeListener {

        private WakeTimeFragment mWakeFragment;
        private SleepTimeFragment mSleepFragment;

        private ActionBar mMainActionBar;
        private ViewPager mPager;

        public TabsAdapter(Activity activity, ViewPager pager) {
            super(activity.getFragmentManager());

            mPager = pager;
            mPager.setAdapter(this);
            mPager.setOnPageChangeListener(this);

            mMainActionBar = activity.getActionBar();
            mMainActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            mWakeTimeTab = mMainActionBar.newTab();
            mWakeTimeTab.setIcon(R.drawable.wake_tab);
            mWakeTimeTab.setContentDescription(R.string.waketime_tab);
            mWakeTimeTab.setTabListener(this);

            mMainActionBar.addTab(mWakeTimeTab);

            mSleepTimeTab = mMainActionBar.newTab();
            mSleepTimeTab.setIcon(R.drawable.sleep_tab);
            mSleepTimeTab.setContentDescription(R.string.sleeptime_tab);
            mSleepTimeTab.setTabListener(this);

            mMainActionBar.addTab(mSleepTimeTab);
        }

        public Fragment getItem(int position) {
            switch (position) {
            case WAKE_TAB_INDEX:
                mWakeFragment = new WakeTimeFragment();
                return mWakeFragment;
            case SLEEP_TAB_INDEX:
                mSleepFragment = new SleepTimeFragment();
                return mSleepFragment;
            default:
                return null;
            }
        }

        public int getCount() {
            return NUMBER_OF_TABS;
        }

        public void onPageSelected(int position) {
            mMainActionBar.setSelectedNavigationItem(position);
        }

        public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
            int position = tab.getPosition();
            mPager.setCurrentItem(position);
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // Do nothing (Implemented to cover abstract)
        }

        public void onPageScrollStateChanged(int state) {
            // Do nothing (Implemented to cover abstract)
        }

        public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {
            // Do nothing (Implemented to cover abstract)
        }

        public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {
            // Do nothing (Implemented to cover abstract)
        }

    }

}
