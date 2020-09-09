package com.kdpark.sickdan.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class OAuthTokenInfoDto {
    private String accessToken;
    private String refreshToken;
    private long expiresAt;
    private String tokenType;

    @Builder
    public OAuthTokenInfoDto(String accessToken, String refreshToken, long expiresAt, String tokenType) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.tokenType = tokenType;
    }
}
