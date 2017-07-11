package com.doapps.habits.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.util.Log;
import com.doapps.habits.models.Habit;
import java.util.List;

// TODO: 7/7/17 isEmpty
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
    updateId(fromPosition, 0); // fromPosition -> 0
    Log.i("DAO", "UPDATE habits SET id = 0 WHERE id = fromPosition");
    updateId(toPosition, fromPosition); // toPosition -> fromPosition
    Log.i("DAO", "UPDATE habits SET id = fromPosition WHERE id = toPosition");
    updateId(0, toPosition); // 0 -> fromPosition
    Log.i("DAO", "UPDATE habits SET id = toPosition WHERE id = 0");
  }

  @Query("UPDATE habits SET id = :toPosition WHERE id = :fromPosition")
  void updateId(int fromPosition, int toPosition);

  @Delete
  void delete(Habit habit);

}
