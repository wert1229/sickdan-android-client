package com.kdpark.sickdan.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kdpark.sickdan.model.ApiClient;
import com.kdpark.sickdan.model.service.AuthService;
import com.kdpark.sickdan.model.dto.SignInForm;
import com.kdpark.sickdan.model.dto.SignUpForm;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Getter
public class SignViewModel extends ViewModel {

    //== Data ==//
    private MutableLiveData<String> token = new MutableLiveData<>();

    //== Event ==//
    private MutableLiveData<Event<String>> showToast = new MutableLiveData<>();

    public void signup(String email, String password, String displayName) {
        SignUpForm signUpForm = SignUpForm.builder()
                .email(email)
                .password(password)
                .displayName(displayName)
                .build();

        ApiClient.getService(AuthService.class).signUp(signUpForm).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("PKD", response.toString());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("PKD", t.toString());
            }
        });
    }

    public void signin(String email, String password) {
        SignInForm signInForm = SignInForm.builder()
                .email(email)
                .password(password)
                .build();

        ApiClient.getService(AuthService.class).signIn(signInForm).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 400) {
                    ErrorResponse error = ApiClient.getErrorResponse(response);
                    showToast.setValue(new Event<>(error.getMessage()));
                    return;
                }

                String jwtToken = response.headers().get("X-AUTH-TOKEN");
                token.setValue(jwtToken);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("PKD", t.toString());
            }
        });
    }
}
