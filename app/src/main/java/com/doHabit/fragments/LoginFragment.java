package com.dohabit.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.dohabit.R;
import com.dohabit.activity.MainActivity;
import com.dohabit.activity.PasswordRecoveryActivity;

public class LoginFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextInputEditText inputPassword;
    private TextInputEditText inputEmail;
    private Button btnLogin;
    private Button btnRecovery;
    private Button btnLinkToRegister;
    private Button btnAnonymous;

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_login, container, false);

        inputEmail = (TextInputEditText) result.findViewById(R.id.email);
        inputPassword = (TextInputEditText) result.findViewById(R.id.password);
        setupFields();

        btnLogin = (Button) result.findViewById(R.id.btn_login);
        btnAnonymous = (Button) result.findViewById(R.id.btn_anonymous_login);
        btnRecovery = (Button) result.findViewById(R.id.btn_recovery);
        btnLinkToRegister = (Button) result.findViewById(R.id.btnLinkToRegisterScreen);
        setupButtons();

        return result;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void setupButtons() {

        btnAnonymous.setOnClickListener(LoginFragment::anonymousLogin);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                // User is signed in
                final Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        };

        // Login button Click Event
        btnLogin.setOnClickListener(view -> {
            final String email = inputEmail != null ? inputEmail.getText().toString().trim() : null;
            final String password = inputPassword != null ? inputPassword.getText().toString().trim() : null;

            // Check for empty data in the form
            checkInput(email, password);
        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(view ->
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, new RegisterFragment()).commit());


        btnRecovery.setOnClickListener(view -> {
            final Intent intent = new Intent(getActivity().getApplicationContext(),
                    PasswordRecoveryActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
    }

    private void checkInput(final String email, final String password) {
        // Check for empty data in the form
        if (email.isEmpty() || password.isEmpty()) {
            // Prompt user to enter credentials
            Toast.makeText(getContext().getApplicationContext(),
                    "Please enter the credentials!", Toast.LENGTH_LONG)
                    .show();
        }
        // Check for valid email
        else if (!RegisterFragment.isValidPattern(email, RegisterFragment.EMAIL_PATTERN)) {
            // Prompt user to enter valid credentials
            Toast.makeText(getContext().getApplicationContext(),
                    "Please enter valid credentials!", Toast.LENGTH_LONG)
                    .show();
        } else {
            // login user
            final FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText
                            (getActivity().getApplicationContext(),
                                    "Email or password isn't correct",
                                    Toast.LENGTH_SHORT).show();

                    getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE)
                            .edit().putString("last email", email).apply();
                }
            });
        }
    }

    private void setupFields() {
        final Typeface face =
                Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(),
                        "Montserrat-Regular.ttf");

        inputEmail.setTypeface(face);
        inputPassword.setTypeface(face);
        inputPassword.setImeActionLabel("Login", KeyEvent.KEYCODE_ENTER);
        inputPassword.setOnKeyListener((final View view, final int keyCode, final KeyEvent event) -> {
            // If the event is a key-down event on the "enter" button
            if (event.getAction() == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER) {
                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();

                checkInput(email, password);

                return true;
            }
            return false;
        });
    }

    private static void anonymousLogin(final View view) {
        FirebaseAuth.getInstance().signInAnonymously();
    }

}
