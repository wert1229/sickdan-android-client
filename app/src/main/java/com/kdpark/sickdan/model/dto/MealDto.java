package com.kdpark.sickdan.model.dto;

import com.kdpark.sickdan.model.dto.enums.MealCategory;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MealDto {
    @Data
    @NoArgsConstructor
    public static class Meal {
        private Long id;
        private String description;
        private MealCategory category;
        private List<MealPhoto> photos;

        @Builder
        public Meal(Long id, String description, MealCategory category, List<MealPhoto> photos) {
            this.id = id;
            this.description = description;
            this.category = category;
            this.photos = photos;
        }
    }

    @Data
    @NoArgsConstructor
    public static class MealPhoto {
        private Long id;
        private String fileOriginName;
        private String fileName;
        private String fileUrl;

        @Builder
        public MealPhoto(Long id, String fileOriginName, String fileName, String fileUrl) {
            this.id = id;
            this.fileOriginName = fileOriginName;
            this.fileName = fileName;
            this.fileUrl = fileUrl;
        }
    }

    @Data
    public static class MealAddRequest {
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

    @Data
    public static class MealEditRequest {
        private String description;

        @Builder
        public MealEditRequest(String description) {
            this.description = description;
        }
    }
}
