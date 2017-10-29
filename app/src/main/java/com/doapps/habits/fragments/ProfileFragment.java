package com.doapps.habits.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.doapps.habits.BuildConfig;
import com.doapps.habits.R;
import com.doapps.habits.activity.EditPhotoActivity;
import com.doapps.habits.activity.MainActivity;
import com.doapps.habits.data.AvatarData;
import com.doapps.habits.listeners.ProfileAvatarListener;
import com.doapps.habits.models.UserRemoveCompleteListener;
import com.doapps.habits.slider.swipeselector.PixelUtils;
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

public class ProfileFragment extends Fragment {

  private static final boolean[] editorOpened = new boolean[1];
  private static final String TAG = ProfileFragment.class.getSimpleName();
  public static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

  static boolean[] getEditorOpened() {
    return editorOpened;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    getActivity().findViewById(R.id.toolbar_shadow).setVisibility(View.GONE);
    // Inflate the layout for this fragment
    View result = inflater.inflate(R.layout.fragment_profile, container, false);
    Toolbar toolbar = ((MainActivity) getActivity()).getToolbar();
    toolbar.setTitle(R.string.profile);
    TextView name = result.findViewById(R.id.name);
    TextView email = result.findViewById(R.id.email);
    TextView location = result.findViewById(R.id.location);
    Button btnDelete = result.findViewById(R.id.btn_delete_user);
    FloatingActionButton fab = result.findViewById(R.id.fab);
    ImageView plus = result.findViewById(R.id.plus_overlay);

    fab.setOnClickListener(view -> {
      getChildFragmentManager()
          .beginTransaction()
          .replace(R.id.user_info, new ProfileEditFragment())
          .commit();
      editorOpened[0] = true;
    });

    ImageView avatar = result.findViewById(R.id.avatarImage);

    if (user != null) {
      location.setText(getActivity()
          .getSharedPreferences("pref", Context.MODE_PRIVATE)
          .getString("location", ""));

      if (!AvatarData.INSTANCE.hasAvatar(getContext().getApplicationContext())) {
        Log.w(TAG, "no avatar");
        plus.setVisibility(View.VISIBLE);
        plus.setOnClickListener(view -> {
          Intent intent = new Intent(getActivity(), EditPhotoActivity.class);
          startActivity(intent);
        });
      } else {
        AvatarData.INSTANCE.getAvatar(getContext().getApplicationContext(), avatar);
      }

      if (MainActivity.Companion.isFacebook(user) || user.getPhotoUrl() != null) {
        View topPanel = result.findViewById(R.id.topPanel);
        topPanel.addOnLayoutChangeListener(
            (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
              if (topPanel.getHeight() - PixelUtils.dpToPixel(getContext(), 50) < 200) {
                avatar.setImageAlpha(0);
                plus.setImageAlpha(0);
                name.setGravity(Gravity.CENTER);
                location.setGravity(Gravity.CENTER);
                Log.i("Top Panel", "I really can't fit on top panel, the view is only " +
                    (topPanel.getHeight() - PixelUtils.dpToPixel(getContext(), 50)));
              } else {
                avatar.setImageAlpha(255);
                plus.setImageAlpha(255);
                name.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                location.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                Log.i("Top Panel", "I fit on top panel");
              }
            });
      }
      if (!MainActivity.Companion.isFacebook(user)) {
        Button btnFacebook = result.findViewById(R.id.btn_connect_facebook);
        btnFacebook.setVisibility(View.VISIBLE);
        CallbackManager callbackManager =
            ((MainActivity) getActivity()).getCallbackManager();
        LoginManager.getInstance().registerCallback(callbackManager,
            new FacebookCallback<LoginResult>() {
              @Override
              public void onSuccess(LoginResult result) {
                if (BuildConfig.DEBUG) {
                  Log.d(TAG, "facebook:onSuccess:" + result);
                }
                AuthCredential credential =
                    FacebookAuthProvider.getCredential(result.getAccessToken().getToken());
                user.linkWithCredential(credential)
                    .addOnCompleteListener(task -> {
                      if (BuildConfig.DEBUG) {
                        Log.d("FA", "linkWithCredential:onComplete:" + task.isSuccessful());
                      }

                      if (task.isSuccessful()) {
                        Toast.makeText(getContext().getApplicationContext(),
                            task.getResult() + " Successfully connected with Facebook",
                            Toast.LENGTH_SHORT).show();
                      } else {
                        Toast.makeText(getContext().getApplicationContext(),
                            "Authentication failed", Toast.LENGTH_SHORT).show();
                      }
                    });
              }

              @Override
              public void onCancel() {
                Log.d(TAG, "Facebook login canceled");
              }

              @Override
              public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
              }
            });
        // set up facebook btn
        btnFacebook.setOnClickListener(view ->
            LoginManager.getInstance().
                logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile")));
      }

      name.setText(user.getDisplayName());
      email.setText(user.getEmail());

      btnDelete.setOnClickListener(view -> user.delete()
          .addOnCompleteListener(new UserRemoveCompleteListener(getActivity())));
      AvatarData.INSTANCE.observe(this,
          new ProfileAvatarListener(getContext(), avatar, getActivity(), this, plus));
    }

    return result;
  }
}