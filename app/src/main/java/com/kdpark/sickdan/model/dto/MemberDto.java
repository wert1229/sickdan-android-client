package com.kdpark.sickdan.model.dto;

import lombok.Data;

@Data
public class MemberDto {
    private String id;
    private String email;
    private String password;
    private String displayName;
}
