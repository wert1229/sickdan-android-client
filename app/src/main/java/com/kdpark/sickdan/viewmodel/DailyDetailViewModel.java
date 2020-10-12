package com.kdpark.sickdan.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.kdpark.sickdan.model.ApiClient;
import com.kdpark.sickdan.model.BaseCallback;
import com.kdpark.sickdan.model.dto.DailyDto;
import com.kdpark.sickdan.model.dto.enums.MealCategory;
import com.kdpark.sickdan.model.dto.MealDto;
import com.kdpark.sickdan.model.service.DailyService;
import com.kdpark.sickdan.util.CalendarUtil;
import com.kdpark.sickdan.util.SharedDataUtil;
import com.kdpark.sickdan.view.control.meallist.MealCellType;
import com.kdpark.sickdan.view.control.meallist.MealItem;
import com.kdpark.sickdan.viewmodel.common.BundleViewModel;
import com.kdpark.sickdan.viewmodel.common.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DailyDetailViewModel extends BundleViewModel {

    //== Data ==//
    public final MutableLiveData<Calendar> currentDate = new MutableLiveData<>();
    public final MutableLiveData<Float> bodyWeight = new MutableLiveData<>();
    public final MutableLiveData<Integer> walkCount = new MutableLiveData<>();
    public final MutableLiveData<List<MealItem>> mealList = new MutableLiveData<>();
    public final MutableLiveData<Integer> commentCount = new MutableLiveData<>();
    public final MutableLiveData<Integer> likeCount = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isLiked = new MutableLiveData<>();

    private int mode;
    private long memberId;
    private String date;

    //== Event ==//
    public final MutableLiveData<Event<String>> toastEvent = new MutableLiveData<>();
    public final MutableLiveData<Event<Boolean>> closeActivity = new MutableLiveData<>();

    public DailyDetailViewModel(@NonNull Application application, Bundle bundle) {
        super(application, bundle);

        if (bundle.containsKey(CalendarUtil.VIEW_MODE_KEY))
            this.mode = bundle.getInt(CalendarUtil.VIEW_MODE_KEY);
        if (bundle.containsKey("memberId"))
            this.memberId = bundle.getLong("memberId");
        if (bundle.containsKey("date"))
            this.date = bundle.getString("date");
    }

    public void loadData() {
        loadDateData(CalendarUtil.stringToCalendar(date));
    }

    public void loadDateData(Calendar calendar) {
        String yyyymmdd = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(calendar.getTime());

        currentDate.setValue(calendar);

        Call<DailyDto.Daily> dayDetailData;

        if (mode == CalendarUtil.MODE_PRIVATE) {
            dayDetailData = ApiClient.getService(getApplication(), DailyService.class).getDayDetailData(yyyymmdd);
        } else {
            if (memberId <= 0) return;
            dayDetailData = ApiClient.getService(getApplication(), DailyService.class).getDayDetailData(memberId, yyyymmdd);
        }

        dayDetailData.enqueue(new BaseCallback<DailyDto.Daily>(getApplication()) {
            @Override
            public void onResponse(Response<DailyDto.Daily> response) {
                if (!response.isSuccessful()) return;

                DailyDto.Daily daily = response.body() != null ? response.body() : new DailyDto.Daily();

                List<MealItem> meals = new ArrayList<>();

                for (MealDto.Meal meal : daily.getMeals()) {
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

                mealList.setValue(meals);
                bodyWeight.setValue(daily.getBodyWeight());
                commentCount.setValue(daily.getCommentCount());
                likeCount.setValue(daily.getLikeCount());

                if (mode ==  CalendarUtil.MODE_PRIVATE && CalendarUtil.isSameDate(currentDate.getValue(), Calendar.getInstance())) {
                    SharedPreferences sp = getApplication().getSharedPreferences(SharedDataUtil.STEP_INFO, Context.MODE_PRIVATE);
                    int todayCount = sp.getInt(date, 0);

                    walkCount.setValue(todayCount);
                } else {
                    walkCount.setValue(daily.getWalkCount());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("PKD", t.toString());
            }
        });
    }

    public void addMeal(String description, MealCategory category) {
        String yyyymmdd = new SimpleDateFormat("yyyyMMdd",
                Locale.getDefault()).format(currentDate.getValue().getTime());

        MealDto.MealAddRequest request = MealDto.MealAddRequest.builder()
                .date(yyyymmdd)
                .description(description)
                .category(category)
                .build();

        ApiClient.getService(getApplication(), DailyService.class).addMeal(request).enqueue(new BaseCallback<Void>(getApplication()) {
            @Override
            public void onResponse(Response<Void> response) {
                if (!response.isSuccessful()) {
                    toastEvent.setValue(new Event<>("meal add failed!"));
                } else {
                    toastEvent.setValue(new Event<>("meal add confirm!"));
                }

                reload();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("PKD", t.toString());
                reload();
            }
        });
    }

    public void editMealDesc(Long id, String description) {
        MealDto.MealEditRequest request = MealDto.MealEditRequest.builder()
                .description(description)
                .build();

        ApiClient.getService(getApplication(), DailyService.class).editMeal(id, request).enqueue(new BaseCallback<Void>(getApplication()) {
            @Override
            public void onResponse(Response<Void> response) {
                if (!response.isSuccessful()) {
                    toastEvent.setValue(new Event<>("meal edit failed!"));
                } else {
                    toastEvent.setValue(new Event<>("meal edit confirm!"));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("PKD", t.toString());
            }
        });
    }

    public void deleteMeal(Long id) {
        ApiClient.getService(getApplication(), DailyService.class).deleteMeal(id).enqueue(new BaseCallback<Void>(getApplication()) {
            @Override
            public void onResponse(Response<Void> response) {
                if (!response.isSuccessful()) {
                    toastEvent.setValue(new Event<>("meal delete failed!"));
                } else {
                    toastEvent.setValue(new Event<>("meal delete confirm!"));
                }
                reload();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("PKD", t.toString());
                reload();
            }
        });
    }

    public void addPhoto(Long mealId, MultipartBody.Part part) {
        ApiClient.getService(getApplication(), DailyService.class).addMealPhoto(mealId, part).enqueue(new BaseCallback<Void>(getApplication()) {
            @Override
            public void onResponse(Response<Void> response) {
                Log.e("PKD", response.toString());
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("PKD", t.toString());
            }
        });
    }

    public void editWeight(double weight) {
        DailyDto.DayInfoUpdateRequest request = DailyDto.DayInfoUpdateRequest.builder()
                .bodyWeight(weight)
                .build();

        ApiClient.getService(getApplication(), DailyService.class).editDaily(date, request).enqueue(new BaseCallback<Void>(getApplication()) {
            @Override
            public void onResponse(Response<Void> response) {
                if (!response.isSuccessful()) {
                    Log.e("PKD", response.toString());
                    return;
                }
                toastEvent.setValue(new Event<>("weight edit confirm!"));
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("PKD", t.toString());
            }
        });

    }

    public void isLiked() {
        if (memberId == 0) memberId = Long.parseLong(SharedDataUtil.getData(SharedDataUtil.AUTH_MEMBER_ID, false));
        ApiClient.getService(getApplication(), DailyService.class).isLiked(memberId, date).enqueue(new BaseCallback<Map<String, Boolean>>(getApplication()) {
            @Override
            public void onResponse(Response<Map<String, Boolean>> response) {
                if (!response.isSuccessful()) return;

                boolean liked = response.body() != null ? response.body().get("isLiked") : false;
                isLiked.setValue(liked);
            }

            @Override
            public void onFailure(Throwable t) {}
        });
    }

    public void toggleLike() {
        Boolean liked = isLiked.getValue();
        if (liked == null) return;

        if (liked)
            undoLike();
        else
            doLike();
    }

    public void doLike() {
        if (memberId == 0) memberId = Long.parseLong(SharedDataUtil.getData(SharedDataUtil.AUTH_MEMBER_ID, false));
        ApiClient.getService(getApplication(), DailyService.class).doLike(memberId, date).enqueue(new BaseCallback<Void>(getApplication()) {
            @Override
            public void onResponse(Response<Void> response) {
                if (!response.isSuccessful()) return;
                isLiked.setValue(true);
                likeCount.setValue(likeCount.getValue() + 1);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void undoLike() {
        if (memberId == 0) memberId = Long.parseLong(SharedDataUtil.getData(SharedDataUtil.AUTH_MEMBER_ID, false));
        ApiClient.getService(getApplication(), DailyService.class).undoLike(memberId, date).enqueue(new BaseCallback<Void>(getApplication()) {
            @Override
            public void onResponse(Response<Void> response) {
                if (!response.isSuccessful()) return;
                isLiked.setValue(false);
                likeCount.setValue(likeCount.getValue() - 1);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void reload() {
        loadDateData(currentDate.getValue());
    }

    public int getMode() {
        return mode;
    }

    public long getMemberId() {
        return memberId;
    }

    public String getDate() {
        return date;
    }
}
