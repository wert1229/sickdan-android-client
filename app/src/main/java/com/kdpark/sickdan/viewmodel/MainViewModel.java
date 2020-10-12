package com.kdpark.sickdan.viewmodel;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kdpark.sickdan.model.ApiClient;
import com.kdpark.sickdan.model.BaseCallback;
import com.kdpark.sickdan.model.dto.MemberDto;
import com.kdpark.sickdan.model.service.MemberService;
import com.kdpark.sickdan.util.SharedDataUtil;
import com.kdpark.sickdan.viewmodel.common.BundleViewModel;

import retrofit2.Response;

public class MainViewModel extends BundleViewModel {

    public final MutableLiveData<MemberDto.Member> member = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application, Bundle bundle) {
        super(application, bundle);
    }

    public void getMyInfo() {
        ApiClient.getService(getApplication(), MemberService.class).getAuthMember().enqueue(new BaseCallback<MemberDto.Member>(getApplication()) {
            @Override
            public void onResponse(Response<MemberDto.Member> response) {
                MemberDto.Member memberDto = response.body();
                SharedDataUtil.setData(SharedDataUtil.AUTH_MEMBER_ID, memberDto.getId());
                member.setValue(memberDto);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}
