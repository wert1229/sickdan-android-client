package com.kdpark.sickdan.model.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DailyDto {
    @Data
    @NoArgsConstructor
    public static class Daily {
        private String date;
        private String memo;
        private int walkCount;
        private float bodyWeight;
        private int commentCount;
        private int likeCount;
        private List<MealDto.Meal> meals = new ArrayList<>();

        @Builder
        public Daily(String date, String memo, int walkCount, float bodyWeight, int commentCount, int likeCount, List<MealDto.Meal> meals) {
            this.date = date;
            this.memo = memo;
            this.walkCount = walkCount;
            this.bodyWeight = bodyWeight;
            this.commentCount = commentCount;
            this.likeCount = likeCount;
            this.meals = meals;
        }
    }


    @Data
    public static class DayInfoUpdateRequest {
        private Integer walkCount;
        private Double bodyWeight;

        @Builder
        public DayInfoUpdateRequest(Integer walkCount, Double bodyWeight) {
            this.walkCount = walkCount;
            this.bodyWeight = bodyWeight;
        }
    }
}
