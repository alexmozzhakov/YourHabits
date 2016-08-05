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
import com.doapps.habits.helper.NameChangeListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    public static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z \\-\\.']*$");
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    public static boolean isValidPattern(final CharSequence sequence, final Pattern pattern) {
        return pattern.matcher(sequence).matches();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_register, container, false);
        final EditText inputFullName = (EditText) result.findViewById(R.id.full_name);
        final EditText inputEmail = (EditText) result.findViewById(R.id.email);
        final EditText inputPassword = (EditText) result.findViewById(R.id.password);
        final Button btnRegister = (Button) result.findViewById(R.id.btnRegister);
        final Button btnLinkToLogin = (Button) result.findViewById(R.id.btnLinkToLoginScreen);

        btnLinkToLogin.setOnClickListener(view -> toLoginActivity());
        inputEmail.setText(
                getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE)
                        .getString("last email", ""));

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // Register Button Click event
        btnRegister.setOnClickListener(view -> {
                    final String name = inputFullName.getText().toString().trim();
                    final String email = inputEmail.getText().toString().trim();
                    final String password = inputPassword.getText().toString().trim();

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

    private void setUserName(final String name) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final UserProfileChangeRequest.Builder changeRequest =
                    new UserProfileChangeRequest.Builder();
            changeRequest.setDisplayName(name);
            user.updateProfile(changeRequest.build()).addOnCompleteListener(task -> {
                if (NameChangeListener.listener.countObservers() == 0) {
                    if (getActivity() != null) {
                        final NavigationView nav = (NavigationView)
                                getActivity().findViewById(R.id.navigationView);
                        if (nav != null) {
                            ((TextView) nav.getHeaderView(0).findViewById(R.id.name_info)).setText(name);
                        } else {
                            Log.i("updateProfile", "nav is null");
                        }
                    } else {
                        Log.i("updateProfile", "activity is null");
                    }
                } else {
                    NameChangeListener.listener.setChanged(true);
                    Log.i("NameChangeListener", "notifyObservers");
                }
            });
        }
    }

    private static void handleRegisterError(final Task task, final Activity activity) {
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
