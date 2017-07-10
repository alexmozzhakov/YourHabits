package com.doapps.habits.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.util.Log;
import com.doapps.habits.BuildConfig;
import com.doapps.habits.converters.IntegerArrayConverter;
import com.doapps.habits.dao.HabitDao;
import com.doapps.habits.models.Habit;
import java.util.Calendar;

@Database(entities = {Habit.class}, version = 1)
@TypeConverters({IntegerArrayConverter.class})
public abstract class HabitsDatabase extends RoomDatabase {

  /**
   * TAG is defined for logging errors and debugging information
   */
  private static final String TAG = HabitsDatabase.class.getSimpleName();

  private static int daysTillNow(Habit habit) {
    Calendar updateDate = Calendar.getInstance();
    updateDate.set(habit.getMarkerUpdatedYear(), habit.getMarkerUpdatedMonth(),
        habit.getMarkerUpdatedDay());

    long millis1 = System.currentTimeMillis();
    long millis2 = updateDate.getTimeInMillis();
    long diff = millis2 - millis1;
    if (BuildConfig.DEBUG) {
      Log.i(TAG, "Not marked for " + diff / 86400000);
    }
    return (int) (diff / 86400000);
  }

  public static boolean mustHaveFollowed(Habit habit) {
    int daysTillNow = daysTillNow(habit);
    int[] freq = habit.getFrequencyArray();
    if (BuildConfig.DEBUG) {
      Log.i(TAG, "Must have followed = " + (daysTillNow > freq[freq.length - 1]));
    }
    return daysTillNow > freq[freq.length - 1];
  }

  public abstract HabitDao habitDao();
}
