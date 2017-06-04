package com.doapps.habits.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.doapps.habits.R;
import com.doapps.habits.fragments.RegisterFragment;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordRecoveryActivity extends AppCompatActivity {
    private TextInputEditText emailEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);
        emailEdit = findViewById(R.id.textInputEditText);
    }

    public void recoverPassword(View view) {
        String newEmail = emailEdit.getText().toString().trim();
        if (newEmail.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Email is empty", Toast.LENGTH_SHORT).show();
        } else if (RegisterFragment.isValidPattern(newEmail, RegisterFragment.EMAIL_PATTERN)) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.sendPasswordResetEmail(newEmail);
            Toast.makeText(getApplicationContext(), "Recovery mail sent", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Recovery email isn't valid", Toast.LENGTH_SHORT).show();
        }

        InputMethodManager imm = (InputMethodManager) getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
