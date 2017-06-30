package com.doapps.habits.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.doapps.habits.models.Habit;

import java.util.List;

@Dao
public interface HabitDao {
    @Update
    void update(Habit habit);

    @Query("SELECT * FROM habits")
    List<Habit> getAll();

    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    Habit get(long id);

    @Insert
    long insert(Habit habit);

    default void move(int fromPosition, int toPosition) {
        moveOne(fromPosition);
        moveTwo(fromPosition, toPosition);
        moveThree(toPosition);
    }

    @Query("UPDATE habits SET id = 0 WHERE id = :fromPosition")
    void moveOne(int fromPosition);

    @Query("UPDATE habits SET id = :fromPosition WHERE id = :toPosition")
    void moveTwo(int fromPosition, int toPosition);

    @Query("UPDATE habits SET id = :toPosition WHERE id = 0")
    void moveThree(int toPosition);

    @Delete
    void delete(Habit habit);

}
