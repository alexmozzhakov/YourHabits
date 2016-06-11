package com.habit_track.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.habit_track.R;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    public static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z \\-\\.']*$");
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static boolean isValidPattern(final String str, final Pattern pattern) {
        return pattern.matcher(str).matches();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText inputFullName = (EditText) findViewById(R.id.full_name);
        final EditText inputEmail = (EditText) findViewById(R.id.email);
        final EditText inputPassword = (EditText) findViewById(R.id.password);
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
            final String name = inputFullName.getText().toString().trim();
            final String email = inputEmail.getText().toString().trim();
            final String password = inputPassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty() && !name.isEmpty()) {
                if (isValidPattern(name, NAME_PATTERN)) {
                    if (isValidPattern(email, EMAIL_PATTERN)) {
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this, task -> {
                                    if (!task.isSuccessful()) {
                                        // Sign in failed.
                                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        setUserName(name);
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
        btnLinkToLogin.setOnClickListener(view -> toLoginActivity());
    }

    private void toLoginActivity() {
        final Intent intent = new Intent(getApplicationContext(),
                LoginActivity.class);
        startActivity(intent);
        finish();
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
