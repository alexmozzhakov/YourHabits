package com.habit_track.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.habit_track.R;

public class LoginActivity extends AppCompatActivity {
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
        if (inputEmail != null && inputPassword != null) {
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

        final Button btnLogin = (Button) findViewById(R.id.btn_login);
        final Button btnRecovery = (Button) findViewById(R.id.btn_recovery);
        final Button btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            final FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                final Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };

        // Login button Click Event
        if (btnLogin != null) {
            btnLogin.setOnClickListener(view -> {
                final String email = inputEmail != null ? inputEmail.getText().toString().trim() : null;
                final String password = inputPassword != null ? inputPassword.getText().toString().trim() : null;

                // Check for empty data in the form
                checkInput(email, password);
            });
        }

        // Link to Register Screen
        if (btnLinkToRegister != null) {
            btnLinkToRegister.setOnClickListener(view -> {
                final Intent intent = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (btnRecovery != null) {
            btnRecovery.setOnClickListener(view -> {
                final Intent intent = new Intent(getApplicationContext(),
                        PasswordRecoveryActivity.class);
                startActivity(intent);
                finish();
            });
        }

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
