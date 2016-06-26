package com.habit_track.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.habit_track.R;
import com.habit_track.models.Program;

public class ProgramFragment extends Fragment {

    private static SparseArray<Program> programHashMap = new SparseArray<>();
    DataSnapshot snapshot;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_program, container, false);

        FloatingActionButton fab = (FloatingActionButton) result.findViewById(R.id.fab);

        final TextView description = (TextView) result.findViewById(R.id.description);

        description.setText(snapshot.child("habit").child("description").getValue(String.class));

        if (fab != null) {
            fab.setOnClickListener(view -> {
                int id = Integer.valueOf(snapshot.getKey());

                if (programHashMap.get(id) == null) {
                    final Program program = ProgramsFragment.onProgramApply(snapshot, getActivity());
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
