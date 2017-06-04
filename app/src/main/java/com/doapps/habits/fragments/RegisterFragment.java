package com.doapps.habits.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.doapps.habits.R;
import com.doapps.habits.listeners.NameChangeListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    public static final Pattern NAME_PATTERN = Pattern.compile("[\\s]^[0-9][^±!@£$%^&*_+§¡€#¢§¶•ªº«\\\\/<>?:;|=.,]{1,20}$");
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-" +
                    "\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\" +
                    "x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z" +
                    "0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?" +
                    ")\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\" +
                    "x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\" +
                    "x0b\\x0c\\x0e-\\x7f])+)\\])");

    public static boolean isValidPattern(CharSequence sequence, Pattern pattern) {
        return pattern.matcher(sequence).matches();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_register, container, false);
        EditText inputFullName = result.findViewById(R.id.full_name);
        EditText inputEmail = result.findViewById(R.id.email);
        EditText inputPassword = result.findViewById(R.id.password);
        Button btnRegister = result.findViewById(R.id.btnCreate);
        Button btnLinkToLogin = result.findViewById(R.id.btnLinkToLoginScreen);

        btnLinkToLogin.setOnClickListener(view -> toLoginActivity());
        inputEmail.setText(
                getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE)
                        .getString("last email", ""));

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // Register Button Click event
        btnRegister.setOnClickListener(view -> {
                    String name = inputFullName.getText().toString().trim();
                    String email = inputEmail.getText().toString().trim();
                    String password = inputPassword.getText().toString().trim();

                    if (!email.isEmpty() && !password.isEmpty() && !name.isEmpty()) {
                        if (isValidPattern(name, NAME_PATTERN)) {
                            if (isValidPattern(email, EMAIL_PATTERN)) {
                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(getActivity(), task -> {
                                            if (task.isSuccessful()) {
                                                setUserName(name);
                                                toLoginActivity();
                                            } else {
                                                handleRegisterError(task, getActivity());
                                            }
                                        });
                            } else {
                                Toast.makeText(getActivity(), "Invalid Email Address", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Invalid Name", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Please enter your details!", Toast.LENGTH_LONG)
                                .show();
                    }
                }

        );


        return result;
    }

    private void toLoginActivity() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.frame_layout, new LoginFragment()).commit();
    }

    private void setUserName(String name) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest.Builder changeRequest =
                    new UserProfileChangeRequest.Builder();
            changeRequest.setDisplayName(name);
            user.updateProfile(changeRequest.build()).addOnCompleteListener(task -> {
                if (NameChangeListener.listener.countObservers() == 0) {
                    if (getActivity() != null) {
                        NavigationView nav = getActivity().findViewById(R.id.navigationView);
                        if (nav != null) {
                            ((TextView) nav.getHeaderView(0).findViewById(R.id.name_info)).setText(name);
                        } else {
                            Log.i("updateProfile", "nav is null");
                        }
                    } else {
                        Log.i("updateProfile", "activity is null");
                    }
                } else {
                    NameChangeListener.listener.setChanged();
                    Log.i("NameChangeListener", "notifyObservers");
                }
            });
        }
    }

    private static void handleRegisterError(Task task, Activity activity) {
        task.addOnFailureListener(activity, fail -> {
                    // Sign in failed.
                    Log.e("task failed", fail.getMessage());
                    Toast.makeText(
                            activity.getApplicationContext(),
                            fail.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
        );
    }

}
