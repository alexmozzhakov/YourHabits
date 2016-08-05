package com.doapps.habits.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.doapps.habits.BuildConfig;
import com.doapps.habits.R;
import com.doapps.habits.helper.HabitListManager;
import com.doapps.habits.models.Achievement;
import com.doapps.habits.models.HabitsDatabase;
import com.doapps.habits.models.Program;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ProgramFragment extends Fragment {

    private static final SparseArray<Program> programHashMap = new SparseArray<>();
    private DataSnapshot snapshot;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_program, container, false);

        final FloatingActionButton fab = (FloatingActionButton) result.findViewById(R.id.fab);

        final TextView description = (TextView) result.findViewById(R.id.description);

        description.setText(snapshot.child("habit").child("description").getValue(String.class));
        if (BuildConfig.DEBUG) {
            Log.i("Program fragment", snapshot.toString());
        }

        if (fab != null) {
            fab.setOnClickListener(view -> {
                final int id = Integer.valueOf(snapshot.getKey());

                if (programHashMap.get(id) == null) {
                    final Program program = onProgramApply(snapshot, getContext());
                    programHashMap.put(id, program);
                    Toast.makeText(getActivity(), "New program added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Program already added", Toast.LENGTH_SHORT).show();
                }

            });
        }

        return result;
    }

    void setSnapshot(final DataSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    private static Program onProgramApply(final DataSnapshot dataSnapshot, final Context context) {
        final HabitsDatabase habitsDatabase = HabitListManager.getInstance(context).getDatabase();
        final long habitId = habitsDatabase.addHabit(
                dataSnapshot.child("habit").child("title").getValue(String.class),
                dataSnapshot.child("habit").child("question").getValue(String.class),
                dataSnapshot.child("habit").child("time").getValue(Integer.class),
                Calendar.getInstance(),
                dataSnapshot.child("habit").child("cost").getValue(Integer.class),
                dataSnapshot.child("habit").child("frequency").getValue(String.class)
        );

        final List<Achievement> achievements =
                createAchievementList(dataSnapshot.child("achievements"));

        return new Program(
                Integer.valueOf(dataSnapshot.getKey()),
                dataSnapshot.child("name").getValue(String.class),
                String.format("%s SUCCESS", dataSnapshot.child("success").getValue(String.class)),
                habitId,
                achievements
        );
    }

    private static List<Achievement> createAchievementList(final DataSnapshot dataSnapshot) {
        final List<Achievement> achievements = new ArrayList<>((int) dataSnapshot.getChildrenCount());
        for (final DataSnapshot achievementSnapshot : dataSnapshot.getChildren()) {
            final List<String> templates =
                    new ArrayList<>((int) achievementSnapshot.getChildrenCount());
            for (final DataSnapshot templatesSnapshot : achievementSnapshot.child("templates").getChildren()) {
                templates.add(templatesSnapshot.child("name").getValue(String.class));
            }
            final Achievement achievement = new Achievement(
                    achievementSnapshot.child("rating").getValue(Integer.class),
                    templates);
            achievements.add(achievement);
        }
        return achievements;
    }
}
