package com.doapps.habits.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import com.doapps.habits.converters.IntegerArrayConverter;
import com.doapps.habits.dao.HabitDao;
import com.doapps.habits.models.Habit;

/**
 * The habits database.
 */
@Database(entities = {Habit.class}, version = 1)
@TypeConverters({IntegerArrayConverter.class})
public abstract class HabitsDatabase extends RoomDatabase {

  /**
   * Gets database data access object
   *
   * @return Data Access Object
   */
  public abstract HabitDao habitDao();
}
