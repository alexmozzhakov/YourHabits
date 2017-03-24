package com.doapps.habits.helper;

import android.content.Context;
import android.util.Log;

import com.doapps.habits.database.HabitDatabaseHandler;
import com.doapps.habits.models.Habit;
import com.doapps.habits.models.IHabitDatabaseMovableListProvider;
import com.doapps.habits.models.IHabitsDatabase;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class HabitListManager implements IHabitDatabaseMovableListProvider {
    private List<Habit> mHabitsList;
    private final IHabitsDatabase mHabitsDatabase;
    private static volatile IHabitDatabaseMovableListProvider instance;

    public static IHabitDatabaseMovableListProvider getInstance(Context context) {
        if (instance == null) {
            synchronized (HabitListManager.class) {
                if (instance == null) {
                    instance = new HabitListManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private HabitListManager(Context context) {
        mHabitsDatabase = new HabitDatabaseHandler(context);
        mHabitsList = mHabitsDatabase.getHabitDetailsAsList();
    }

    @Override
    public List<Habit> getList() {
        if (HabitDatabaseHandler.notSame() || mHabitsList == null) {
            Log.i("t", String.valueOf(mHabitsList == null));
            mHabitsList = mHabitsDatabase.getHabitDetailsAsList();
        }

        return mHabitsList;
    }

    @Override
    public boolean isEmpty() {
        return mHabitsDatabase.isEmpty();
    }

    @Override
    public void onItemDismiss(int position) {
        final int id = mHabitsList.get(position).id;
        mHabitsList.remove(position);
        mHabitsDatabase.delete(id);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mHabitsList, fromPosition, toPosition);
        int fromPositionId = mHabitsList.get(fromPosition).id;
        mHabitsList.get(fromPosition).id = mHabitsList.get(toPosition).id;
        mHabitsList.get(toPosition).id = fromPositionId;
        mHabitsDatabase.move(mHabitsList.get(fromPosition).id, mHabitsList.get(toPosition).id);
    }

    /**
     * @return String value of number of incomplete habits
     */
    public CharSequence getDueCount() {
        Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        int counter = 0;
        for (Habit habit : mHabitsList) {
            if (!habit.isDone(date, month, year)) {
                counter++;
            }
        }
        return String.valueOf(counter);
    }

    @Override
    public IHabitsDatabase getDatabase() {
        return mHabitsDatabase;
    }
}
