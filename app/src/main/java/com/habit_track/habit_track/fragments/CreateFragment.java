package com.habit_track.habit_track.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.habit_track.R;
import com.habit_track.habit_track.activity.MainActivity;

public class CreateFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MainActivity.immOpened = true;
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

}
