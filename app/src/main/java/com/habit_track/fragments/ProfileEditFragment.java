package com.habit_track.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.habit_track.R;

public class ProfileEditFragment extends Fragment {

    private static final String TAG = ProfileEditFragment.class.getSimpleName();

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        FloatingActionButton fab = (FloatingActionButton) result.findViewById(R.id.fab);

        fab.setOnClickListener((view) -> {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                final String email =
                        String.valueOf(((EditText) result.findViewById(R.id.edit_email)).getText()).toLowerCase();

                final String name =
                        String.valueOf(((EditText) result.findViewById(R.id.edit_name)).getText());

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();

                if (!name.isEmpty() && !name.equals(user.getDisplayName())) {
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User profile updated.");
                                }
                            });
                } else {
                    Log.i("FA", "Name is same or empty");
                }

                if (!email.isEmpty() && !email.equals(user.getEmail())) {
                    user.updateEmail(email)
                            .addOnCompleteListener(task -> {

                               if (task.isSuccessful()) {
                                    Log.d(TAG, "User email address updated.");
                                }
                            });
                } else {
                    Log.i(TAG, "Email is same or empty");
                }
            } else {
                Log.e(TAG, "User is null");
            }
        });


        return result;
    }

}
