package com.kdpark.sickdan.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kdpark.sickdan.model.ApiClient;
import com.kdpark.sickdan.model.dto.DailyDto;
import com.kdpark.sickdan.model.dto.MealAddRequest;
import com.kdpark.sickdan.model.dto.MealCategory;
import com.kdpark.sickdan.model.dto.MealDto;
import com.kdpark.sickdan.model.service.DailyService;
import com.kdpark.sickdan.view.control.meallist.MealCellType;
import com.kdpark.sickdan.view.control.meallist.MealItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Getter
public class DailyDetailViewModel extends AndroidViewModel {

    //== Data ==//
    private MutableLiveData<Calendar> currentDate = new MutableLiveData<>();
    private MutableLiveData<Float> bodyWeight = new MutableLiveData<>();
    private MutableLiveData<Integer> walkCount = new MutableLiveData<>();
    private MutableLiveData<List<MealItem>> mealList = new MutableLiveData<>();

    //== Event ==//
    private MutableLiveData<Event<String>> toastEvent = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> closeActivity = new MutableLiveData<>();

    public DailyDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void setDate(Calendar calendar) {
        String yyyymmdd = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(calendar.getTime());

        currentDate.setValue(calendar);

        ApiClient.getService(DailyService.class).getDayDetailData(yyyymmdd).enqueue(new Callback<DailyDto>() {
            @Override
            public void onResponse(Call<DailyDto> call, Response<DailyDto> response) {
                if (!response.isSuccessful()) return;

                DailyDto daily = response.body();

                List<MealItem> meals = new ArrayList<>();

                for (MealDto meal : daily.getMeals()) {
                    meals.add(new MealItem(meal.getDescription(), meal.getCategory(), MealCellType.ITEM));
                }

                mealList.setValue(meals);
                bodyWeight.setValue(daily.getBodyWeight());
                walkCount.setValue(daily.getWalkCount());
                currentDate.setValue(calendar);
            }

            @Override
            public void onFailure(Call<DailyDto> call, Throwable t) {
                currentDate.setValue(calendar);
                Log.e("PKD", t.toString());
            }
        });
    }

    public void addMeal(String yyyymmdd, String description, MealCategory category) {

        MealAddRequest request = MealAddRequest.builder()
                .date(yyyymmdd)
                .description(description)
                .category(category)
                .build();

        ApiClient.getService(DailyService.class).addMeal(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                closeActivity.setValue(new Event(true));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("PKD", t.toString());
            }
        });
    }
}
