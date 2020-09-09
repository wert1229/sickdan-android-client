package com.kdpark.sickdan.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MealPhotoDto {
    private Long id;
    private String fileOriginName;
    private String fileName;
    private String fileUrl;

    @Builder
    public MealPhotoDto(Long id, String fileOriginName, String fileName, String fileUrl) {
        this.id = id;
        this.fileOriginName = fileOriginName;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }
}
