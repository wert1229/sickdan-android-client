package com.kdpark.sickdan.model.repositoty;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DayWalkDao {
    @Query("SELECT * FROM daywalk")
    List<DayWalk> getAll();

    @Query("SELECT * FROM daywalk where date = :date")
    DayWalk getByDate(String date);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DayWalk dayWalk);

    @Delete
    void delete(DayWalk dayWalk);

    @Update
    void update(DayWalk dayWalk);
}
