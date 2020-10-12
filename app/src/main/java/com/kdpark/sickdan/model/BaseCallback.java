package com.kdpark.sickdan.model;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.kdpark.sickdan.model.dto.MemberDto;
import com.kdpark.sickdan.model.service.AuthService;
import com.kdpark.sickdan.util.SharedDataUtil;
import com.kdpark.sickdan.view.SigninActivity;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Map;
import java.util.Queue;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public abstract class BaseCallback<T> implements Callback<T> {

    private Context context;
    private Callback<T> originCallback;

    private static boolean isRefreshProcessing;
    private static Queue<Pair<Call, Callback>> jobQueue = new ArrayDeque<>();

    public BaseCallback(Context context) {
        this.context = context;
        this.originCallback = this;
    }

    @Override
    public void onResponse(@NonNull Call<T> originCall, Response<T> response) {
        if (response.code() == 401 || response.code() == 403) {
            jobQueue.add(new Pair<>(originCall, this));
            if (!isRefreshProcessing) refreshAccessToken();

            return;
        }

        onResponse(response);
    }

    private void refreshAccessToken() {
        SharedPreferences sp = context.getSharedPreferences(SharedDataUtil.JWT_INFO, MODE_PRIVATE);
        String refreshToken = sp.getString(SharedDataUtil.JWT_REFRESH_TOKEN, "");

        if ("".equals(refreshToken)) {
            onRefreshFailure();
            return;
        }

        MemberDto.TokenRefreshRequest request = MemberDto.TokenRefreshRequest.builder()
                .refreshToken(refreshToken)
                .build();

        isRefreshProcessing = true;

        ApiClient.getService(context, AuthService.class).refreshToken(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    onRefreshFailure();
                    return;
                }

                String accessToken = response.headers().get(ApiClient.ACCESS_TOKEN_HEADER);

                SharedPreferences sp = context.getSharedPreferences(SharedDataUtil.JWT_INFO, Context.MODE_PRIVATE);
                sp.edit().putString(SharedDataUtil.JWT_ACCESS_TOKEN, accessToken).apply();

                isRefreshProcessing = false;
                flushJobQueue();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                onRefreshFailure();
                isRefreshProcessing = false;
            }
        });

    }

    private void flushJobQueue() {
        while (!jobQueue.isEmpty()) {
            Pair<Call, Callback> job = jobQueue.poll();
            job.first.clone().enqueue(job.second);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFailure(t);
    }

    public void onRefreshFailure() {
        jobQueue.clear();
        openLogin();
    }

    public abstract void onResponse(Response<T> response);

    public abstract void onFailure(Throwable t);

    private void openLogin() {
        Intent intent = new Intent(context, SigninActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("EXIT", true);

        context.startActivity(intent);
    }
}
