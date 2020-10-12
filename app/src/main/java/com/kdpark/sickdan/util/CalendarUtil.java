package com.kdpark.sickdan.util;

import com.kdpark.sickdan.view.control.calendar.CalendarCell;
import com.kdpark.sickdan.view.control.calendar.CalendarCellType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarUtil {
    public static final String VIEW_MODE_KEY = "viewMode";
    public static final int MODE_PRIVATE = 0;
    public static final int MODE_PUBLIC = 1;

    public static List<CalendarCell> getDefaultMonthList(Calendar calendar) {
        List<CalendarCell> defaultList = new ArrayList<>();

        Calendar temp = (Calendar) calendar.clone();
        Calendar startOfMonth = (Calendar) calendar.clone();
        Calendar endOfMonth = (Calendar) calendar.clone();

        startOfMonth.set(Calendar.DATE, 1);
        endOfMonth.set(Calendar.DATE, endOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH));

        int firstDayOfMonth = startOfMonth.get(Calendar.DAY_OF_WEEK);

        temp.set(Calendar.DATE, 1);
        temp.add(Calendar.DATE, 1 - firstDayOfMonth);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

        int calendarLineCount = getCalendarLineCount(calendar);

        for (int i = 0; i < calendarLineCount * 7; i++) {

            CalendarCellType type;

            if (temp.before(startOfMonth)) {
                type = CalendarCellType.EMPTY;
            } else if (temp.after(endOfMonth)) {
                type = CalendarCellType.EMPTY;
            } else {
                type = CalendarCellType.DAY;
            }

            defaultList.add(CalendarCell.builder()
                    .date(dateFormat.format(temp.getTime()))
                    .type(type)
                    .build());

            temp.add(Calendar.DATE, 1);
        }

        return defaultList;
    }

    public static List<CalendarCell> getDefaultMonthList(String date) {
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6)) - 1;
        int day = 1;

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);

        return getDefaultMonthList(cal);
    }

    public static int getCalendarLineCount(Calendar calendar) {
        Calendar temp = (Calendar) calendar.clone();
        temp.set(Calendar.DATE, 1);
        int firstDayOfMonth = temp.get(Calendar.DAY_OF_WEEK);

        return (temp.getActualMaximum(Calendar.DATE) + firstDayOfMonth - 1 + 6) / 7;
    }

    public static String calendarToString(Calendar calendar, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(calendar.getTime());
    }

    public static Calendar stringToCalendar(String date) {
        Calendar calendar = Calendar.getInstance();

        int year = date.length() >= 4 ? Integer.parseInt(date.substring(0, 4)) : calendar.get(Calendar.YEAR);
        int month = date.length() >= 6 ? Integer.parseInt(date.substring(4, 6)) - 1 : calendar.get(Calendar.MONTH);
        int day = date.length() >= 8 ? Integer.parseInt(date.substring(6, 8)) : calendar.get(Calendar.DATE);

        calendar.set(year, month, day);

        return calendar;
    }

    public static boolean isSameDate(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE);
    }

    public static String getTodayString() {
        return calendarToString(Calendar.getInstance(), "yyyyMMdd");
    }
}
