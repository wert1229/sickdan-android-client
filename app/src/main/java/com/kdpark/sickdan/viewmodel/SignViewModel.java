package com.kdpark.sickdan.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kdpark.sickdan.model.ApiClient;
import com.kdpark.sickdan.model.BaseCallback;
import com.kdpark.sickdan.model.dto.MemberDto;
import com.kdpark.sickdan.model.service.AuthService;
import com.kdpark.sickdan.model.service.MemberService;
import com.kdpark.sickdan.util.SharedDataUtil;
import com.kdpark.sickdan.viewmodel.common.BundleViewModel;
import com.kdpark.sickdan.viewmodel.common.ErrorResponse;
import com.kdpark.sickdan.viewmodel.common.Event;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignViewModel extends BundleViewModel {

    //== Data ==//
    public final MutableLiveData<String> token = new MutableLiveData<>();

    public final MutableLiveData<Boolean> isValidInput = new MutableLiveData<>(false);
    public final MutableLiveData<Boolean> isUserIdOk = new MutableLiveData<>(false);
    public final MutableLiveData<Boolean> isEmailOk = new MutableLiveData<>(false);
    public final MutableLiveData<Boolean> isPasswordVisible = new MutableLiveData<>(false);

    public final MutableLiveData<String> userId = new MutableLiveData<>("");
    public final MutableLiveData<String> password = new MutableLiveData<>("");
    public final MutableLiveData<String> passwordRe = new MutableLiveData<>("");
    public final MutableLiveData<String> displayName = new MutableLiveData<>("");
    public final MutableLiveData<String> email = new MutableLiveData<>("");

    //== Event ==//
    public final MutableLiveData<Event<String>> showToast = new MutableLiveData<>();
    public final MutableLiveData<Event<String>> onSignupSuccess = new MutableLiveData<>();
    public final MutableLiveData<Event<String>> onSigninSuccess = new MutableLiveData<>();

    public SignViewModel(@NonNull Application application, Bundle bundle) {
        super(application, bundle);
    }

    public void togglePasswordVisible() {
        isPasswordVisible.setValue(!isPasswordVisible.getValue());
    }

    public void validateInput() {
        boolean isUserIdOK = isUserIdOk.getValue();
        boolean isEmailOK = isEmailOk.getValue();
        String userIdText = userId.getValue();
        String passwordText = password.getValue();
        String passwordReText = passwordRe.getValue();
        String displayNameText = displayName.getValue();
        String emailText= email.getValue();

        boolean enabled = true;

        if (!isUserIdOK || userIdText.isEmpty()) enabled = false;
        if (passwordText.isEmpty() || passwordReText.isEmpty()) enabled = false;
        if (!passwordText.equals(passwordReText)) enabled = false;
        if (displayNameText.isEmpty()) enabled = false;
        if (!isEmailOK || emailText.isEmpty()) enabled = false;

        isValidInput.setValue(enabled);
    }

    public void checkIdDuplicate(String userId) {
        this.userId.setValue(userId);

        ApiClient.getService(getApplication(), MemberService.class).checkDuplicate(userId).enqueue(new BaseCallback<Map<String, Boolean>>(getApplication()) {
            @Override
            public void onResponse(Response<Map<String, Boolean>> response) {
                if (!response.isSuccessful()) return;
                boolean exist = response.body() != null ? response.body().get("exist") : true;
                isUserIdOk.setValue(!exist);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void checkEmailRegex(String email) {
        this.email.setValue(email);
        Pattern emailPattern = Pattern.compile("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$");
        Matcher m = emailPattern.matcher(email);

        isEmailOk.setValue(m.matches());
    }

    public void signup() {
        MemberDto.SignUpForm signUpForm = MemberDto.SignUpForm.builder()
                .userId(userId.getValue())
                .password(password.getValue())
                .displayName(displayName.getValue())
                .email(email.getValue())
                .build();

        ApiClient.getService(getApplication(), AuthService.class).signUp(signUpForm).enqueue(new BaseCallback<Void>(getApplication()) {
            @Override
            public void onResponse(Response<Void> response) {
                if (!response.isSuccessful()) return;
                onSignupSuccess.setValue(new Event<>("정상적으로 가입되었습니다"));
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("PKD", t.toString());
            }
        });
    }

    public void signin(String userId, String password) {
        MemberDto.SignInForm signInForm = MemberDto.SignInForm.builder()
                .userId(userId)
                .password(password)
                .build();

        ApiClient.getService(getApplication(), AuthService.class).signIn(signInForm).enqueue(new BaseCallback<Void>(getApplication()) {
            @Override
            public void onResponse(Response<Void> response) {
                if (!response.isSuccessful()) {
                    ErrorResponse error = ApiClient.getErrorResponse(response);
                    showToast.setValue(new Event<>(error.getMessage()));
                    return;
                }

                String accessToken = response.headers().get(ApiClient.ACCESS_TOKEN_HEADER);
                String refreshToken = response.headers().get(ApiClient.REFRESH_TOKEN_HEADER);

                SharedPreferences sp = getApplication().getSharedPreferences(SharedDataUtil.JWT_INFO, Context.MODE_PRIVATE);
                sp.edit().putString(SharedDataUtil.JWT_ACCESS_TOKEN, accessToken)
                        .putString(SharedDataUtil.JWT_REFRESH_TOKEN, refreshToken)
                        .apply();

                token.setValue(accessToken);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("PKD", t.toString());
            }
        });
    }

    public void naverLogin(String accessToken, String refreshToken, long expiresAt, String tokenType) {
        MemberDto.OAuthTokenInfo info = MemberDto.OAuthTokenInfo.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresAt(expiresAt)
                .tokenType(tokenType)
                .build();

        ApiClient.getService(getApplication(), AuthService.class).authNaver(info).enqueue(new BaseCallback<Void>(getApplication()) {
            @Override
            public void onResponse(Response<Void> response) {
                if (!response.isSuccessful()) {
                    ErrorResponse error = ApiClient.getErrorResponse(response);
                    showToast.setValue(new Event<>(error.getMessage()));
                    return;
                }

                String accessToken = response.headers().get(ApiClient.ACCESS_TOKEN_HEADER);
                String refreshToken = response.headers().get(ApiClient.REFRESH_TOKEN_HEADER);

                SharedPreferences sp = getApplication().getSharedPreferences(SharedDataUtil.JWT_INFO, Context.MODE_PRIVATE);
                sp.edit().putString(SharedDataUtil.JWT_ACCESS_TOKEN, accessToken)
                        .putString(SharedDataUtil.JWT_REFRESH_TOKEN, refreshToken)
                        .apply();

                token.setValue(accessToken);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void kakaoLogin(String accessToken, String refreshToken, long expiresAt) {
        MemberDto.OAuthTokenInfo info = MemberDto.OAuthTokenInfo.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresAt(expiresAt)
                .build();

        ApiClient.getService(getApplication(), AuthService.class).authKakao(info).enqueue(new BaseCallback<Void>(getApplication()) {
            @Override
            public void onResponse(Response<Void> response) {
                if (!response.isSuccessful()) {
                    ErrorResponse error = ApiClient.getErrorResponse(response);
                    showToast.setValue(new Event<>(error.getMessage()));
                    return;
                }

                String accessToken = response.headers().get(ApiClient.ACCESS_TOKEN_HEADER);
                String refreshToken = response.headers().get(ApiClient.REFRESH_TOKEN_HEADER);

                SharedPreferences sp = getApplication().getSharedPreferences(SharedDataUtil.JWT_INFO, Context.MODE_PRIVATE);
                sp.edit().putString(SharedDataUtil.JWT_ACCESS_TOKEN, accessToken)
                        .putString(SharedDataUtil.JWT_REFRESH_TOKEN, refreshToken)
                        .apply();

                token.setValue(accessToken);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

}
