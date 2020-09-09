package com.kdpark.sickdan.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class SignInForm {
    private String userId;
    private String password;

    @Builder
    public SignInForm(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}
