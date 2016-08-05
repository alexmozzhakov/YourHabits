package com.doapps.habits.helper;

import android.content.Context;
import android.util.Log;

import com.doapps.habits.database.HabitDatabaseHandler;
import com.doapps.habits.models.Habit;
import com.doapps.habits.models.HabitDatabaseMovableListProvider;
import com.doapps.habits.models.HabitsDatabase;

import java.util.Collections;
import java.util.List;

public class HabitListManager implements HabitDatabaseMovableListProvider {
    private List<Habit> mHabitsList;
    private final HabitsDatabase mHabitsDatabase;
    private static volatile HabitDatabaseMovableListProvider instance;

    public static HabitDatabaseMovableListProvider getInstance(final Context context) {
        if (instance == null) {
            synchronized (HabitListManager.class) {
                if (instance == null) {
                    instance = new HabitListManager(context);
                }
            }
        }
        return instance;
    }

    private HabitListManager(final Context context) {
        mHabitsDatabase = new HabitDatabaseHandler(context);
        mHabitsList = mHabitsDatabase.getHabitDetailsAsList();
    }

    @Override
    public List<Habit> getList() {
        if (HabitDatabaseHandler.notSame() || mHabitsList == null) {
            Log.w(String.valueOf(HabitDatabaseHandler.notSame()), String.valueOf(mHabitsList == null));
            mHabitsList = mHabitsDatabase.getHabitDetailsAsList();
        }

        return mHabitsList;
    }

    @Override
    public boolean isEmpty() {
        return mHabitsDatabase.isEmpty();
    }

    @Override
    public void onItemDismiss(final int position) {
        mHabitsDatabase.delete(mHabitsList.get(position).id);
        mHabitsList.remove(position);
    }

    @Override
    public void onItemMove(final int fromPosition, final int toPosition) {
        Collections.swap(mHabitsList, fromPosition, toPosition);
        final int fromPositionId = mHabitsList.get(fromPosition).id;
        mHabitsList.get(fromPosition).id = mHabitsList.get(toPosition).id;
        mHabitsList.get(toPosition).id = fromPositionId;
        mHabitsDatabase.move(mHabitsList.get(fromPosition).id, mHabitsList.get(toPosition).id);
    }

    @Override
    public HabitsDatabase getDatabase() {
        return mHabitsDatabase;
    }
}
