package com.kdpark.sickdan.viewmodel.common;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

public class BundleViewModel extends ViewModel {
    @SuppressLint("StaticFieldLeak")
    private Application mApplication;
    private Bundle mBundle;

    public BundleViewModel(@NonNull Application application, Bundle bundle) {
        mApplication = application;
        mBundle = bundle;
    }

    @SuppressWarnings({"TypeParameterUnusedInFormals", "unchecked"})
    @NonNull
    public <T extends Application> T getApplication() {
        return (T) mApplication;
    }

    public Bundle getBundle() {
        return mBundle;
    }
}
