package com.doapps.habits.helper;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import com.doapps.habits.adapter.HabitRecycleAdapter;
import com.doapps.habits.database.HabitsDatabase;
import com.doapps.habits.database.ProgramsDatabase;
import com.doapps.habits.listeners.EmptyListListener;
import com.doapps.habits.models.Habit;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HabitListManager {

  private static ProgramsDatabase programDatabase;
  private static HabitsDatabase habitsDatabase;
  private static volatile HabitListManager instance;

  private HabitListManager(Context context) {
    programDatabase = Room.databaseBuilder(context, ProgramsDatabase.class, "programs").build();
    habitsDatabase = Room.databaseBuilder(context, HabitsDatabase.class, "habits").build();
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

  public void onItemMove(int fromPosition, int toPosition,
      HabitRecycleAdapter habitRecycleAdapter) {
    new MoveTask(habitRecycleAdapter).execute(fromPosition, toPosition);
  }

  public HabitsDatabase getDatabase() {
    return habitsDatabase;
  }

  private static class DeleteTask extends AsyncTask<Integer, Void, Integer> {

    private HabitRecycleAdapter habitRecycleAdapter;
    private int habitsSize = 0;

    private DeleteTask(HabitRecycleAdapter habitRecycleAdapter) {
      this.habitRecycleAdapter = habitRecycleAdapter;
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
      List<Habit> habits = habitsDatabase.habitDao().getAll();
      habitsSize = habits.size() - 1;
      Habit habit = habits.get(integers[0]);
      if (habit.isProgram()) {
        habitsDatabase.habitDao().delete(habit);
      }
      habits.remove(habit);
      return integers[0];
    }

    @Override
    protected void onPostExecute(Integer integer) {
      EmptyListListener.Companion.getListener().isEmpty(habitsSize == 0);
      habitRecycleAdapter.notifyItemRemoved(integer);
    }
  }

  private static class MoveTask extends AsyncTask<Integer, Void, Integer[]> {

    private HabitRecycleAdapter habitRecycleAdapter;

    private MoveTask(HabitRecycleAdapter habitRecycleAdapter) {
      this.habitRecycleAdapter = habitRecycleAdapter;
    }

    @Override
    protected Integer[] doInBackground(Integer... integers) {
      int fromPosition = integers[0];
      int toPosition = integers[1];
      habitsDatabase.habitDao().move(fromPosition, toPosition);
      return new Integer[]{fromPosition, toPosition};
    }

    @Override
    protected void onPostExecute(Integer[] integers) {
      habitRecycleAdapter.notifyItemMoved(integers[0], integers[1]);
    }
  }

  private static class GetTask extends AsyncTask<Void, Void, List<Habit>> {

    @Override
    protected List<Habit> doInBackground(Void... voids) {
      return habitsDatabase.habitDao().getAll();
    }
  }
}
