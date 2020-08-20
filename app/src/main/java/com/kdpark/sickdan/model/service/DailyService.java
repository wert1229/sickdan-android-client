package com.kdpark.sickdan.model.service;

import com.kdpark.sickdan.model.dto.DailyDto;
import com.kdpark.sickdan.model.dto.MealAddRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DailyService {
    @GET("api/v1/dailies/month/{yyyymm}")
    Call<List<DailyDto>> getDailyListData(@Path("yyyymm") String yyyymm);

    @GET("api/v1/dailies/day/{yyyymmdd}")
    Call<DailyDto> getDayDetailData(@Path("yyyymmdd") String yyyymmdd);

    @POST("api/v1/meals")
    Call<Void> addMeal(@Body MealAddRequest request);
}
