package com.dohabit.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.dohabit.R;

public class ProfileEditFragment extends Fragment {

    private String inputPassword = "";
    private static final String TAG = ProfileEditFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        getPasswordFromUser();
        FloatingActionButton fab = (FloatingActionButton) result.findViewById(R.id.fab);

        fab.setOnClickListener(view -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null && user.getEmail() != null) {

                String email =
                        String.valueOf(((EditText) result.findViewById(R.id.edit_email)).getText()).toLowerCase();
                String name =
                        String.valueOf(((EditText) result.findViewById(R.id.edit_name)).getText());

                if ((name.isEmpty() || name.equals(user.getDisplayName())) // name same
                        && (email.isEmpty() || email.equals(user.getEmail()))) { // email same)
                    ProfileFragment.editorOpened[0] = false;
                    getFragmentManager().beginTransaction().remove(this).commit();
                    return;
                }
                if (inputPassword.isEmpty()) { // entered password empty
                    Toast.makeText(getContext(), "Please enter correct password", Toast.LENGTH_SHORT)
                            .show();
                    getPasswordFromUser();
                    return;
                }

                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), inputPassword);

                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(task -> Log.d(TAG, "User re-authenticated."));


                TextView nameView =
                        (TextView) getActivity().findViewById(R.id.name);
                TextView emailView =
                        (TextView) getActivity().findViewById(R.id.email);

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();

                if (!name.isEmpty() && !name.equals(user.getDisplayName())) {
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    NavigationView navigationView =
                                            (NavigationView) getParentFragment()
                                                    .getActivity()
                                                    .findViewById(R.id.navigationView);

                                    ((TextView) navigationView
                                            .getHeaderView(0).findViewById(R.id.name_info))
                                            .setText(user.getDisplayName());

                                    ((TextView) navigationView
                                            .getHeaderView(0).findViewById(R.id.email_info))
                                            .setText(user.getEmail());


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

                ProfileFragment.editorOpened[0] = false;
                getFragmentManager().beginTransaction().remove(this).commit();

            } else {
                Log.e(TAG, "User is null");
            }
        });


        return result;
    }

    private void getPasswordFromUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Please re-enter your password");

        // Set up the input
        EditText input = new EditText(getActivity());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> inputPassword = input.getText().toString());
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            ProfileFragment.editorOpened[0] = false;
            getFragmentManager().beginTransaction().remove(this).commit();
        });

        builder.show();
    }


}
