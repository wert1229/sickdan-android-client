package com.kdpark.sickdan.view.control.calendar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.kdpark.sickdan.util.CalendarUtil;
import com.kdpark.sickdan.view.CalendarSwipeFragment;

import java.util.Calendar;

public class CalendarSlideAdapter extends FragmentStateAdapter {
    public static final int NUM_PAGES = Integer.MAX_VALUE;

    private int nowPos;
    private Calendar current;
    private OnSlideComplete onSlideComplete;

    public CalendarSlideAdapter(FragmentActivity fa, int startPos, Calendar calendar) {
        super(fa);
        nowPos = startPos;
        current = calendar;
    }

    public void setOnSlideComplete(OnSlideComplete onSlideComplete) {
        this.onSlideComplete = onSlideComplete;
    }

    @Override
    public Fragment createFragment(int position) {
        Calendar cal = (Calendar) current.clone();

        if (position != 0)
            cal.add(Calendar.MONTH, position - nowPos);

        return CalendarSwipeFragment.newInstance(CalendarUtil.calendarToString(cal, "yyyyMM"));
    }

    public void setNowPos(int newPos) {
        current.add(Calendar.MONTH, newPos - nowPos);
        this.nowPos = newPos;
    }

    public void triggerSlideComplete() {
        onSlideComplete.onComplete(nowPos, current);
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }

    public interface OnSlideComplete {
        void onComplete(int pos, Calendar calendar);
    }

    public int getNowPos() {
        return nowPos;
    }

    public Calendar getCurrent() {
        return current;
    }
}
