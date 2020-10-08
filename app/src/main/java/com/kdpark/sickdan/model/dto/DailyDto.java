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
    private int commentCount;
    private int likeCount;
    private List<MealDto> meals = new ArrayList<>();

    @Builder
    public DailyDto(String date, String memo, int walkCount, float bodyWeight, int commentCount, int likeCount, List<MealDto> meals) {
        this.date = date;
        this.memo = memo;
        this.walkCount = walkCount;
        this.bodyWeight = bodyWeight;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.meals = meals;
    }
}
