package com.kdpark.sickdan.model.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DailyDto {
    private String date;
    private String memo;
    private int walkCount;
    private float bodyWeight;
    private List<MealDto> meals = new ArrayList<>();

    @Builder
    public DailyDto(String date, String memo, int walkCount, float bodyWeight, List<MealDto> meals) {
        this.date = date;
        this.memo = memo;
        this.walkCount = walkCount;
        this.bodyWeight = bodyWeight;
        this.meals = meals;
    }
}
