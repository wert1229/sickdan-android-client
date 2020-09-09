package com.kdpark.sickdan.viewmodel.common;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.Optional;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Event<T> {

    private T value;
    private boolean isHandled = false;

    public Event(@NonNull T value) {
        this.value = value;
    }

    public T getValueIfNotHandledOrNull() {
        if (!isHandled) {
            isHandled = true;
            return this.value;
        } else {
            return null;
        }
    }
}
