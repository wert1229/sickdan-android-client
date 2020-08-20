package com.kdpark.sickdan.view.control.calendar;

public enum CalendarCellType {
    EMPTY(0), DAY(1);

    private int intVal;

    CalendarCellType(int type) {
        this.intVal = type;
    }

    public int getInt() {
        return intVal;
    }
}
