package com.kdpark.sickdan.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class SignUpForm {
    private String email;
    private String password;
    private String displayName;

    @Builder
    public SignUpForm(String email, String password, String displayName) {
        this.email = email;
        this.password = password;
        this.displayName = displayName;
    }
}
