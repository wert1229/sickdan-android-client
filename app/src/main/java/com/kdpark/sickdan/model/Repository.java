package com.kdpark.sickdan.model;

import android.content.Context;

import androidx.room.Room;

import com.kdpark.sickdan.model.repositoty.AppDB;

public class Repository {
    private static AppDB db;

    public static AppDB getInstance(Context context) {
        if (db == null)
            db = Room.databaseBuilder(context, AppDB.class, "database").build();

        return db;
    }
}
