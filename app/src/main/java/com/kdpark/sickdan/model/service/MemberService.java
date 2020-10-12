package com.kdpark.sickdan.model.service;

import com.kdpark.sickdan.model.dto.MemberDto;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface MemberService {
    @GET("api/v1/members/me")
    Call<MemberDto.Member> getAuthMember();

    @GET("api/v1/members/me/code")
    Call<Map<String, String>> getMyCode();

    @POST("api/v1/members/me/relationships")
    Call<Void> requestFriend(@Body Long relatedId);

    @PUT("api/v1/members/me/relationships")
    Call<Void> acceptFriend(@Body Long relatedId);

    @GET("api/v1/members")
    Call<MemberDto.FriendSearchResult> searchMemberByFilter(@Query("by") String by, @Query("value") String value);

    @GET("api/v1/members/exist")
    Call<Map<String, Boolean>> checkDuplicate(@Query("userId") String userId);
}
