package com.kdpark.sickdan.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.databinding.ActivityIntroBinding;

public class IntroActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();
    private ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro);

        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mHandler.post(() -> binding.actIntroPbLoading.setProgress(binding.actIntroPbLoading.getProgress() + 1));
            }

            Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
            startActivity(intent);
            finish();
        }).start();
    }
}
