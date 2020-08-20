package com.kdpark.sickdan.model.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MealDto {
    private String description;
    private MealCategory category;
    private List<MealPhotoDto> photoList;

    @Builder
    public MealDto(String description, MealCategory category, List<MealPhotoDto> photoList) {
        this.description = description;
        this.category = category;
        this.photoList = photoList;
    }
}
