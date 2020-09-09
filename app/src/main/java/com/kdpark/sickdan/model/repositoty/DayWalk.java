package com.kdpark.sickdan.model.repositoty;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DayWalk {
    @PrimaryKey
    @NonNull
    public String date;

    @ColumnInfo(name = "walk_count")
    public int walkCount;

    public DayWalk(@NonNull String date, int walkCount) {
        this.date = date;
        this.walkCount = walkCount;
    }
}
