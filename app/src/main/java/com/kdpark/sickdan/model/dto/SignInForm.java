package com.kdpark.sickdan.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class SignInForm {
    private String email;
    private String password;

    @Builder
    public SignInForm(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
