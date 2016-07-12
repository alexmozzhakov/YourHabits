package com.doapps.habits.helper;

import android.content.Context;

import com.doapps.habits.database.HabitDatabaseHandler;
import com.doapps.habits.models.Habit;
import com.doapps.habits.models.HabitDatabaseMovableListProvider;
import com.doapps.habits.models.HabitsDatabase;

import java.util.Collections;
import java.util.List;

public class HabitListManager implements HabitDatabaseMovableListProvider {
    private static List<Habit> mHabitsList;
    private static HabitsDatabase mHabitsDatabase;

    public HabitListManager(final Context context) {
        mHabitsDatabase = new HabitDatabaseHandler(context);
    }

    @Override
    public List<Habit> getList() {
        if (!HabitDatabaseHandler.isSame() || mHabitsList == null) {
            mHabitsList = mHabitsDatabase.getHabitDetailsAsList();
        }

        return mHabitsList;
    }

    @Override
    public boolean isEmpty() {
        // TODO: 11/07/2016 refactor to something more efficient
        return getList().isEmpty();
    }

    @Override
    public void onItemDismiss(final int position) {
        mHabitsDatabase.delete(mHabitsList.get(position).id);
        mHabitsList.remove(position);
    }

    @Override
    public void onItemMove(final int fromPosition, final int toPosition) {

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mHabitsList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mHabitsList, i, i - 1);
            }
        }
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
