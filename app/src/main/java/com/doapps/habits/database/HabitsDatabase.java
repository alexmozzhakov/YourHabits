package com.doapps.habits.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import com.doapps.habits.converters.IntegerArrayConverter;
import com.doapps.habits.dao.HabitDao;
import com.doapps.habits.models.Habit;

@Database(entities = {Habit.class}, version = 1)
@TypeConverters({IntegerArrayConverter.class})
public abstract class HabitsDatabase extends RoomDatabase {

  public abstract HabitDao habitDao();
}
