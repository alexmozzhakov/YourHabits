package com.habit_track.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.habit_track.R;
import com.habit_track.models.Program;

import java.util.HashMap;
import java.util.Map;

public class ProgramFragment extends Fragment {

    public static Map<Integer, Program> programHashMap = new HashMap<>();
    public FloatingActionButton mFab;
    public DataSnapshot snapshot;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_program, container, false);

        mFab = (FloatingActionButton) result.findViewById(R.id.fab);

        final TextView title = (TextView) result.findViewById(R.id.title);
        final TextView description = (TextView) result.findViewById(R.id.description);

        title.setText(snapshot.child("name").getValue(String.class));
        description.setText(snapshot.child("habit").child("description").getValue(String.class));

        if (mFab != null) {
            mFab.setOnClickListener(v -> {
                int id = Integer.valueOf(snapshot.getKey());

                if (!programHashMap.containsKey(id)) {
                    final Program program = ProgramsFragment.onProgramApply(snapshot);
                    programHashMap.put(id, program);
                    Toast.makeText(getActivity(), "New program added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Program already added", Toast.LENGTH_SHORT).show();
                }

            });
        }

        return result;
    }


}
