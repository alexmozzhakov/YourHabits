package com.habit_track.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.habit_track.R;

import java.util.regex.Pattern;

public class RegisterActivity extends Activity {

    public static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z \\-\\.']*$");
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText mInputFullName = (EditText) findViewById(R.id.full_name);
        final EditText mInputEmail = (EditText) findViewById(R.id.email);
        final EditText mInputPassword = (EditText) findViewById(R.id.password);
        final Button btnRegister = (Button) findViewById(R.id.btnRegister);
        final Button btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                final Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };

        // Register Button Click event
        btnRegister.setOnClickListener(view -> {
            String name = mInputFullName.getText().toString().trim();
            final String email = mInputEmail.getText().toString().trim();
            final String password = mInputPassword.getText().toString().trim();
            if (name.isEmpty()) {
                name = "Anonymous";
            }
            if (!email.isEmpty() && !password.isEmpty()) {
                if (isValidPattern(name, NAME_PATTERN)) {
                    if (isValidPattern(email, EMAIL_PATTERN)) {
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this, task -> {
                                    if (!task.isSuccessful()) {
                                        // Sign in failed.
                                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Launch login activity
                                        toLoginActivity();
                                    }
                                });
                    } else {
                        Toast.makeText(RegisterActivity.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Invalid Name", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getApplicationContext(),
                        "Please enter your details!", Toast.LENGTH_LONG)
                        .show();
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(view -> {
            toLoginActivity();
        });
    }

    private void toLoginActivity() {
        final Intent intent = new Intent(getApplicationContext(),
                LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public static boolean isValidPattern(final String str, final Pattern pattern) {
        return pattern.matcher(str).matches();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
