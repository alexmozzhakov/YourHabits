package com.dohabit.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.dohabit.R;
import com.dohabit.activity.MainActivity;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    public static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z \\-\\.']*$");
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static boolean isValidPattern(String str, Pattern pattern) {
        return pattern.matcher(str).matches();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_register, container, false);
        EditText inputFullName = (EditText) result.findViewById(R.id.full_name);
        EditText inputEmail = (EditText) result.findViewById(R.id.email);
        EditText inputPassword = (EditText) result.findViewById(R.id.password);
        Button btnRegister = (Button) result.findViewById(R.id.btnRegister);
        Button btnLinkToLogin = (Button) result.findViewById(R.id.btnLinkToLoginScreen);

        btnLinkToLogin.setOnClickListener(view -> toLoginActivity());
        inputEmail.setText(
                getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE)
                        .getString("last email", ""));

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
                                    if (!task.isSuccessful()) {
                                        // Sign in failed.
                                        Toast.makeText(getContext(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        setUserName(name);
                                        toLoginActivity();
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
        });


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        };
        return result;
    }

    private void toLoginActivity() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, new LoginFragment()).commit();
    }

    private void setUserName(String name) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest.Builder changeRequest =
                    new UserProfileChangeRequest.Builder();
            changeRequest.setDisplayName(name);
            user.updateProfile(changeRequest.build());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
