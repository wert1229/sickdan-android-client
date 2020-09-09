package com.kdpark.sickdan.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.databinding.ActivityIntroBinding;
import com.kdpark.sickdan.util.service.StepService;

public class IntroActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();
    private ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro);

        Intent serviceIntent = new Intent(this, StepService.class);
        startService(serviceIntent);

        mHandler.postDelayed(() -> {
            Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
            startActivity(intent);
            finish();
        }, 1000);
    }

    @Override
    public void onBackPressed() {}
}
