package com.kdpark.sickdan.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

    public static final int PERMISSION_REQUEST_CODE = 1;

    private Handler mHandler = new Handler();
    private ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro);

        askPermision();
    }

    private void askPermision() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR)) {
            doNext();
        }

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            doNext();
        } else {
            String[] permission = { Manifest.permission.ACTIVITY_RECOGNITION };
            ActivityCompat.requestPermissions(this, permission, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            doNext();
        }
    }

    private void doNext() {
        Intent serviceIntent = new Intent(this, StepService.class);
        startService(serviceIntent);

        ApiClient.init(getApplication());

        SharedPreferences sp = getApplication().getSharedPreferences(SharedDataUtil.JWT_INFO, MODE_PRIVATE);
        String accessToken = sp.getString(SharedDataUtil.JWT_ACCESS_TOKEN, "");
        String refreshToken = sp.getString(SharedDataUtil.JWT_REFRESH_TOKEN, "");

        Intent next;
        accessToken = "";
        refreshToken = "";
        if ("".equals(accessToken) && "".equals(refreshToken))
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
