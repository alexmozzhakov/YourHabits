package com.dohabit.fragments;

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

import com.google.firebase.database.DataSnapshot;
import com.dohabit.R;
import com.dohabit.models.Program;

public class ProgramFragment extends Fragment {

    private static final SparseArray<Program> programHashMap = new SparseArray<>();
    DataSnapshot snapshot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_program, container, false);

        FloatingActionButton fab = (FloatingActionButton) result.findViewById(R.id.fab);

        TextView description = (TextView) result.findViewById(R.id.description);

        description.setText(snapshot.child("habit").child("description").getValue(String.class));
        Log.i("Program fragment", snapshot.toString());

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

}
