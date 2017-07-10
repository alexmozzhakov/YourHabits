package com.doapps.habits.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
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
import com.doapps.habits.data.AvatarData;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Arrays;

public class LoginFragment extends LifecycleFragment {

  /**
   * TAG is defined for logging errors and debugging information
   */
  private static final String TAG = LoginFragment.class.getSimpleName();

  private FirebaseAuth mAuth;
  private TextInputEditText inputPassword;
  private TextInputEditText inputEmail;
  private Button btnFacebook;
  private Button btnLogin;
  private Button btnRecovery;
  private Button btnLinkToRegister;
  private Button btnSkip;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View result = inflater.inflate(R.layout.fragment_login, container, false);

    inputEmail = result.findViewById(R.id.email);
    inputPassword = result.findViewById(R.id.password);
    setupFields();

    btnLogin = result.findViewById(R.id.btn_login);
    btnSkip = result.findViewById(R.id.btn_skip);
    btnFacebook = result.findViewById(R.id.btn_login_facebook);
    btnRecovery = result.findViewById(R.id.btn_recovery);
    btnLinkToRegister = result.findViewById(R.id.btnLinkToRegisterScreen);

    setupButtons();

    return result;
  }

  private void setupButtons() {
    btnSkip.setOnClickListener(this::anonymousLogin);
    mAuth = FirebaseAuth.getInstance();

    // Login button Click Event
    btnLogin.setOnClickListener(view -> {
      String email = inputEmail != null ? inputEmail.getText().toString().trim() : null;
      String password = inputPassword != null ? inputPassword.getText().toString().trim() : null;

      // Check for empty data in the form
      checkInput(email, password);
    });

    // Link to Register Screen
    btnLinkToRegister.setOnClickListener(view ->
        getActivity().getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.frame_layout, new RegisterFragment()).commit());

    btnRecovery.setOnClickListener(view -> {
      Intent intent = new Intent(getActivity().getApplicationContext(),
          PasswordRecoveryActivity.class);
      startActivity(intent);
    });

    CallbackManager callbackManager = ((AuthActivity) getActivity()).getCallbackManager();

    LoginManager.getInstance().registerCallback(callbackManager,
        new FacebookCallback<LoginResult>() {
          @Override
          public void onSuccess(LoginResult result) {
            handleFacebookAccessToken(result.getAccessToken());
          }

          @Override
          public void onCancel() {
            Log.d(TAG, "facebook:onCancel");
          }

          @Override
          public void onError(FacebookException error) {
            Log.d(TAG, "facebook:onError", error);
          }

          private void handleFacebookAccessToken(AccessToken token) {
            AuthCredential credential =
                FacebookAuthProvider.getCredential(token.getToken());
            mAuth.signInWithCredential(credential)
                .addOnFailureListener(e -> {
                  Log.w(TAG, "signInWithCredential", e);
                  Toast.makeText(getContext(), "Authentication failed.",
                      Toast.LENGTH_SHORT).show();
                })
                .addOnCompleteListener(task -> {
                  FirebaseUser user =
                      FirebaseAuth.getInstance().getCurrentUser();
                  if (user != null && getContext().getSharedPreferences("pref",
                      Context.MODE_PRIVATE).getString(user.getUid(), null) == null) {
                    getContext()
                        .getSharedPreferences("pref", Context.MODE_PRIVATE)
                        .edit()
                        .putString(user.getUid(), token.getUserId())
                        .apply();

                    AvatarData.getInstance().setValue(
                        Uri.parse(String.format("https://graph.facebook.com/%s/picture?type=large",
                            token.getUserId())));
                  }
                });
          }

        });
    btnFacebook.setOnClickListener(view ->
        LoginManager.getInstance().
            logInWithReadPermissions(getActivity(),
                Arrays.asList("email", "public_profile")));
  }

  private void checkInput(String email, String password) {
    // Check for empty data in the form
    if (email.isEmpty() || password.isEmpty()) {
      // Prompt user to enter credentials
      Toast.makeText(getContext().getApplicationContext(),
          "Please enter the credentials!", Toast.LENGTH_LONG)
          .show();
    }
    // Check for valid email
    else if (!RegisterFragment.isValidEmail(email)) {
      // Prompt user to enter valid credentials
      Toast.makeText(getContext().getApplicationContext(),
          "Please enter valid credentials!", Toast.LENGTH_LONG)
          .show();
    } else {
      // login user
      FirebaseAuth auth = FirebaseAuth.getInstance();
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
    inputPassword.setImeActionLabel("Login", KeyEvent.KEYCODE_ENTER);
    inputPassword.setOnKeyListener((final View view, final int keyCode, final KeyEvent event) -> {
      // If the event is a key-down event on the "enter" button
      if (keyCode == KeyEvent.KEYCODE_ENTER) {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        checkInput(email, password);

        return true;
      }
      return false;
    });
  }

  @SuppressWarnings("unused")
  private void anonymousLogin(View view) {
    FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(task -> {
      if (BuildConfig.DEBUG) {
        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
      }

      if (getActivity() != null) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
      } else {
        Log.i("FA", "Login page no longer exists");
      }
    });

  }
}
