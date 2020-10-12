package com.kdpark.sickdan.view.control.meallist;

import com.kdpark.sickdan.model.dto.enums.MealCategory;
import com.kdpark.sickdan.model.dto.MealDto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
public class MealItem {
    private Long id;
    private String description;
    private MealCategory category;
    private MealCellType type;
    private List<MealDto.MealPhoto> photos;

    @Builder
    public MealItem(Long id, String description, MealCategory category, MealCellType type, List<MealDto.MealPhoto> photos) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.type = type;
        this.photos = photos;
    }
}
