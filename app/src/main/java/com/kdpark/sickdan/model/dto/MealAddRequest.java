package com.kdpark.sickdan.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class MealAddRequest {
    private String date;
    private String description;
    private MealCategory category;
    private Integer order;

    @Builder
    public MealAddRequest(String date, String description, MealCategory category, Integer order) {
        this.date = date;
        this.description = description;
        this.category = category;
        this.order = order;
    }
}
