package com.kdpark.sickdan.viewmodel;

import android.app.Application;
import android.os.Bundle;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kdpark.sickdan.model.ApiClient;
import com.kdpark.sickdan.model.BaseCallback;
import com.kdpark.sickdan.model.dto.CommentDto;
import com.kdpark.sickdan.model.service.DailyService;
import com.kdpark.sickdan.util.CalendarUtil;
import com.kdpark.sickdan.viewmodel.common.BundleViewModel;

import java.util.List;

import retrofit2.Response;

public class CommentViewModel extends BundleViewModel {

    //== Data ==//
    public final MutableLiveData<List<CommentDto.Comment>> comments = new MutableLiveData<>();
    public final MutableLiveData<Pair<Long, String>> replyInfo = new MutableLiveData<>();

    private long memberId;
    private String date;

    public CommentViewModel(@NonNull Application application, Bundle bundle) {
        super(application, bundle);

        if (bundle.containsKey("date"))
            this.date = bundle.getString("date");
        if (bundle.containsKey("memberId"))
            this.memberId = bundle.getLong("memberId");
    }

    public void setReplyInfo(Long id, String displayName) {
        replyInfo.setValue(new Pair<>(id, displayName));
    }

    public void clearReplyInfo() {
        replyInfo.setValue(null);
    }

    public void getComments() {
        ApiClient.getService(getApplication(), DailyService.class)
                .getComments(memberId, date)
                .enqueue(new BaseCallback<List<CommentDto.Comment>>(getApplication()) {
            @Override
            public void onResponse(Response<List<CommentDto.Comment>> response) {
                if (!response.isSuccessful()) return;
                List<CommentDto.Comment> result = response.body();
                comments.setValue(result);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void writeComment(String description) {
        Long parentId = null;
        if (replyInfo.getValue() != null) parentId = replyInfo.getValue().first;

        CommentDto.CommentWriteRequest request = new CommentDto.CommentWriteRequest(description, parentId);

        ApiClient.getService(getApplication(), DailyService.class)
                .writeComment(memberId, date, request).
                enqueue(new BaseCallback<Void>(getApplication()) {
            @Override
            public void onResponse(Response<Void> response) {
                reload();
            }

            @Override
            public void onFailure(Throwable t) {
                reload();
            }
        });
    }

    public void reload() {
        getComments();
    }
}
