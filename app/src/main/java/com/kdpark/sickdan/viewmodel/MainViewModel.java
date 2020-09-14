package com.kdpark.sickdan.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kdpark.sickdan.model.ApiClient;
import com.kdpark.sickdan.model.BaseCallback;
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
//    public final MutableLiveData<Event<List<String>>> syncComplete = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public void getMyInfo() {
        ApiClient.getService(MemberService.class).getAuthMember().enqueue(new BaseCallback<MemberDto>(getApplication()) {
            @Override
            public void onResponse(Response<MemberDto> response) {
                member.setValue(response.body());
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}
