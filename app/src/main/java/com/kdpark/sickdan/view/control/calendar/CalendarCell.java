package com.kdpark.sickdan.view.control.calendar;

import lombok.Builder;
import lombok.Data;

@Data
public class CalendarCell {
    private Long id;
    private String date;
    private int walkCount;
    private float bodyWeight;
    private CalendarCellType type;

    @Builder
    public CalendarCell(Long id, String date, int walkCount, float bodyWeight, CalendarCellType type) {
        this.id = id;
        this.date = date;
        this.walkCount = walkCount;
        this.bodyWeight = bodyWeight;
        this.type = type;
    }
}
