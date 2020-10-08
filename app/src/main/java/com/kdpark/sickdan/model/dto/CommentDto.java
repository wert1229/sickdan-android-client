package com.kdpark.sickdan.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String displayName;
    private String description;
    private String createdDateTime;
    private Long parentCommentId;
    private List<CommentDto> replies;

    @Builder

    public CommentDto(Long id, String displayName, String description, String createdDateTime, Long parentCommentId, List<CommentDto> replies) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.createdDateTime = createdDateTime;
        this.parentCommentId = parentCommentId;
        this.replies = replies;
    }
}
