package com.kdpark.sickdan.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kdpark.sickdan.model.ApiClient;
import com.kdpark.sickdan.model.dto.DailyDto;
import com.kdpark.sickdan.model.service.DailyService;
import com.kdpark.sickdan.util.CalendarUtil;
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

@Getter
public class FriendMainViewModel extends AndroidViewModel {

    //== Data ==//
    private MutableLiveData<Long> memberId = new MutableLiveData<>();

    //== Event ==//

//    private MutableLiveData<Event<String>> tempEvent = new MutableLiveData<>();

    public FriendMainViewModel(@NonNull Application application) {
        super(application);
    }

    public void setMemberId(long memberId) {
        this.memberId.setValue(memberId);
    }
}
