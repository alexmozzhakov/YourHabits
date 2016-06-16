package com.habit_track.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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

            if (user != null && user.getEmail() != null) {
                final AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), "123456");

                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(task -> Log.d(TAG, "User re-authenticated."));

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
                                    NavigationView navigationView =
                                            (NavigationView) result.findViewById(R.id.navigationView);


                                    // TODO: 16/06/2016 change name and email in menu
                                    ((TextView) navigationView.getHeaderView(0)
                                            .findViewById(R.id.name_info))
                                            .setText(user.getDisplayName());

                                    ((TextView) navigationView
                                            .getHeaderView(0).findViewById(R.id.email_info))
                                            .setText(user.getEmail());

                                    final TextView nameView =
                                            (TextView) result.findViewById(R.id.name);
                                    final TextView emailView =
                                            (TextView) result.findViewById(R.id.email);

                                    nameView.setText(user.getDisplayName());
                                    emailView.setText(user.getEmail());

                                    Log.d(TAG, "User profile updated.");
                                }
                            });
                } else {
                    Log.i(TAG, "Name is same or empty");
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

                getFragmentManager().beginTransaction().remove(this).commit();

            } else {
                Log.e(TAG, "User is null");
            }
        });


        return result;
    }

}
