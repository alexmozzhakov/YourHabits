package com.habit_track.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.habit_track.R;

import static com.habit_track.activity.RegisterActivity.EMAIL_PATTERN;
import static com.habit_track.activity.RegisterActivity.isValidPattern;

public class PasswordRecoveryActivity extends AppCompatActivity {

    private TextInputEditText emailEdit;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);
        emailEdit = (TextInputEditText) findViewById(R.id.textInputEditText);

    }

    public void recoverPassword(final View view) {
        final String newEmail = emailEdit.getText().toString().trim();
        if (newEmail.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Email is empty", Toast.LENGTH_SHORT).show();
        } else if (isValidPattern(newEmail, EMAIL_PATTERN)) {
            final FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.sendPasswordResetEmail(newEmail);
            Toast.makeText(getApplicationContext(), "Recovery mail sent", Toast.LENGTH_SHORT).show();
            final Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Recovery email isn't valid", Toast.LENGTH_SHORT).show();
        }
    }
}