package com.doapps.habits.helper;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import com.doapps.habits.adapter.HabitRecycleAdapter;
import com.doapps.habits.database.HabitsDatabase;
import com.doapps.habits.database.ProgramsDatabase;
import com.doapps.habits.listeners.EmptyListListener;
import com.doapps.habits.models.Habit;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HabitListManager {
    //    private final IHabitsDatabase mHabitsDatabase;
    private static ProgramsDatabase programDatabase;
    private static HabitsDatabase habitsDatabase;
    private static volatile HabitListManager instance;
    private List<Habit> mHabitsList;

    private HabitListManager(Context context) {
        programDatabase =
                Room.databaseBuilder(context.getApplicationContext(), ProgramsDatabase.class, "programs").build();
        habitsDatabase =
                Room.databaseBuilder(context.getApplicationContext(), HabitsDatabase.class, "habits").build();
//        mHabitsDatabase = new HabitDatabaseHandler(context);
//        mHabitsList = mHabitsDatabase.getHabitDetailsAsList();
//        mHabitsList = getList();
    }

    public static HabitListManager getInstance(Context context) {
        if (instance == null) {
            synchronized (HabitListManager.class) {
                if (instance == null) {
                    instance = new HabitListManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public ProgramsDatabase getProgramDatabase() {
        return programDatabase;
    }

    public List<Habit> getList() throws InterruptedException, ExecutionException {
        return (new GetTask()).execute().get();
    }

    public void onItemDismiss(int position, HabitRecycleAdapter habitRecycleAdapter) {
        new DeleteTask(habitRecycleAdapter).execute(position);
    }

    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mHabitsList, fromPosition, toPosition);
        int fromPositionId = mHabitsList.get(fromPosition).getId();
        int toPositionId = mHabitsList.get(toPosition).getId();
        mHabitsList.get(fromPosition).setId(toPositionId);
        mHabitsList.get(toPosition).setId(fromPositionId);
        habitsDatabase.habitDao().move(fromPosition, toPosition);
    }

    public HabitsDatabase getDatabase() {
        return habitsDatabase;
    }

    private static class DeleteTask extends AsyncTask<Integer, Void, Integer> {

        private HabitRecycleAdapter habitRecycleAdapter;

        private DeleteTask(HabitRecycleAdapter habitRecycleAdapter) {
            this.habitRecycleAdapter = habitRecycleAdapter;
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            List<Habit> habits = habitsDatabase.habitDao().getAll();
            Habit habit = habits.get(integers[0]);
            if (habit.isProgram())
                habitsDatabase.habitDao().delete(habit);
            EmptyListListener.listener.isEmpty(habits.size() == 0);

            return integers[0];
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            habitRecycleAdapter.notifyItemRemoved(integer);
        }
    }

    private static class GetTask extends AsyncTask<Void, Void, List<Habit>> {

        @Override
        protected List<Habit> doInBackground(Void... voids) {
            return habitsDatabase.habitDao().getAll();
        }
    }
}
