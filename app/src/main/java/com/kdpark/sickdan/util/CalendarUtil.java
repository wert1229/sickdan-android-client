package com.kdpark.sickdan.util;

import com.kdpark.sickdan.view.control.calendar.CalendarCell;
import com.kdpark.sickdan.view.control.calendar.CalendarCellType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarUtil {
    public static List<CalendarCell> getDefaultOfMonth(final Calendar calendar) {
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

    public static int getCalendarLineCount(final Calendar calendar) {
        Calendar temp = (Calendar) calendar.clone();
        temp.set(Calendar.DATE, 1);
        int firstDayOfMonth = temp.get(Calendar.DAY_OF_WEEK);

        return (temp.getActualMaximum(Calendar.DATE) + firstDayOfMonth - 1 + 6) / 7;
    }
}
