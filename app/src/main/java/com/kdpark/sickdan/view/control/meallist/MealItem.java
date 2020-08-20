package com.kdpark.sickdan.view.control.meallist;

import com.kdpark.sickdan.model.dto.MealCategory;

import lombok.Builder;
import lombok.Data;

@Data
public class MealItem {
    private String description;
    private MealCategory category;
    private MealCellType type;

    @Builder
    public MealItem(String description, MealCategory category, MealCellType type) {
        this.description = description;
        this.category = category;
        this.type = type;
    }
}
