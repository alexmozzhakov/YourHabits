package com.habit_track.habit_track.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.habit_track.R;
import com.habit_track.habit_track.activity.MainActivity;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_profile, container, false);

        final TextView name = (TextView) result.findViewById(R.id.name);
        final TextView email = (TextView) result.findViewById(R.id.email);
        name.setText(MainActivity.mUser.get("name"));
        email.setText(MainActivity.mUser.get("email"));

        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("pref",
                Context.MODE_PRIVATE);

        if (sharedPreferences.getString("celsius", null) != null) {
            final TextView address = (TextView) result.findViewById(R.id.address);
            address.setText(sharedPreferences.getString("location", "Error"));
        }
        return result;
    }


}
