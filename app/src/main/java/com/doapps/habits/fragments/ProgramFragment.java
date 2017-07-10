package com.doapps.habits.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.doapps.habits.BuildConfig;
import com.doapps.habits.R;
import com.doapps.habits.converters.IntegerArrayConverter;
import com.doapps.habits.dao.HabitDao;
import com.doapps.habits.database.ProgramsDatabase;
import com.doapps.habits.helper.HabitListManager;
import com.doapps.habits.models.Achievement;
import com.doapps.habits.models.Habit;
import com.doapps.habits.models.Program;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ProgramFragment extends Fragment {

  public static ProgramsDatabase database;
  public static HabitDao habitDatabase;
  private DatabaseReference programRef;

  private static List<Achievement> createAchievementList(DataSnapshot dataSnapshot) {
    List<Achievement> achievements = new ArrayList<>((int) dataSnapshot.getChildrenCount());
    for (DataSnapshot achievementSnapshot : dataSnapshot.getChildren()) {
      List<String> templates =
          new ArrayList<>((int) achievementSnapshot.getChildrenCount());
      for (DataSnapshot templatesSnapshot : achievementSnapshot.child("templates").getChildren()) {
        templates.add(templatesSnapshot.child("name").getValue(String.class));
      }
      Achievement achievement = new Achievement(
          achievementSnapshot.child("rating").getValue(Integer.class),
          templates);
      achievements.add(achievement);
    }
    return achievements;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View result = inflater.inflate(R.layout.fragment_program, container, false);

    FloatingActionButton fab = result.findViewById(R.id.fab);
    TextView description = result.findViewById(R.id.description);
    database = HabitListManager.getInstance(getContext()).getProgramDatabase();
    habitDatabase = HabitListManager.getInstance(getContext()).getDatabase().habitDao();

    int position = getArguments().getInt("pos", 0);
    programRef = FirebaseDatabase.getInstance().getReference().child("programs")
        .child(Integer.toString(position));
    programRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        description
            .setText(dataSnapshot.child("habit").child("description").getValue(String.class));

        if (BuildConfig.DEBUG) {
          Log.i("Program fragment", dataSnapshot.toString());
        }

        if (fab != null) {
          fab.setOnClickListener(view -> new InsertIfNotExists(dataSnapshot).execute());
        }

      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.e("onCancelled", databaseError.getMessage());
        getActivity().getSupportFragmentManager().beginTransaction().remove(ProgramFragment.this)
            .commit();
      }
    });
    return result;
  }

  private static class InsertTask extends AsyncTask<Program, Void, Void> {

    @Override
    protected Void doInBackground(Program... programs) {
      Log.i("DatabaseContains", "New program added");
      database.programDao().insertAll(programs);
      new DatabasePrintTask().execute();
      return null;
    }
  }

  private static class InsertIfNotExists extends AsyncTask<Void, Void, Void> {

    private final DataSnapshot mSnapshot;

    InsertIfNotExists(DataSnapshot snapshot) {
      mSnapshot = snapshot;
    }

    @Override
    protected Void doInBackground(Void... voids) {
      if (database.programDao().idExists(Integer.parseInt(mSnapshot.getKey())) == 0) {
        new InsertHabitTask(mSnapshot).execute();
      } else {
        Log.i("DatabaseContains", "Program already added");
      }
      return null;
    }
  }

  private static class DatabasePrintTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... voids) {
      Log.i("New Database: ", Arrays.toString(database.programDao().getAll().toArray()));
      return null;
    }
  }

  @SuppressLint("StaticFieldLeak")
  private static class InsertHabitTask extends AsyncTask<Void, Void, Long> {

    private final DataSnapshot dataSnapshot;

    InsertHabitTask(DataSnapshot dataSnapshot) {
      this.dataSnapshot = dataSnapshot;
    }

    @Override
    protected Long doInBackground(Void... voids) {
      Log.i("DatabaseContains", "New program added");
      Calendar upd = Calendar.getInstance();
      Habit habit = new Habit(
          dataSnapshot.child("habit").child("title").getValue(String.class),
          dataSnapshot.child("habit").child("question").getValue(String.class),
          false,
          upd.get(Calendar.DATE),
          upd.get(Calendar.MONTH),
          upd.get(Calendar.YEAR),
          dataSnapshot.child("habit").child("time").getValue(Integer.class),
          System.currentTimeMillis(),
          dataSnapshot.child("habit").child("cost").getValue(Integer.class),
          IntegerArrayConverter.fromArray(
              dataSnapshot.child("habit").child("frequency").getValue(String.class)));
      return habitDatabase.insert(habit);
    }

    @Override
    protected void onPostExecute(Long id) {
      super.onPostExecute(id);
      List<Achievement> achievements = createAchievementList(dataSnapshot.child("achievements"));

      Program program = new Program(
          Integer.valueOf(dataSnapshot.getKey()),
          dataSnapshot.child("name").getValue(String.class),
          String.format("%s SUCCESS", dataSnapshot.child("success").getValue(String.class)),
          id,
          achievements
      );
      new InsertTask().execute(program);
    }
  }
}
