package com.doapps.habits.helper;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import com.doapps.habits.database.HabitsDatabase;
import com.doapps.habits.database.ProgramsDatabase;
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

  public void onItemDismiss(int position) {
    new DeleteTask().execute(position);
  }

  public void onItemMove(Habit fromPosition, Habit toPosition) {
    new MoveTask().execute(fromPosition, toPosition);
  }

  public HabitsDatabase getDatabase() {
    return habitsDatabase;
  }

  private static class DeleteTask extends AsyncTask<Integer, Void, Void> {

    @Override
    protected Void doInBackground(Integer... integers) {
      List<Habit> habits = habitsDatabase.habitDao().getAll();
      Habit habit = habits.get(integers[0]);
//      if (habit.isProgram()) {
        habitsDatabase.habitDao().delete(habit);
//      }
//      habits.remove(habit);
      return null;
    }
  }

  private static class MoveTask extends AsyncTask<Habit, Void, Void> {

    @Override
    protected Void doInBackground(Habit... habits) {
      habitsDatabase.habitDao().update(habits[0]);
      habitsDatabase.habitDao().update(habits[1]);
      return null;
    }
  }

  private static class GetTask extends AsyncTask<Void, Void, List<Habit>> {

    @Override
    protected List<Habit> doInBackground(Void... voids) {
      return habitsDatabase.habitDao().getAll();
    }
  }
}
