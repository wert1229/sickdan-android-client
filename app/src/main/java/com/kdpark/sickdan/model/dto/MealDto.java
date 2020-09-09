package com.kdpark.sickdan.model.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MealDto {
    private Long id;
    private String description;
    private MealCategory category;
    private List<MealPhotoDto> photos;

    @Builder
    public MealDto(Long id, String description, MealCategory category, List<MealPhotoDto> photos) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.photos = photos;
    }
}
