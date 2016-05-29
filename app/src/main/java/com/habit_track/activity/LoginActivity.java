package com.habit_track.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.habit_track.R;

public class LoginActivity extends Activity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final TextInputEditText inputEmail = (TextInputEditText) findViewById(R.id.email);
        final TextInputEditText inputPassword = (TextInputEditText) findViewById(R.id.password);
        final Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf");
        inputEmail.setTypeface(face);
        inputPassword.setTypeface(face);

        final Button btnLogin = (Button) findViewById(R.id.btnLogin);
        final Button btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        inputPassword.setImeActionLabel("Login", KeyEvent.KEYCODE_ENTER);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                final Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };

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

        // Login button Click Event
        btnLogin.setOnClickListener(view -> {
            final String email = inputEmail.getText().toString().trim();
            final String password = inputPassword.getText().toString().trim();

            // Check for empty data in the form
            checkInput(email, password);
        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(view -> {
            final Intent i = new Intent(getApplicationContext(),
                    RegisterActivity.class);
            startActivity(i);
            finish();
        });

    }

    /**
     * function to verify login details in mysql db
     */

    private void checkInput(final String email, final String password) {
        // Check for empty data in the form
        if (email.isEmpty() || password.isEmpty()) {
            // Prompt user to enter credentials
            Toast.makeText(getApplicationContext(),
                    "Please enter the credentials!", Toast.LENGTH_LONG)
                    .show();
        }
        // Check for valid email
        else if (!RegisterActivity.isValidPattern(email, RegisterActivity.EMAIL_PATTERN)) {
            // Prompt user to enter valid credentials
            Toast.makeText(getApplicationContext(),
                    "Please enter valid credentials!", Toast.LENGTH_LONG)
                    .show();
        } else {
            // login user
            final FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(email, password);
        }
    }
}
