package com.doapps.habits.helper;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.doapps.habits.database.HabitDatabaseHandler;
import com.doapps.habits.models.Habit;
import com.doapps.habits.models.IHabitDatabaseMovableListProvider;
import com.doapps.habits.models.IHabitsDatabase;
import com.doapps.habits.models.Program;

import java.util.Collections;
import java.util.List;

public class HabitListManager implements IHabitDatabaseMovableListProvider, ProgramsManager {
    private List<Habit> mHabitsList;
    private final IHabitsDatabase mHabitsDatabase;
    private final SparseArray<Program> programsAdded = new SparseArray<>();
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
        final int id = mHabitsList.get(position).getId();
        if (mHabitsList.get(position).isProgram()) {
            for (int i = 0; i < programsAdded.size(); i++) {
                Program program = programsAdded.get(programsAdded.keyAt(i));
                Log.d("Check", program.toString());
                if (program.getHabitId() == mHabitsList.get(position).getId()) {
                    programsAdded.remove(programsAdded.keyAt(i));
                }
            }

        }
        mHabitsList.remove(position);
        mHabitsDatabase.delete(id);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mHabitsList, fromPosition, toPosition);
        int fromPositionId = mHabitsList.get(fromPosition).getId();
        mHabitsList.get(fromPosition).setId(mHabitsList.get(toPosition).getId());
        mHabitsList.get(toPosition).setId(fromPositionId);
        mHabitsDatabase.move(mHabitsList.get(fromPosition).getId(), mHabitsList.get(toPosition).getId());
    }

    @Override
    public IHabitsDatabase getDatabase() {
        return mHabitsDatabase;
    }

    @Override
    public SparseArray<Program> getProgramsAdded() {
        return programsAdded;
    }
}
