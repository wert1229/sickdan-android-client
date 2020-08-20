package com.kdpark.sickdan.viewmodel;

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Optional<T> getValueIfNotHandled() {
        if (!isHandled) {
            isHandled = true;
            return Optional.of(this.value);
        } else {
            return null;
        }
    }
}
