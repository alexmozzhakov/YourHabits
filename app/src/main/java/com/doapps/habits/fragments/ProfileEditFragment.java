package com.doapps.habits.fragments;

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

import com.doapps.habits.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;

import static com.doapps.habits.fragments.ProfileFragment.editorOpened;

public class ProfileEditFragment extends Fragment {

    private String inputPassword = "";
    private static final String TAG = ProfileEditFragment.class.getSimpleName();
    private FloatingActionButton fab;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        getPasswordFromUser();
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);

        fab.setImageResource(R.drawable.ic_action_name);
        fab.setOnClickListener(view -> {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null && user.getEmail() != null) {

                final String email =
                        String.valueOf(((TextView) result.findViewById(R.id.edit_email)).getText()).toLowerCase();
                final String name =
                        String.valueOf(((TextView) result.findViewById(R.id.edit_name)).getText());

                if (isUpdated(user, email, name)) { // email same)
                    editorOpened[0] = false;
                    getFragmentManager().beginTransaction().remove(this).commit();
                    return;
                }
                if (inputPassword.isEmpty()) { // entered password empty
                    Toast.makeText(getContext().getApplicationContext(), "Please enter correct password", Toast.LENGTH_SHORT)
                            .show();
                    getPasswordFromUser();
                    return;
                }

                final AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), inputPassword);

                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(task -> Log.d(TAG, "User re-authenticated."));


                final TextView nameView =
                        (TextView) getActivity().findViewById(R.id.name);
                final TextView emailView =
                        (TextView) getActivity().findViewById(R.id.email);

                final UserProfileChangeRequest profileUpdates =
                        new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();

                if (!name.isEmpty() && !name.equals(user.getDisplayName())) {
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    final NavigationView navigationView =
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

                editorOpened[0] = false;
                fab.setOnClickListener(v -> {
                    if (!editorOpened[0]) {
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.userInfo, new ProfileEditFragment())
                                .commit();
                        editorOpened[0] = true;
                    }
                });
                getFragmentManager().beginTransaction().remove(this).commit();

            } else {
                Log.e(TAG, "User is null");
            }
        });


        return result;
    }

    private static boolean isUpdated(final UserInfo user, final String email, final String name) {
        final boolean nameUpdated = !name.isEmpty() && !name.equals(user.getDisplayName());
        final boolean emailUpdated = !email.isEmpty() && !email.equals(user.getEmail());
        return nameUpdated && emailUpdated;
    }

    private void getPasswordFromUser() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Please re-enter your password");

        // Set up the input
        final EditText input = new EditText(getActivity());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> inputPassword = input.getText().toString());
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            editorOpened[0] = false;
            fab.setImageResource(R.drawable.ic_edit_white_24dp);
            fab.setOnClickListener(v -> {
                if (!editorOpened[0]) {
                    getParentFragment().getChildFragmentManager()
                            .beginTransaction()
                            .replace(R.id.userInfo, new ProfileEditFragment())
                            .commit();
                    editorOpened[0] = true;
                }
            });
            getFragmentManager().beginTransaction().remove(this).commit();
        });

        builder.show();
    }


}
