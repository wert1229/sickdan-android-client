package com.kdpark.sickdan.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.databinding.ActivitySigninBinding;
import com.kdpark.sickdan.util.SharedDataUtil;
import com.kdpark.sickdan.viewmodel.SignViewModel;

public class SigninActivity extends AppCompatActivity {

    private ActivitySigninBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signin);

        SignViewModel viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(SignViewModel.class);

        viewModel.getToken().observe(this, token -> {
            binding.actSigninTvToken.setText(token);
            SharedDataUtil.setData(SharedDataUtil.JWT_TOKEN, token);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        viewModel.getShowToast().observe(this, event -> {
            Toast.makeText(this, event.getValue(), Toast.LENGTH_LONG).show();
        });

        binding.actSigninBtnSignin.setOnClickListener(v -> {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish();
            viewModel.signin(binding.actSigninEdEmail.getText().toString(), binding.actSigninEdPassword.getText().toString());
        });

        binding.actSigninBtnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
            startActivity(intent);
        });
    }
}
