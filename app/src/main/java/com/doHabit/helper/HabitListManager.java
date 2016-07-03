package com.dohabit.helper;

import android.content.Context;

import com.dohabit.database.HabitDBHandler;
import com.dohabit.models.Habit;

import java.util.List;

public class HabitListManager {
    private static List<Habit> mHabitsList;

    public static List<Habit> getInstance(final Context context) {
        if (!HabitDBHandler.isSame() || mHabitsList == null) {
            mHabitsList = HabitDBHandler.getHabitDatabase(context).getHabitDetailsAsList();
        }

        return mHabitsList;
    }

    public static List<Habit> getHabitsList() {
        return mHabitsList;
    }
}
