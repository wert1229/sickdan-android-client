package com.kdpark.sickdan.view.control.meallist;

import android.content.Intent;

import com.kdpark.sickdan.model.dto.MealCategory;
import com.kdpark.sickdan.model.dto.MealPhotoDto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
public class MealItem {
    private Long id;
    private String description;
    private MealCategory category;
    private MealCellType type;
    private List<MealPhotoDto> photos;

    @Builder
    public MealItem(Long id, String description, MealCategory category, MealCellType type, List<MealPhotoDto> photos) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.type = type;
        this.photos = photos;
    }
}
