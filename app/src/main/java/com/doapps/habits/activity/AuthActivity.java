package com.doapps.habits.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.doapps.habits.R;
import com.doapps.habits.fragments.LoginFragment;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity {

    private final CallbackManager mCallbackManager = CallbackManager.Factory.create();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    public CallbackManager getCallbackManager() {
        return mCallbackManager;
    }


    public FirebaseAuth getAuth() {
        return mAuth;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            final FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null && !user.isAnonymous()) {
                // User is signed in
                final Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_layout, new LoginFragment())
                .commit();
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        if (FacebookSdk.isInitialized()) {
            FacebookSdk.clearLoggingBehaviors();
        }
    }
}
