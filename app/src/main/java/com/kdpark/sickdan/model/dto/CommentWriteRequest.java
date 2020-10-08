package com.kdpark.sickdan.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class CommentWriteRequest {
    private String description;
    private Long parentId;

    @Builder
    public CommentWriteRequest(String description, Long parentId) {
        this.description = description;
        this.parentId = parentId;
    }
}
