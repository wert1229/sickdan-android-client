package com.kdpark.sickdan.viewmodel;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kdpark.sickdan.model.ApiClient;
import com.kdpark.sickdan.model.dto.OAuthTokenInfoDto;
import com.kdpark.sickdan.model.service.AuthService;
import com.kdpark.sickdan.model.dto.SignInForm;
import com.kdpark.sickdan.model.dto.SignUpForm;
import com.kdpark.sickdan.model.service.MemberService;
import com.kdpark.sickdan.viewmodel.common.ErrorResponse;
import com.kdpark.sickdan.viewmodel.common.Event;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignViewModel extends ViewModel {

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
    public final MutableLiveData<Event<String>> onSigninSuccess = new MutableLiveData<>();

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

        ApiClient.getService(MemberService.class).checkDuplicate(userId).enqueue(new Callback<Map<String, Boolean>>() {
            @Override
            public void onResponse(Call<Map<String, Boolean>> call, Response<Map<String, Boolean>> response) {
                boolean exist = response.body().get("exist");
                isUserIdOk.setValue(!exist);
            }

            @Override
            public void onFailure(Call<Map<String, Boolean>> call, Throwable t) {

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
        SignUpForm signUpForm = SignUpForm.builder()
                .userId(userId.getValue())
                .password(password.getValue())
                .displayName(displayName.getValue())
                .email(email.getValue())
                .build();

        ApiClient.getService(AuthService.class).signUp(signUpForm).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) return;
                onSigninSuccess.setValue(new Event<>("정상적으로 가입되었습니다"));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("PKD", t.toString());
            }
        });
    }

    public void signin(String userId, String password) {
        SignInForm signInForm = SignInForm.builder()
                .userId(userId)
                .password(password)
                .build();

        ApiClient.getService(AuthService.class).signIn(signInForm).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
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

    public void naverLogin(String accessToken, String refreshToken, long expiresAt, String tokenType) {
        OAuthTokenInfoDto info = OAuthTokenInfoDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresAt(expiresAt)
                .tokenType(tokenType)
                .build();

        ApiClient.getService(AuthService.class).authNaver(info).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    ErrorResponse error = ApiClient.getErrorResponse(response);
                    showToast.setValue(new Event<>(error.getMessage()));
                    return;
                }

                String jwtToken = response.headers().get("X-AUTH-TOKEN");
                token.setValue(jwtToken);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    public void kakaoLogin(String accessToken, String refreshToken, long expiresAt) {
        OAuthTokenInfoDto info = OAuthTokenInfoDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresAt(expiresAt)
                .build();

        ApiClient.getService(AuthService.class).authKakao(info).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    ErrorResponse error = ApiClient.getErrorResponse(response);
                    showToast.setValue(new Event<>(error.getMessage()));
                    return;
                }

                String jwtToken = response.headers().get("X-AUTH-TOKEN");
                token.setValue(jwtToken);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}
