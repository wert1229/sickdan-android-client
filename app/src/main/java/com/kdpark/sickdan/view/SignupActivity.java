package com.kdpark.sickdan.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.databinding.ActivitySignupBinding;
import com.kdpark.sickdan.viewmodel.SignViewModel;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);

        SignViewModel viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(SignViewModel.class);

        binding.actSignupBtnSignup.setOnClickListener(v -> viewModel.signup(
                binding.actSignupEdEmail.getText().toString(),
                binding.actSignupEdPassword.getText().toString(),
                binding.actSignupEdDisplayname.getText().toString()
        ));
    }
}