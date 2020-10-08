package com.kdpark.sickdan.model.service;

import com.kdpark.sickdan.model.dto.CommentDto;
import com.kdpark.sickdan.model.dto.CommentWriteRequest;
import com.kdpark.sickdan.model.dto.DailyDto;
import com.kdpark.sickdan.model.dto.MealAddRequest;
import com.kdpark.sickdan.model.dto.MemberDto;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DailyService {
    @GET("api/v1/members/me/dailies")
    Call<List<DailyDto>> getDailyListData(@Query("yyyymm") String yyyymm);

    @GET("api/v1/members/me/dailies/{yyyymmdd}")
    Call<DailyDto> getDayDetailData(@Path("yyyymmdd") String yyyymmdd);

    @GET("api/v1/members/{memberId}/dailies")
    Call<List<DailyDto>> getDailyListData(@Path("memberId")Long memberId, @Query("yyyymm") String yyyymm);

    @GET("api/v1/members/{memberId}/dailies/{yyyymmdd}")
    Call<DailyDto> getDayDetailData(@Path("memberId")Long memberId, @Path("yyyymmdd") String yyyymmdd);

    @POST("api/v1/meals")
    Call<Void> addMeal(@Body MealAddRequest request);

    @PUT("api/v1/meals/{mealId}")
    Call<Void> editMeal(@Path("mealId") Long id, @Body Map<String, String> params);

    @DELETE("api/v1/meals/{mealId}")
    Call<Void> deleteMeal(@Path("mealId") Long id);

    @Multipart
    @POST("api/v1/meals/{mealId}/photos")
    Call<Void> addMealPhoto(@Path(("mealId")) Long mealId, @Part MultipartBody.Part file);

    @PUT("api/v1/members/me/dailies/{yyyymmdd}")
    Call<Void> editDaily(@Path("yyyymmdd") String yyyymmdd, @Body Map<String, Object> params);

    @PUT("api/v1/members/me/dailies")
    Call<Map<String, List<String>>> syncWalkCount(@Body Map<String, Integer> params);

    @POST("/api/v1/members/{memberId}/dailies/{yyyymmdd}/comments")
    Call<Void> writeComment(@Path("memberId")Long memberId, @Path("yyyymmdd") String yyyymmdd, @Body CommentWriteRequest request);

    @GET("/api/v1/members/{memberId}/dailies/{yyyymmdd}/comments")
    Call<List<CommentDto>> getComments(@Path("memberId")Long memberId, @Path("yyyymmdd") String yyyymmdd);

    @GET("/api/v1/members/{memberId}/dailies/{yyyymmdd}/likes/me")
    Call<Map<String, Boolean>> isLiked(@Path("memberId")Long memberId, @Path("yyyymmdd") String yyyymmdd);

    @POST("/api/v1/members/{memberId}/dailies/{yyyymmdd}/likes")
    Call<Void> doLike(@Path("memberId")Long memberId, @Path("yyyymmdd") String yyyymmdd);

    @DELETE("/api/v1/members/{memberId}/dailies/{yyyymmdd}/likes")
    Call<Void> undoLike(@Path("memberId")Long memberId, @Path("yyyymmdd") String yyyymmdd);
}
