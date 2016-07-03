package com.dohabit.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.dohabit.R;

public class ProfileFragment extends Fragment {
    static boolean[] editorOpened;
    private String inputPassword = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_profile, container, false);
        editorOpened = new boolean[]{false};
        TextView name = (TextView) result.findViewById(R.id.name);
        TextView email = (TextView) result.findViewById(R.id.email);
        Button btnLogout = (Button) result.findViewById(R.id.btn_delete_user);
//        final ImageView imageView = (ImageView) result.findViewById(R.id.profile_photo);
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if (user != null) {
            btnLogout.setOnClickListener(view -> {
                reauthenticate(user);
            });
            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
            if (user.getPhotoUrl() != null) {
//                imageView.setImageURI(user.getPhotoUrl());
            } else {
//                imageView.setImageResource(R.drawable.bg_img);
//                imageView.setOnClickListener(view -> {
//
//                    Uri uri = Uri.parse("http://habbitsapp.esy.es/uploads/" + user.getUid());
//                    new UserProfileChangeRequest.Builder()
//                            .setPhotoUri(uri).build();

                getChildFragmentManager()
                        .beginTransaction()
                        .add(R.id.userInfo, new EditPhotoFragment())
                        .commit();
//            }
            }
        }

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("pref",
                Context.MODE_PRIVATE);

        if (sharedPreferences.getString("location", null) != null)

        {
            TextView address = (TextView) result.findViewById(R.id.address);
            address.setText(sharedPreferences.getString("location", "Error"));
        }

        return result;
    }

    private void reauthenticate(FirebaseUser user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Please re-enter your password");

        // Set up the input
        EditText input = new EditText(getActivity());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            inputPassword = input.getText().toString();
            if (!inputPassword.isEmpty() && user.getEmail() != null) {
                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), inputPassword);
                user.reauthenticate(credential).addOnCompleteListener(task ->
                        user.delete().addOnCompleteListener(t -> {
                            if (task.isSuccessful()) {
                                Log.d("FA", "User account deleted.");
                            } else {
                                Toast.makeText(getContext().getApplicationContext(), "Password isn't correct",
                                        Toast.LENGTH_SHORT);
                            }
                        }));
            } else if (inputPassword.isEmpty()) {
                Toast.makeText(getContext().getApplicationContext(), "Password was empty",
                        Toast.LENGTH_SHORT);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }
}
