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

  @Delete
  void delete(Habit habit);

}
