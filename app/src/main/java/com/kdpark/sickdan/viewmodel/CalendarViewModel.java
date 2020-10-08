package com.kdpark.sickdan.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarViewModel extends AndroidViewModel {

    //== Data ==//
    public final MutableLiveData<Calendar> currentDate = new MutableLiveData<>();
    public final MutableLiveData<List<CalendarCell>> dailyList = new MutableLiveData<>();

    public long memberId;
    public int mode;

    //== Event ==//
//    private MutableLiveData<Event<String>> tempEvent = new MutableLiveData<>();

    public CalendarViewModel(@NonNull Application application) {
        super(application);
    }

    public void setMonth(Calendar calendar) {
        currentDate.setValue((Calendar) calendar.clone());
    }

    public void setCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        setCalendarData(calendar);
    }

    public void setCalendarData(Calendar calendar) {
        String yyyymm = new SimpleDateFormat("yyyyMM", Locale.getDefault()).format(calendar.getTime());

        Call<List<DailyDto>> dailyListData;

        if (mode == CalendarUtil.MODE_PRIVATE) {
            dailyListData = ApiClient.getService(DailyService.class).getDailyListData(yyyymm);
        } else {
            if (memberId <= 0) return;
            dailyListData = ApiClient.getService(DailyService.class).getDailyListData(memberId, yyyymm);
        }

        dailyListData.enqueue(new BaseCallback<List<DailyDto>>(getApplication()) {
            @Override
            public void onResponse(Response<List<DailyDto>> response) {
                if (!response.isSuccessful()) return;

                List<DailyDto> dailyData = response.body();
                List<CalendarCell> cellList = CalendarUtil.getDefaultOfMonth(calendar);
                Map<String, DailyDto> map = new HashMap<>();

                for (DailyDto daily : dailyData) {
                    map.put(daily.getDate(), daily);
                }

                String today = CalendarUtil.calendarToString(Calendar.getInstance(), "yyyyMMdd");

                for (CalendarCell cell : cellList) {
                    if (!map.containsKey(cell.getDate())) continue;

                    DailyDto info = map.get(cell.getDate());

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
                dailyList.setValue(CalendarUtil.getDefaultOfMonth(calendar));
            }
        });
    }

    public void reload() {
        setCalendarData(currentDate.getValue());
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public long getMemberId() {
        return memberId;
    }

    public int getMode() {
        return mode;
    }
}
