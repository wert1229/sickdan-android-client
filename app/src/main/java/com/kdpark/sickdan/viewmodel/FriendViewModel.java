package com.kdpark.sickdan.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kdpark.sickdan.model.ApiClient;
import com.kdpark.sickdan.model.BaseCallback;
import com.kdpark.sickdan.model.dto.FriendSearchDto;
import com.kdpark.sickdan.model.dto.MemberDto;
import com.kdpark.sickdan.model.dto.MemberRelationshipDto;
import com.kdpark.sickdan.model.dto.RelationshipStatus;
import com.kdpark.sickdan.model.service.MemberService;
import com.kdpark.sickdan.view.control.friend.FriendItem;
import com.kdpark.sickdan.view.control.friend.FriendSection;
import com.kdpark.sickdan.viewmodel.common.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Getter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendViewModel extends AndroidViewModel {

    //== Data ==//
    public final MutableLiveData<FriendSearchDto> searchResult = new MutableLiveData<>();
    public final MutableLiveData<List<FriendSection>> friendList = new MutableLiveData<>();
    public final MutableLiveData<String> myCode = new MutableLiveData<>("");

    //== Event ==//
    public final MutableLiveData<Event<Boolean>> friendAddComplete = new MutableLiveData<>();
    public final MutableLiveData<Event<Boolean>> friendAcceptComplete = new MutableLiveData<>();
    public final MutableLiveData<Event<String>> showToast = new MutableLiveData<>();

    public FriendViewModel(@NonNull Application application) {
        super(application);
    }

    public void getMyCode() {
        ApiClient.getService(MemberService.class).getMyCode().enqueue(new BaseCallback<Map<String, String>>(getApplication()) {
            @Override
            public void onResponse(Response<Map<String, String>> response) {
                String code = response.body() != null ? response.body().getOrDefault("code", "") : "";
                myCode.setValue(code);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void requestFriendList() {
        ApiClient.getService(MemberService.class).getAuthMember().enqueue(new BaseCallback<MemberDto>(getApplication()) {
            @Override
            public void onResponse(Response<MemberDto> response) {
                List<MemberRelationshipDto> relationships = response.body().getRelationships();

                Map<RelationshipStatus, List<FriendItem>> map = new HashMap<>();
                map.put(RelationshipStatus.FRIEND, new ArrayList<>());
                map.put(RelationshipStatus.REQUESTED, new ArrayList<>());
                map.put(RelationshipStatus.REQUESTING, new ArrayList<>());

                for (MemberRelationshipDto r : relationships) {
                    if (!map.containsKey(r.getStatus())) map.put(r.getStatus(), new ArrayList<>());

                    FriendItem item = FriendItem.builder()
                            .displayName(r.getDisplayName())
                            .email(r.getEmail())
                            .id(r.getId())
                            .status(r.getStatus())
                            .build();

                    map.get(r.getStatus()).add(item);
                }

                List<FriendSection> list = new ArrayList<>();

                List<RelationshipStatus> keys = new ArrayList<>(map.keySet());
                Collections.sort(keys, RelationshipStatus.SHOW_ORDER);;

                for (RelationshipStatus status : keys) {
                    List<FriendItem> items = map.get(status);
                    String title = String.format(Locale.getDefault(), "%s (%d)", status.getDesc(), items.size());
                    list.add(new FriendSection(title, items));
                }

                friendList.setValue(list);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void searchByCode(String code) {
        ApiClient.getService(MemberService.class).searchMemberByFilter("code", code).enqueue(new BaseCallback<FriendSearchDto>(getApplication()) {
            @Override
            public void onResponse(Response<FriendSearchDto> response) {
                FriendSearchDto result = response.body();
                if (result == null) {
                    showToast.setValue(new Event<>("검색 결과가 없습니다."));
                    return;
                }
                searchResult.setValue(result);
            }

            @Override
            public void onFailure(Throwable t) {

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

    public void acceptFriendRequest(Long friendId) {
        if (friendId == null) return;

        ApiClient.getService(MemberService.class).acceptFriend(friendId).enqueue(new BaseCallback<Void>(getApplication()) {
            @Override
            public void onResponse(Response<Void> response) {
                friendAcceptComplete.setValue(new Event<>(true));
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

}
