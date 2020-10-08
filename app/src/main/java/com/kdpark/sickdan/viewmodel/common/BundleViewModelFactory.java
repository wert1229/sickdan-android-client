package com.kdpark.sickdan.viewmodel.common;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.InvocationTargetException;

public class BundleViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private static BundleViewModelFactory sInstance;

    @NonNull
    public static BundleViewModelFactory getInstance(@NonNull Application application, Bundle bundle) {
        if (sInstance == null) {
            sInstance = new BundleViewModelFactory(application, bundle);
        }
        return sInstance;
    }

    private Application mApplication;
    private Bundle mBundle;

    public BundleViewModelFactory(@NonNull Application application, Bundle bundle) {
        mApplication = application;
        mBundle = bundle;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (BundleViewModel.class.isAssignableFrom(modelClass)) {
            //noinspection TryWithIdenticalCatches
            try {
                return modelClass.getConstructor(Application.class, Bundle.class).newInstance(mApplication, mBundle);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            } catch (InstantiationException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        }
        return super.create(modelClass);
    }
}
