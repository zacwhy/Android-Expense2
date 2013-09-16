package com.example.expense;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.common.helpers.DateHelper;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SummarySectionsPagerAdapter extends FragmentPagerAdapter {

    private List<Calendar> mCalendars;
    private List<String> mTitles;

    public SummarySectionsPagerAdapter(FragmentManager fm, List<Calendar> calendars) {
        super(fm);

        mCalendars = calendars;

        mTitles = new ArrayList<String>();

        for (Calendar calendar : calendars) {
            String title = DateHelper.format(calendar, "MMMM yyyy");
            mTitles.add(title);
        }
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a DummySectionFragment (defined as a static inner class
        // below) with the page number as its lone argument.
        Fragment fragment = new SummarySectionFragment();
        Bundle args = new Bundle();

        Calendar calendar = mCalendars.get(position);
        args.putInt(SummarySectionFragment.ARG_YEAR, calendar.get(Calendar.YEAR));
        args.putInt(SummarySectionFragment.ARG_MONTH, calendar.get(Calendar.MONTH));

        args.putInt(SummarySectionFragment.ARG_SECTION_NUMBER, position + 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return mTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }
}