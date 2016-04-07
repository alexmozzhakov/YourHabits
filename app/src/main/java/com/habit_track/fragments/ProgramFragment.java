package com.habit_track.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.habit_track.R;

public class ProgramFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_program, container, false);
        final Bundle bundle = getArguments();
        if (bundle != null) {
            final String receiveTitle = bundle.getString("title");
            final String receiveInfo = bundle.getString("description");

            ((TextView) result.findViewById(R.id.title)).setText(receiveTitle);
            ((TextView) result.findViewById(R.id.description)).setText(receiveInfo);
        }
        return result;
    }


}
