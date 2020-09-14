package com.kdpark.sickdan.view;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.databinding.ActivitySignupBinding;
import com.kdpark.sickdan.viewmodel.SignViewModel;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private SignViewModel viewModel;

    private Handler handler = new Handler();
    private Runnable finishInput;

    private long userIdCheckDelay = 600;

    private Drawable eyeClosed;
    private Drawable eyeOpened;
    private Drawable checkOk;
    private Drawable checkFail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(SignViewModel.class);

        initData();
        initView();
        initObserver();

        viewModel.validateInput();
    }

    private void initData() {
        finishInput = () -> {
            String userId = binding.actSignupEdId.getText().toString();
            viewModel.checkIdDuplicate(userId);
        };

        eyeClosed = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_closed_eye, null);
        eyeOpened = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_open_eye, null);
        checkOk = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_checked, null);
        checkFail = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_close, null);
    }

    private void initView() {
        binding.actSignupBtnSignup.setOnClickListener(v -> viewModel.signup());

        binding.actSignupEdId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(finishInput);
                binding.actSignupImgId.setVisibility(View.INVISIBLE);

                if (s.length() <= 0) return;
                handler.postDelayed(finishInput, userIdCheckDelay);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.actSignupEdEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.actSignupImgEmail.setVisibility(View.INVISIBLE);

                if (s.length() == 0) return;
                viewModel.checkEmailRegex(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.actSignupEdPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) viewModel.password.setValue(((EditText)v).getText().toString());
        });

        binding.actSignupEdPasswordRe.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) viewModel.passwordRe.setValue(((EditText)v).getText().toString());
        });

        binding.actSignupEdDisplayname.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) viewModel.displayName.setValue(((EditText)v).getText().toString());
        });

        binding.actSignupImgPassword.setOnClickListener(v -> {
            viewModel.togglePasswordVisible();
        });
    }

    private void initObserver() {
        viewModel.isUserIdOk.observe(this, isOk -> {
            if (binding.actSignupEdId.getText().toString().isEmpty())
                binding.actSignupImgId.setVisibility(View.INVISIBLE);
            else
                binding.actSignupImgId.setVisibility(View.VISIBLE);

            if (isOk)
                binding.actSignupImgId.setImageDrawable(checkOk);
            else
                binding.actSignupImgId.setImageDrawable(checkFail);

            viewModel.validateInput();
        });

        viewModel.isEmailOk.observe(this, isOk -> {
            if (binding.actSignupEdEmail.getText().toString().isEmpty())
                binding.actSignupImgEmail.setVisibility(View.INVISIBLE);
            else
                binding.actSignupImgEmail.setVisibility(View.VISIBLE);

            if (isOk)
                binding.actSignupImgEmail.setImageDrawable(checkOk);
            else
                binding.actSignupImgEmail.setImageDrawable(checkFail);

            viewModel.validateInput();
        });

        viewModel.password.observe(this, text -> {
            viewModel.validateInput();
        });

        viewModel.passwordRe.observe(this, text -> {
            viewModel.validateInput();
        });

        viewModel.displayName.observe(this, text -> {
            viewModel.validateInput();
        });

        viewModel.isValidInput.observe(this, isVaild ->
                binding.actSignupBtnSignup.setEnabled(isVaild));

        viewModel.isPasswordVisible.observe(this, visible -> {
            if (visible) {
                binding.actSignupImgPassword.setImageDrawable(eyeClosed);
                binding.actSignupEdPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                binding.actSignupEdPasswordRe.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                binding.actSignupImgPassword.setImageDrawable(eyeOpened);
                binding.actSignupEdPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.actSignupEdPasswordRe.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }

            binding.actSignupEdPassword.setSelection(binding.actSignupEdPassword.getText().length());
            binding.actSignupEdPasswordRe.setSelection(binding.actSignupEdPasswordRe.getText().length());
        });

        viewModel.onSignupSuccess.observe(this, message -> {
            Toast.makeText(this, message.getValueIfNotHandledOrNull(), Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_right, R.anim.out_right);
    }
}