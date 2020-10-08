package com.kdpark.sickdan.viewmodel;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kdpark.sickdan.model.ApiClient;
import com.kdpark.sickdan.model.BaseCallback;
import com.kdpark.sickdan.model.dto.CommentDto;
import com.kdpark.sickdan.model.dto.CommentWriteRequest;
import com.kdpark.sickdan.model.service.DailyService;

import java.util.List;

import retrofit2.Response;

public class CommentViewModel extends AndroidViewModel {

    //== Data ==//
    public final MutableLiveData<String> date = new MutableLiveData<>();
    public final MutableLiveData<List<CommentDto>> comments = new MutableLiveData<>();
    public final MutableLiveData<Pair<Long, String>> replyInfo = new MutableLiveData<>();
    public long memberId;

    //== Event ==//
//    private MutableLiveData<Event<String>> tempEvent = new MutableLiveData<>();{

    public CommentViewModel(@NonNull Application application) {
        super(application);
    }

    public void setDate(String date) {
        this.date.setValue(date);
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public void setReplyInfo(Long id, String displayName) {
        replyInfo.setValue(new Pair<>(id, displayName));
    }

    public void clearReplyInfo() {
        replyInfo.setValue(null);
    }

    public void getComments() {
        String date = this.date.getValue();
        ApiClient.getService(DailyService.class).getComments(memberId, date).enqueue(new BaseCallback<List<CommentDto>>(getApplication()) {
            @Override
            public void onResponse(Response<List<CommentDto>> response) {
                if (!response.isSuccessful()) return;
                comments.setValue(response.body());
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    public void writeComment(String description) {
        String date = this.date.getValue();

        Long parentId = null;
        if (replyInfo.getValue() != null) parentId = replyInfo.getValue().first;

        CommentWriteRequest request = new CommentWriteRequest(description, parentId);

        ApiClient.getService(DailyService.class).writeComment(memberId, date, request).enqueue(new BaseCallback<Void>(getApplication()) {
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
