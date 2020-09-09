package com.kdpark.sickdan.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FriendSearchDto {
    private Long id;
    private String email;
    private String displayName;
    private RelationshipStatus status;

    @Builder
    public FriendSearchDto(Long id, String email, String displayName, RelationshipStatus status) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.status = status;
    }
}
