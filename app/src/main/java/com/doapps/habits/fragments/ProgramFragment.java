package com.doapps.habits.fragments;

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
import com.doapps.habits.models.Program;
import com.google.firebase.database.DataSnapshot;

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
                    final Program program = ProgramsFragment.onProgramApply(snapshot, getContext());
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
}
