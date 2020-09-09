package com.kdpark.sickdan.model.repositoty;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DayWalk.class}, version = 1)
public abstract class AppDB extends RoomDatabase {
    public abstract DayWalkDao dayWalkDao();
}
