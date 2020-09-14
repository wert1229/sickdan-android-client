package com.kdpark.sickdan.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.work.WorkManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.Display;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.databinding.ActivityIntroBinding;
import com.kdpark.sickdan.model.ApiClient;
import com.kdpark.sickdan.util.SharedDataUtil;
import com.kdpark.sickdan.util.service.StepService;
import com.kdpark.sickdan.util.service.UploadStepWork;

public class IntroActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();
    private ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro);

        Intent serviceIntent = new Intent(this, StepService.class);
        startService(serviceIntent);

        ApiClient.init(getApplication());

        doNext();
    }

    private void doNext() {
        SharedPreferences sp = getApplication().getSharedPreferences(SharedDataUtil.JWT_INFO, MODE_PRIVATE);
        String accessToken = sp.getString(SharedDataUtil.JWT_ACCESS_TOKEN, "");
        accessToken = "";
        Intent next;

        if ("".equals(accessToken))
            next = new Intent(getApplicationContext(), SigninActivity.class);
        else
            next = new Intent(getApplicationContext(), MainActivity.class);

        mHandler.postDelayed(() -> {
            startActivity(next);
            finish();
        }, 1000);
    }

    @Override
    public void onBackPressed() {}
}
