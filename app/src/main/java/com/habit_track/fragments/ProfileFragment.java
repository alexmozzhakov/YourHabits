package com.habit_track.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.habit_track.R;

public class ProfileFragment extends Fragment {
    static boolean[] editorOpened;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_profile, container, false);
        editorOpened = new boolean[]{false};
        final TextView name = (TextView) result.findViewById(R.id.name);
        final TextView email = (TextView) result.findViewById(R.id.email);
        FloatingActionButton fab = (FloatingActionButton) result.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if (!editorOpened[0]) {
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.userInfo, new ProfileEditFragment())
                        .commit();
                editorOpened[0] = true;
            }
        });

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
        }

        final SharedPreferences sharedPreferences = getContext().getSharedPreferences("pref",
                Context.MODE_PRIVATE);

        if (sharedPreferences.getString("celsius", null) != null) {
            final TextView address = (TextView) result.findViewById(R.id.address);
            address.setText(sharedPreferences.getString("location", "Error"));
        }
        return result;
    }
}
