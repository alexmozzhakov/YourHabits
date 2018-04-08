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

  /**
   * Updates an entity in a database
   * @param habit A habit object to be updated
   */
  @Update
  void update(Habit habit);

  /**
   * Gets habit list from a database
   */
  @Query("SELECT * FROM habits")
  List<Habit> getAll();

  /**
   * Gets an entity in a database
   * @param id An id of an entity in a database
   */
  @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
  Habit get(long id);

  /**
   * Inserts an entity into a database
   * @param habit A habit object to be updated
   */
  @Insert
  long insert(Habit habit);

  /**
   * Deletes an entity from a database
   * @param habit A habit object to be deleted
   */
  @Delete
  void delete(Habit habit);

}
