package com.kdpark.sickdan.model.service;

import com.kdpark.sickdan.model.dto.SignInForm;
import com.kdpark.sickdan.model.dto.SignUpForm;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    @POST("v1/signup")
    Call<Void> signUp(@Body SignUpForm signUpForm);

    @POST("v1/signin")
    Call<Void> signIn(@Body SignInForm signInForm);
}
