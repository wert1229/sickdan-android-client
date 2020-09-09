package com.kdpark.sickdan.model.dto;

import lombok.Data;

@Data
public class MemberRelationshipDto {
    private Long id;
    private String displayName;
    private String email;
    private RelationshipStatus status;
}
