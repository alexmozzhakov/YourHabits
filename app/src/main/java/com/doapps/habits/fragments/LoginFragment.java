package com.doapps.habits.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.doapps.habits.BuildConfig;
import com.doapps.habits.R;
import com.doapps.habits.activity.AuthActivity;
import com.doapps.habits.activity.MainActivity;
import com.doapps.habits.activity.PasswordRecoveryActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Arrays;

public class LoginFragment extends Fragment {

    private static final String TAG = LoginFragment.class.getName();

    private FirebaseAuth mAuth;
    private TextInputEditText inputPassword;
    private TextInputEditText inputEmail;
    private Button btnFacebook;
    private Button btnLogin;
    private Button btnRecovery;
    private Button btnLinkToRegister;
    private Button btnAnonymous;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getContext().getApplicationContext());
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_login, container, false);

        inputEmail = (TextInputEditText) result.findViewById(R.id.email);
        inputPassword = (TextInputEditText) result.findViewById(R.id.password);
        setupFields();

        btnLogin = (Button) result.findViewById(R.id.btn_login);
        btnAnonymous = (Button) result.findViewById(R.id.btn_anonymous_login);
        btnFacebook = (Button) result.findViewById(R.id.btn_login_facebook);
        btnRecovery = (Button) result.findViewById(R.id.btn_recovery);
        btnLinkToRegister = (Button) result.findViewById(R.id.btnLinkToRegisterScreen);

        setupButtons();

        return result;
    }

    private void setupButtons() {

        btnAnonymous.setOnClickListener(this::anonymousLogin);
        mAuth = FirebaseAuth.getInstance();

        // Login button Click Event
        btnLogin.setOnClickListener(view -> {
            final String email = inputEmail != null ? inputEmail.getText().toString().trim() : null;
            final String password = inputPassword != null ? inputPassword.getText().toString().trim() : null;

            // Check for empty data in the form
            checkInput(email, password);
        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(view ->
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.frame_layout, new RegisterFragment()).commit());


        btnRecovery.setOnClickListener(view -> {
            final Intent intent = new Intent(getActivity().getApplicationContext(),
                    PasswordRecoveryActivity.class);
            startActivity(intent);
        });

        final CallbackManager callbackManager = ((AuthActivity) getActivity()).getCallbackManager();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult result) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "facebook:onSuccess:" + result);
                        }
                        handleFacebookAccessToken(result.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                    }

                    @Override
                    public void onError(final FacebookException error) {
                        Log.d(TAG, "facebook:onError", error);
                    }

                    private void handleFacebookAccessToken(final AccessToken token) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "handleFacebookAccessToken:" + token);
                        }

                        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
                        mAuth.signInWithCredential(credential)
                                .addOnCompleteListener(task -> {
                                    if (BuildConfig.DEBUG) {
                                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                                    }

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (task.isSuccessful()) {
                                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                        if (user != null) {
                                            final String userID = token.getUserId();

                                            final UserProfileChangeRequest profileUpdates =
                                                    new UserProfileChangeRequest.Builder()
                                                            .setPhotoUri(Uri.parse(String.format("https://graph.facebook.com/%s/picture?type=large", userID)))
                                                            .build();

                                            user.updateProfile(profileUpdates)
                                                    .addOnCompleteListener(update -> {
                                                        if (update.isSuccessful()) {
                                                            Log.d(TAG, "User photo set.");
                                                        }
                                                    });
                                        }
                                    } else {
                                        Log.w(TAG, "signInWithCredential", task.getException());
                                        Toast.makeText(getContext(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
        btnFacebook.setOnClickListener(view ->
                LoginManager.getInstance().
                        logInWithReadPermissions(getActivity(),
                                Arrays.asList("email", "public_profile")));
    }


    private void checkInput(final String email, final String password) {
        // Check for empty data in the form
        if (email.isEmpty() || password.isEmpty()) {
            // Prompt user to enter credentials
            Toast.makeText(getContext().getApplicationContext(),
                    "Please enter the credentials!", Toast.LENGTH_LONG)
                    .show();
        }
        // Check for valid email
        else if (!RegisterFragment.isValidPattern(email, RegisterFragment.EMAIL_PATTERN)) {
            // Prompt user to enter valid credentials
            Toast.makeText(getContext().getApplicationContext(),
                    "Please enter valid credentials!", Toast.LENGTH_LONG)
                    .show();
        } else {
            // login user
            final FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText
                            (getActivity().getApplicationContext(),
                                    "Email or password isn't correct",
                                    Toast.LENGTH_SHORT).show();

                    getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE)
                            .edit().putString("last email", email).apply();
                }
            });
        }
    }

    private void setupFields() {
        final Typeface face =
                Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(),
                        "Montserrat-Regular.ttf");

        inputEmail.setTypeface(face);
        inputPassword.setTypeface(face);
        inputPassword.setImeActionLabel("Login", KeyEvent.KEYCODE_ENTER);
        inputPassword.setOnKeyListener((final View view, final int keyCode, final KeyEvent event) -> {
            // If the event is a key-down event on the "enter" button
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();

                checkInput(email, password);

                return true;
            }
            return false;
        });
    }

    private void anonymousLogin(final View view) {
        FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(task -> {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
            }

            // If sign in fails, display a message to the user. If sign in succeeds
            // the auth state listener will be notified and logic to handle the
            // signed in user can be handled in the listener.
            if (task.isSuccessful()) {
                if (getActivity() != null) {
                    final Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Log.i("FA", "login page no longer exists");
                }
            } else {
                Log.w(TAG, "signInAnonymously", task.getException());
                Toast.makeText(getContext().getApplicationContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }

            // ...
        });
    }
}
