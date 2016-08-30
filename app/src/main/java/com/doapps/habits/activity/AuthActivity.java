package com.doapps.habits.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.doapps.habits.R;
import com.doapps.habits.fragments.LoginFragment;
import com.doapps.habits.slider.swipeselector.PixelUtils;
import com.facebook.CallbackManager;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        onConfigurationChanged(getResources().getConfiguration());

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null && !user.isAnonymous()) {
                // User is signed in
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        };

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .add(R.id.frame_layout, new LoginFragment())
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            findViewById(R.id.top_image_text).setVisibility(View.VISIBLE);
            FrameLayout layout = (FrameLayout) findViewById(R.id.frame_layout);
            FrameLayout.LayoutParams params =
                    (FrameLayout.LayoutParams) layout.getLayoutParams();
            params.topMargin = (int) PixelUtils.dpToPixel(getApplicationContext(), 170);
            layout.setLayoutParams(params);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            findViewById(R.id.top_image_text).setVisibility(View.INVISIBLE);
            FrameLayout layout = (FrameLayout) findViewById(R.id.frame_layout);
            FrameLayout.LayoutParams params =
                    (FrameLayout.LayoutParams) layout.getLayoutParams();
            params.topMargin = 0;
            layout.setLayoutParams(params);
        }
    }

    public void toTerms(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://habbitsapp.esy.es/terms.txt")));
    }
}
