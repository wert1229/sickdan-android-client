package com.kdpark.sickdan.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class SignUpForm {
    private String userId;
    private String password;
    private String displayName;
    private String email;

    @Builder
    public SignUpForm(String userId, String password, String displayName, String email) {
        this.userId = userId;
        this.password = password;
        this.displayName = displayName;
        this.email = email;
    }
}
