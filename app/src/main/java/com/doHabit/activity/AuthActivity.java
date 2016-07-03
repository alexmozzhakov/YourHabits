package com.dohabit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dohabit.R;
import com.dohabit.fragments.LoginFragment;

public class AuthActivity extends AppCompatActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_layout, new LoginFragment())
                .commit();
    }
}
