package com.kdpark.sickdan.model.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CommentDto {
    @Data
    @NoArgsConstructor
    public static class Comment {
        private Long id;
        private String displayName;
        private String description;
        private String createdDateTime;
        private Long parentCommentId;
        private List<Comment> replies;

        @Builder
        public Comment(Long id, String displayName, String description, String createdDateTime, Long parentCommentId, List<Comment> replies) {
            this.id = id;
            this.displayName = displayName;
            this.description = description;
            this.createdDateTime = createdDateTime;
            this.parentCommentId = parentCommentId;
            this.replies = replies;
        }
    }

    @Data
    public static class CommentWriteRequest {
        private String description;
        private Long parentId;

        @Builder
        public CommentWriteRequest(String description, Long parentId) {
            this.description = description;
            this.parentId = parentId;
        }
    }
}
