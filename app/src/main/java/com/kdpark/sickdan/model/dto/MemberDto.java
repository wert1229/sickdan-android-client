package com.kdpark.sickdan.model.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDto {
    private String id;
    private String email;
    private String displayName;
    private List<MemberRelationshipDto> relationships;

    @Builder
    public MemberDto(String id, String email, String displayName, List<MemberRelationshipDto> relationships) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.relationships = relationships;
    }
}
