package com.kdpark.sickdan.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MealPhotoDto {
    private String fileName;
    private String filePath;

    @Builder
    public MealPhotoDto(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }
}
