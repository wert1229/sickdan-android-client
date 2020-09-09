package com.kdpark.sickdan.model.service;

import com.kdpark.sickdan.model.dto.FriendSearchDto;
import com.kdpark.sickdan.model.dto.MemberDto;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MemberService {
    @GET("api/v1/members/me")
    Call<MemberDto> getAuthMember();

    @POST("api/v1/members/relationships")
    Call<Void> requestFriend(@Body Long relatedId);

    @GET("api/v1/members")
    Call<FriendSearchDto> searchMemberByEmail(@Query("email") String email);

    @GET("api/v1/members/exist")
    Call<Map<String, Boolean>> checkDuplicate(@Query("userId") String userId);
}
