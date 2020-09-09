package com.kdpark.sickdan.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kdpark.sickdan.model.ApiClient;
import com.kdpark.sickdan.model.dto.FriendSearchDto;
import com.kdpark.sickdan.model.dto.MemberDto;
import com.kdpark.sickdan.model.dto.MemberRelationshipDto;
import com.kdpark.sickdan.model.dto.RelationshipStatus;
import com.kdpark.sickdan.model.service.MemberService;
import com.kdpark.sickdan.viewmodel.common.Event;

import java.util.List;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Getter
public class FriendViewModel extends AndroidViewModel {

    //== Data ==//
    private MutableLiveData<FriendSearchDto> searchResult = new MutableLiveData<>();
    private MutableLiveData<List<MemberRelationshipDto>> friendList = new MutableLiveData<>();

    //== Event ==//
    private MutableLiveData<Event<Boolean>> friendAddComplete = new MutableLiveData<>();

    public FriendViewModel(@NonNull Application application) {
        super(application);
    }

    public void requestFriendList() {
        ApiClient.getService(MemberService.class).getAuthMember().enqueue(new Callback<MemberDto>() {
            @Override
            public void onResponse(Call<MemberDto> call, Response<MemberDto> response) {
                MemberDto member = response.body();
                friendList.setValue(member.getRelationships());
            }

            @Override
            public void onFailure(Call<MemberDto> call, Throwable t) {

            }
        });
    }

    public void searchByEmail(String email) {
        ApiClient.getService(MemberService.class).searchMemberByEmail(email).enqueue(new Callback<FriendSearchDto>() {
            @Override
            public void onResponse(Call<FriendSearchDto> call, Response<FriendSearchDto> response) {
                FriendSearchDto result = response.body();
                if (result == null) return;
                searchResult.setValue(result);
            }

            @Override
            public void onFailure(Call<FriendSearchDto> call, Throwable t) {

            }
        });
    }

    public void sendFriendRequest() {
        FriendSearchDto result = searchResult.getValue();

        if (result == null) return;
        if (result.getStatus() != RelationshipStatus.NONE) return;

        Long relatedId = result.getId();

        ApiClient.getService(MemberService.class).requestFriend(relatedId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                friendAddComplete.setValue(new Event(true));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

}
