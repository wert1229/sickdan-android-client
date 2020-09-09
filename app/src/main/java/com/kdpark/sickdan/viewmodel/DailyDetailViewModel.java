package com.kdpark.sickdan.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
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
import com.kdpark.sickdan.util.CalendarUtil;
import com.kdpark.sickdan.util.SharedDataUtil;
import com.kdpark.sickdan.view.control.meallist.MealCellType;
import com.kdpark.sickdan.view.control.meallist.MealItem;
import com.kdpark.sickdan.viewmodel.common.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lombok.Getter;
import okhttp3.MultipartBody;
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

    private int mode;
    private long memberId;

    //== Event ==//
    private MutableLiveData<Event<String>> toastEvent = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> closeActivity = new MutableLiveData<>();

    public DailyDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void setDate(Calendar calendar) {
        String yyyymmdd = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(calendar.getTime());

        currentDate.setValue(calendar);

        Call<DailyDto> dayDetailData;

        if (mode == CalendarUtil.MODE_PRIVATE) {
            dayDetailData = ApiClient.getService(DailyService.class).getDayDetailData(yyyymmdd);
        } else {
            if (memberId <= 0) return;
            dayDetailData = ApiClient.getService(DailyService.class).getDayDetailData(memberId, yyyymmdd);
        }

        dayDetailData.enqueue(new Callback<DailyDto>() {
            @Override
            public void onResponse(Call<DailyDto> call, Response<DailyDto> response) {
                if (!response.isSuccessful()) return;

                DailyDto daily = response.body();

                List<MealItem> meals = new ArrayList<>();

                for (MealDto meal : daily.getMeals()) {
                    meals.add(
                            MealItem.builder()
                                    .id(meal.getId())
                                    .description(meal.getDescription())
                                    .category(meal.getCategory())
                                    .type(MealCellType.ITEM)
                                    .photos(meal.getPhotos())
                                    .build()
                    );
                }

                currentDate.setValue(calendar);
                mealList.setValue(meals);
                bodyWeight.setValue(daily.getBodyWeight());

                if (CalendarUtil.isSameDate(currentDate.getValue(), Calendar.getInstance())) {
                    String todayWalkCount = SharedDataUtil.getData(SharedDataUtil.TODAY_COUNT, false);
                    walkCount.setValue(Integer.parseInt(todayWalkCount));
                } else {
                    walkCount.setValue(daily.getWalkCount());
                }
            }

            @Override
            public void onFailure(Call<DailyDto> call, Throwable t) {
                currentDate.setValue(calendar);
                Log.e("PKD", t.toString());
            }
        });
    }

    public void addMeal(String description, MealCategory category) {
        String yyyymmdd = new SimpleDateFormat("yyyyMMdd",
                Locale.getDefault()).format(currentDate.getValue().getTime());

        MealAddRequest request = MealAddRequest.builder()
                .date(yyyymmdd)
                .description(description)
                .category(category)
                .build();

        ApiClient.getService(DailyService.class).addMeal(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                toastEvent.setValue(new Event<>("meal add confirm!"));
                reload();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("PKD", t.toString());
                reload();
            }
        });
    }

    public void editMealDesc(Long id, String description) {
        Map<String, String> params = new HashMap<>();
        params.put("description", description);

        ApiClient.getService(DailyService.class).editMeal(id, params).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                toastEvent.setValue(new Event<>("meal edit confirm!"));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("PKD", t.toString());
            }
        });
    }

    public void deleteMeal(Long id) {
        ApiClient.getService(DailyService.class).deleteMeal(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                toastEvent.setValue(new Event<>("meal delete confirm!"));
                reload();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("PKD", t.toString());
                reload();
            }
        });
    }

    public void addPhoto(Long mealId, MultipartBody.Part part) {
        ApiClient.getService(DailyService.class).addMealPhoto(mealId, part).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.e("PKD", response.toString());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("PKD", t.toString());
            }
        });
    }

    public void editWeight(double weight) {
        String yyyymmdd = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(currentDate.getValue().getTime());
        Map<String, Object> params = new HashMap<>();
        params.put("bodyWeight", weight);

        ApiClient.getService(DailyService.class).editDaily(yyyymmdd, params).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Log.e("PKD", response.toString());
                    return;
                }
                toastEvent.setValue(new Event<>("weight edit confirm!"));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("PKD", t.toString());
            }
        });

    }

    public void reload() {
        setDate(currentDate.getValue());
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }
}
