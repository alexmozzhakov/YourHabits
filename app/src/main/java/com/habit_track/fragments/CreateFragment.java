package com.habit_track.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.habit_track.R;

public class CreateFragment extends Fragment {

    private EditText editTitle;
    private EditText editDescription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_create, container, false);
        editTitle = (EditText) result.findViewById(R.id.editTitle);
        editDescription = (EditText) result.findViewById(R.id.editDescription);


        return result;
    }

}
