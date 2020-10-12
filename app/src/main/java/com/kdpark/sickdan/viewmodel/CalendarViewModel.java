package com.kdpark.sickdan.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kdpark.sickdan.model.ApiClient;
import com.kdpark.sickdan.model.BaseCallback;
import com.kdpark.sickdan.model.dto.DailyDto;
import com.kdpark.sickdan.model.service.DailyService;
import com.kdpark.sickdan.util.CalendarUtil;
import com.kdpark.sickdan.util.SharedDataUtil;
import com.kdpark.sickdan.view.control.calendar.CalendarCell;
import com.kdpark.sickdan.viewmodel.common.BundleViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class CalendarViewModel extends BundleViewModel {

    //== Data ==//
    public final MutableLiveData<Calendar> currentDate = new MutableLiveData<>();
    public final MutableLiveData<List<CalendarCell>> dailyList = new MutableLiveData<>();

    private long memberId;
    private int mode;

    public CalendarViewModel(@NonNull Application application, Bundle bundle) {
        super(application, bundle);

        if (bundle.containsKey(CalendarUtil.VIEW_MODE_KEY))
            this.mode = bundle.getInt(CalendarUtil.VIEW_MODE_KEY);
        if (bundle.containsKey("memberId"))
            this.memberId = bundle.getLong("memberId");
    }

    public void setMonth(Calendar calendar) {
        currentDate.setValue((Calendar) calendar.clone());
    }

    public void setCalendarData(Calendar calendar) {
        String yyyymm = new SimpleDateFormat("yyyyMM", Locale.getDefault()).format(calendar.getTime());

        Call<List<DailyDto.Daily>> dailyListData;

        if (mode == CalendarUtil.MODE_PRIVATE) {
            dailyListData = ApiClient.getService(getApplication(), DailyService.class).getDailyListData(yyyymm);
        } else {
            if (memberId <= 0) return;
            dailyListData = ApiClient.getService(getApplication(), DailyService.class).getDailyListData(memberId, yyyymm);
        }

        dailyListData.enqueue(new BaseCallback<List<DailyDto.Daily>>(getApplication()) {
            @Override
            public void onResponse(Response<List<DailyDto.Daily>> response) {
                if (!response.isSuccessful()) return;

                List<DailyDto.Daily> dailyData = response.body() != null ? response.body() : new ArrayList<>();
                List<CalendarCell> cellList = CalendarUtil.getDefaultMonthList(calendar);
                Map<String, DailyDto.Daily> map = new HashMap<>();

                for (DailyDto.Daily daily : dailyData) {
                    map.put(daily.getDate(), daily);
                }

                String today = CalendarUtil.calendarToString(Calendar.getInstance(), "yyyyMMdd");

                for (CalendarCell cell : cellList) {
                    if (!map.containsKey(cell.getDate())) continue;

                    DailyDto.Daily info = map.get(cell.getDate());

                    cell.setBodyWeight(info.getBodyWeight());

                    if (mode ==  CalendarUtil.MODE_PRIVATE && today.equals(cell.getDate())) {
                        SharedPreferences sp = getApplication().getSharedPreferences(SharedDataUtil.STEP_INFO, Context.MODE_PRIVATE);
                        int todayCount = sp.getInt(today, 0);

                        if (todayCount == 0) {
                            int count = info.getWalkCount();
                            sp.edit().putInt(today, count).apply();
                            todayCount = count;
                        }

                        cell.setWalkCount(todayCount);
                    } else {
                        cell.setWalkCount(info.getWalkCount());
                    }
                }

                currentDate.setValue(calendar);
                dailyList.setValue(cellList);
            }

            @Override
            public void onFailure(Throwable t) {
                currentDate.setValue(calendar);
                dailyList.setValue(CalendarUtil.getDefaultMonthList(calendar));
            }
        });
    }

    public long getMemberId() {
        return memberId;
    }

    public int getMode() {
        return mode;
    }
}
