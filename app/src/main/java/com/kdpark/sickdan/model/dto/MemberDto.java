package com.kdpark.sickdan.model.dto;

import com.kdpark.sickdan.model.dto.enums.RelationshipStatus;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MemberDto {
    @Data
    @NoArgsConstructor
    public static class Member {
        private String id;
        private String email;
        private String displayName;
        private List<MemberRelationship> relationships;

        @Builder
        public Member(String id, String email, String displayName, List<MemberRelationship> relationships) {
            this.id = id;
            this.email = email;
            this.displayName = displayName;
            this.relationships = relationships;
        }
    }

    @Data
    public static class MemberRelationship {
        private Long id;
        private String displayName;
        private String email;
        private RelationshipStatus status;
    }

    @Data
    @NoArgsConstructor
    public static class FriendSearchResult {
        private Long id;
        private String email;
        private String displayName;
        private RelationshipStatus status;

        @Builder
        public FriendSearchResult(Long id, String email, String displayName, RelationshipStatus status) {
            this.id = id;
            this.email = email;
            this.displayName = displayName;
            this.status = status;
        }
    }

    @Data
    public static class OAuthTokenInfo {
        private String accessToken;
        private String refreshToken;
        private long expiresAt;
        private String tokenType;

        @Builder
        public OAuthTokenInfo(String accessToken, String refreshToken, long expiresAt, String tokenType) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.expiresAt = expiresAt;
            this.tokenType = tokenType;
        }
    }

    @Data
    public static class SignInForm {
        private String userId;
        private String password;

        @Builder
        public SignInForm(String userId, String password) {
            this.userId = userId;
            this.password = password;
        }
    }

    @Data
    public static class SignUpForm {
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

    @Data
    public static class TokenRefreshRequest {
        private String refreshToken;

        @Builder
        public TokenRefreshRequest(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}
