package com.kdpark.sickdan.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kdpark.sickdan.model.ApiClient;
import com.kdpark.sickdan.model.service.DailyService;
import com.kdpark.sickdan.model.dto.DailyDto;
import com.kdpark.sickdan.model.dto.MemberDto;
import com.kdpark.sickdan.model.service.MemberService;
import com.kdpark.sickdan.util.CalendarUtil;
import com.kdpark.sickdan.view.control.calendar.CalendarCell;
import com.kdpark.sickdan.viewmodel.common.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends AndroidViewModel {

    public final MutableLiveData<MemberDto> member = new MutableLiveData<>();

    //== Event ==//
    public final MutableLiveData<Event<List<String>>> syncComplete = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public void getMyInfo() {
        ApiClient.getService(MemberService.class).getAuthMember().enqueue(new Callback<MemberDto>() {
            @Override
            public void onResponse(Call<MemberDto> call, Response<MemberDto> response) {
                member.setValue(response.body());
            }

            @Override
            public void onFailure(Call<MemberDto> call, Throwable t) {

            }
        });
    }

    public void uploadPrevWalkCount(Map<String, ?> map) {
        if (map.size() == 0) {
            syncComplete.setValue(new Event<>(new ArrayList<>()));
            return;
        }

        Map<String, Integer> params = new HashMap<>();

        final int MAX_COUNT = 5;
        int count = 0;

        for (String key : map.keySet()) {
            Object value = map.get(key);

            if (!(value instanceof Integer))
                map.remove(key);
            else if (key.compareTo(CalendarUtil.getTodayString()) < 0) {
                params.put(key, (Integer) value);
                count++;
            }

            if (count > MAX_COUNT) break;
        }

        ApiClient.getService(DailyService.class).syncWalkCount(params).enqueue(new Callback<Map<String, List<String>>>() {
            @Override
            public void onResponse(Call<Map<String, List<String>>> call, Response<Map<String, List<String>>> response) {
                if (!response.isSuccessful()) {
                    syncComplete.setValue(new Event<>(new ArrayList<>()));
                    return;
                }

                List<String> doneDateList = response.body().get("data");

                syncComplete.setValue(new Event<>(doneDateList != null ? doneDateList: new ArrayList<>()));
            }

            @Override
            public void onFailure(Call<Map<String, List<String>>> call, Throwable t) {
                syncComplete.setValue(new Event<>(new ArrayList<>()));
            }
        });
    }
}
