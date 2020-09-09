package com.kdpark.sickdan.model.service;

import com.kdpark.sickdan.model.dto.OAuthTokenInfoDto;
import com.kdpark.sickdan.model.dto.SignInForm;
import com.kdpark.sickdan.model.dto.SignUpForm;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    @POST("v1/signup")
    Call<Void> signUp(@Body SignUpForm signUpForm);

    @POST("v1/signin")
    Call<Void> signIn(@Body SignInForm signInForm);

    @POST("v1/oauth/naver")
    Call<Void> authNaver(@Body OAuthTokenInfoDto tokenInfo);

    @POST("v1/oauth/kakao")
    Call<Void> authKakao(@Body OAuthTokenInfoDto tokenInfo);
}
