package com.kdpark.sickdan.util.service;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.kdpark.sickdan.model.ApiClient;
import com.kdpark.sickdan.model.BaseCallback;
import com.kdpark.sickdan.model.service.DailyService;
import com.kdpark.sickdan.util.CalendarUtil;
import com.kdpark.sickdan.util.SharedDataUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Response;

public class UploadStepWork {

    public static PeriodicWorkRequest getWork() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        return new PeriodicWorkRequest.Builder(UploadWork.class, 1, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build();
    }

    public static class UploadWork extends Worker {
        private SharedPreferences sp;
        public UploadWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
            sp = getApplicationContext().getSharedPreferences(SharedDataUtil.STEP_INFO, Context.MODE_PRIVATE);
        }

        @NonNull
        @Override
        public Result doWork() {
            Map<String, ?> map = sp.getAll();
            uploadWalkCount(map);

            return Result.success();
        }

        private void uploadWalkCount(Map<String, ?> map) {
            if (map.size() == 0) {
                return;
            }

            Map<String, Integer> params = new HashMap<>();

            final int MAX_COUNT = 5;
            int count = 0;

            for (String key : map.keySet()) {
                Object value = map.get(key);

                if (!(value instanceof Integer))
                    map.remove(key);
                else {
                    params.put(key, (Integer) value);
                    count++;
                }

                if (count > MAX_COUNT) break;
            }

            ApiClient.getService(DailyService.class).syncWalkCount(params).enqueue(new BaseCallback<Map<String, List<String>>>(getApplicationContext()) {
                @Override
                public void onResponse(Response<Map<String, List<String>>> response) {
                    if (!response.isSuccessful()) {
                        return;
                    }

                    List<String> doneDateList = response.body().get("data");

                    if (doneDateList == null) return;

                    SharedPreferences.Editor edit = sp.edit();

                    for (String date : doneDateList) {
                        if (date.equals(CalendarUtil.getTodayString())) continue;
                        edit.remove(date);
                    }

                    edit.apply();
                }

                @Override
                public void onFailure(Throwable t) {}

                @Override
                public void onRefreshFailure() {}
            });
        }
    }
}
