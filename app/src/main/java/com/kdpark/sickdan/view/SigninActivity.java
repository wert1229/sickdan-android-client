package com.kdpark.sickdan.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.auth.authorization.accesstoken.AccessToken;
import com.kakao.util.exception.KakaoException;
import com.kdpark.sickdan.R;
import com.kdpark.sickdan.databinding.ActivitySigninBinding;
import com.kdpark.sickdan.util.OAuthUtil;
import com.kdpark.sickdan.util.SharedDataUtil;
import com.kdpark.sickdan.viewmodel.SignViewModel;
import com.kdpark.sickdan.viewmodel.common.BundleViewModelFactory;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

public class SigninActivity extends AppCompatActivity {

    private ActivitySigninBinding binding;
    private SignViewModel viewModel;

    private Context mContext;
    private OAuthLogin mNaverLoginModule;
    private Session mKakaoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signin);
        viewModel = new ViewModelProvider(this,
                BundleViewModelFactory.getInstance(getApplication(), getIntent().getExtras()))
                .get(SignViewModel.class);

        initData();
        initView();
        initObserver();
    }

    private void initData() {
        mContext = getApplicationContext();
        mNaverLoginModule = OAuthLogin.getInstance();
        mNaverLoginModule.init(
                mContext
                ,OAuthUtil.NAVER_CLIENT_ID
                ,OAuthUtil.NAVER_CLIENT_SECRET
                ,OAuthUtil.CLIENT_NAME
        );

        mKakaoSession = Session.getCurrentSession();
        mKakaoSession.addCallback (new ISessionCallback() {
            @Override
            public void onSessionOpened() {
                AccessToken tokenInfo = mKakaoSession.getTokenInfo();
                viewModel.kakaoLogin(
                        tokenInfo.getAccessToken(),
                        tokenInfo.getRefreshToken(),
                        OAuthUtil.getExpireSec(tokenInfo.accessTokenExpiresAt())
                );
            }

            @Override
            public void onSessionOpenFailed(KakaoException exception) { Log.e("PKD", "KAKAO ERROR", exception); }
        });
    }

    @SuppressLint("HandlerLeak")
    private void initView() {
        // 로그인 버튼
        binding.actSigninBtnSignin.setOnClickListener(v -> {
            if (!checkInputAndFocus()) return;
            viewModel.signin(binding.actSigninEdId.getText().toString(), binding.actSigninEdPassword.getText().toString());
        });

        // 회원가입 버튼
        binding.actSigninBtnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, SignupActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.in_left, R.anim.out_left);
        });

        // 네이버 버튼
        binding.actSigninBtnNaver.setOAuthLoginHandler(new OAuthLoginHandler() {
            @Override
            public void run(boolean success) {
                if (success) {
                    String accessToken = mNaverLoginModule.getAccessToken(mContext);
                    String refreshToken = mNaverLoginModule.getRefreshToken(mContext);
                    long expiresAt = mNaverLoginModule.getExpiresAt(mContext);
                    String tokenType = mNaverLoginModule.getTokenType(mContext);

                    viewModel.naverLogin(accessToken, refreshToken, expiresAt, tokenType);
                } else {
                    String errorCode = mNaverLoginModule.getLastErrorCode(mContext).getCode();
                    String errorDesc = mNaverLoginModule.getLastErrorDesc(mContext);
                    Toast.makeText(mContext, "errorCode:" + errorCode
                            + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 카카오 버튼
        binding.actSigninBtnKakao.setOnClickListener(v -> {
            mKakaoSession.open(AuthType.KAKAO_TALK, this);
        });
    }

    private void initObserver() {
        viewModel.token.observe(this, token -> {
            getSharedPreferences(SharedDataUtil.JWT_INFO, MODE_PRIVATE);
            SharedDataUtil.setData(SharedDataUtil.JWT_ACCESS_TOKEN, token);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        viewModel.showToast.observe(this, event -> {
            Toast.makeText(this, event.getValue(), Toast.LENGTH_LONG).show();
        });
    }

    private boolean checkInputAndFocus() {
        if (binding.actSigninEdId.getText().length() == 0) {
            binding.actSigninEdId.post(() -> {
                binding.actSigninEdId.requestFocus();

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(binding.actSigninEdId,0);
            });
            return false;
        }

        if (binding.actSigninEdPassword.getText().length() == 0) {
            binding.actSigninEdPassword.post(() -> {
                binding.actSigninEdPassword.requestFocus();

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(binding.actSigninEdPassword,0);
            });
            return false;
        }

        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
