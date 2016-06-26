package com.habit_track.helper;

import android.content.Context;

import com.habit_track.database.HabitDBHandler;
import com.habit_track.models.Habit;

import java.util.Calendar;
import java.util.List;

public class HabitListManager {
    private static HabitListManager habitListManager = null;
    private static List<Habit> mHabitsList = null;
    private final HabitDBHandler mHabitsDatabase;

    public static HabitListManager getInstance(final Context context) {
        if (habitListManager == null) {
            habitListManager = new HabitListManager(HabitDBHandler.getInstance(context));
        }
        if (habitListManager.mHabitsDatabase.notSame() || mHabitsList == null) {
            mHabitsList = habitListManager.mHabitsDatabase.getHabitDetailsAsArrayList();
        }

        return habitListManager;
    }

    public int getDueCount() {
        final Calendar calendar = Calendar.getInstance();
        final int date = calendar.get(Calendar.DATE);
        final int month = calendar.get(Calendar.MONTH);
        final int year = calendar.get(Calendar.YEAR);

        int counter = 0;
        for (final Habit habit : mHabitsList) {
            if (!habit.isDone(date, month, year)) {
                counter++;
            }
        }
        return counter;
    }
    public List<Habit> getHabitsList() {
        return mHabitsList;
    }
    private HabitListManager(final HabitDBHandler mHabitsDatabase) {
        this.mHabitsDatabase = mHabitsDatabase;
    }
}
