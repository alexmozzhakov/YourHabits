package com.dohabit.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.dohabit.R;
import com.dohabit.fragments.RegisterFragment;
import com.google.firebase.auth.FirebaseAuth;

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
        } else if (RegisterFragment.isValidPattern(newEmail, RegisterFragment.EMAIL_PATTERN)) {
            final FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.sendPasswordResetEmail(newEmail);
            Toast.makeText(getApplicationContext(), "Recovery mail sent", Toast.LENGTH_SHORT).show();
            final Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Recovery email isn't valid", Toast.LENGTH_SHORT).show();
        }

        final InputMethodManager imm = (InputMethodManager) getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
