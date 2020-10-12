package com.kdpark.sickdan.viewmodel;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kdpark.sickdan.viewmodel.common.BundleViewModel;

import lombok.Getter;

@Getter
public class FriendMainViewModel extends BundleViewModel {

    //== Data ==//
    private MutableLiveData<Long> memberId = new MutableLiveData<>();

    public FriendMainViewModel(@NonNull Application application, Bundle bundle) {
        super(application, bundle);
    }

    public void setMemberId(long memberId) {
        this.memberId.setValue(memberId);
    }
}
