package com.kdpark.sickdan.model.service;

import com.kdpark.sickdan.model.dto.MemberDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    @POST("v1/signup")
    Call<Void> signUp(@Body MemberDto.SignUpForm signUpForm);

    @POST("v1/signin")
    Call<Void> signIn(@Body MemberDto.SignInForm signInForm);

    @POST("v1/oauth/naver")
    Call<Void> authNaver(@Body MemberDto.OAuthTokenInfo tokenInfo);

    @POST("v1/oauth/kakao")
    Call<Void> authKakao(@Body MemberDto.OAuthTokenInfo tokenInfo);

    @POST("v1/token/refresh")
    Call<Void> refreshToken(@Body MemberDto.TokenRefreshRequest request);
}
