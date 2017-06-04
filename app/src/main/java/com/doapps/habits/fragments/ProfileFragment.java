package com.doapps.habits.fragments;

import android.app.AlertDialog;
import android.arch.lifecycle.LifecycleFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.doapps.habits.BuildConfig;
import com.doapps.habits.R;
import com.doapps.habits.activity.EditPhotoActivity;
import com.doapps.habits.activity.MainActivity;
import com.doapps.habits.data.AvatarData;
import com.doapps.habits.helper.PicassoRoundedTransformation;
import com.doapps.habits.listeners.ProfileAvatarListener;
import com.doapps.habits.slider.swipeselector.PixelUtils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

public class ProfileFragment extends LifecycleFragment {
    private static final boolean[] editorOpened = new boolean[1];
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private static int mDialogTextLength;
    private static final int DIALOG_MIN_LENGTH = 6;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().findViewById(R.id.toolbar_shadow).setVisibility(View.GONE);
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView name = result.findViewById(R.id.name);
        TextView email = result.findViewById(R.id.email);
        TextView location = result.findViewById(R.id.location);
        Button btnDelete = result.findViewById(R.id.btn_delete_user);
        FloatingActionButton fab = result.findViewById(R.id.fab);

        fab.setOnClickListener(view -> {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.userInfo, new ProfileEditFragment())
                    .commit();
            editorOpened[0] = true;
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ImageView avatar = result.findViewById(R.id.avatarImage);

        if (user != null) {
            location.setText(getActivity()
                    .getSharedPreferences("pref", Context.MODE_PRIVATE)
                    .getString("location", ""));


            Uri avatarUri = AvatarData.getInstance().getValue();
            if (avatarUri != null) {
                Log.i(TAG, String.valueOf(avatarUri));
                Picasso.with(getContext().getApplicationContext())
                        .load(avatarUri)
                        .transform(new PicassoRoundedTransformation())
                        .fit().centerInside()
                        .into(avatar);
            } else {
                Log.w(TAG, "no avatar");
                avatar.setOnClickListener(view -> {
                    Intent intent = new Intent(getActivity(), EditPhotoActivity.class);
                    startActivity(intent);
                });
            }

            if (MainActivity.isFacebook(user)) {
                View topPanel = result.findViewById(R.id.topPanel);
                topPanel.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                    if (topPanel.getHeight() - PixelUtils.dpToPixel(getContext(), 50) < 200) {
                        avatar.setImageAlpha(0);
                        name.setGravity(Gravity.CENTER);
                        location.setGravity(Gravity.CENTER);
                        Log.i("Top Panel", "I really can't fit on top panel, the view is only " +
                                (topPanel.getHeight() - PixelUtils.dpToPixel(getContext(), 50)));
                    } else {
                        avatar.setImageAlpha(255);
                        name.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                        location.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                        Log.i("Top Panel", "I fit on top panel");
                    }
                });
            } else {
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

                                            getActivity()
                                                    .getSharedPreferences("pref", Context.MODE_PRIVATE)
                                                    .edit()
                                                    .putString(user.getUid(), result.getAccessToken().getUserId())
                                                    .apply();

                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext().getApplicationContext(),
                                                        task.getResult() +
                                                                " Successfully connected with Facebook",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getContext().getApplicationContext(),
                                                        "Authentication failed.",
                                                        Toast.LENGTH_SHORT).show();
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
                                logInWithReadPermissions(getActivity(),
                                        Arrays.asList("email", "public_profile")));
            }

            name.setText(user.getDisplayName());
            email.setText(user.getEmail());

            btnDelete.setOnClickListener(view -> deleteUser(user));
            AvatarData.getInstance().observe(this,
                    new ProfileAvatarListener(getContext(), avatar, getActivity(), this));
        }

        return result;
    }

    private void deleteUser(FirebaseUser user) {
        if (MainActivity.isFacebook(user)) {
            LoginManager.getInstance().
                    logInWithReadPermissions(getActivity(),
                            Arrays.asList("email", "public_profile"));
            CallbackManager callbackManager =
                    ((MainActivity) getActivity()).getCallbackManager();
            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult result) {
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "facebook:onSuccess:" + result);
                            }
                            AuthCredential credential = FacebookAuthProvider.getCredential(
                                    result.getAccessToken().getToken());
                            user.reauthenticate(credential).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    user.delete().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            getActivity()
                                                    .getSharedPreferences("pref", 0).edit()
                                                    .remove(user.getUid()).apply();
                                            Intent intent = new Intent(getActivity(),
                                                    MainActivity.class);
                                            startActivity(intent);
                                            Log.i("FA", "user deleted");
                                        } else {
                                            Log.e("FA", "error deleting user");
                                        }
                                    });
                                } else if (BuildConfig.DEBUG) {
                                    Log.i("FA", "error reauthorizing user");
                                }
                            });
                        }

                        @Override
                        public void onCancel() {
                            Log.d(TAG, "facebook:onCancel");
                            // ignored
                        }

                        @Override
                        public void onError(FacebookException error) {
                            Log.d(TAG, "facebook:onError", error);
                        }
                    });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Please re-enter your password");
            EditText input = new EditText(getContext());

            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", (dialog, which) -> {
                String inputPassword = input.getText().toString();
                if (!inputPassword.isEmpty() && user.getEmail() != null) {
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(user.getEmail(), inputPassword);
                    user.reauthenticate(credential).addOnCompleteListener(task ->
                            user.delete().addOnCompleteListener(t -> {
                                if (task.isSuccessful()) {
                                    Log.d("FA", "User account deleted.");
                                } else {
                                    Toast.makeText(getContext().getApplicationContext(), "Password isn't correct",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }));
                } else if (inputPassword.isEmpty()) {
                    Toast.makeText(getContext().getApplicationContext(), "Password was empty",
                            Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            AlertDialog dialog = builder.create();
            dialog.show();
            input.setOnKeyListener((view, i, keyEvent) -> {
                mDialogTextLength++;
                if (mDialogTextLength > DIALOG_MIN_LENGTH) {
                    dialog.getButton(1).setEnabled(true);
                }
                return false;
            });
        }
    }

    static boolean[] getEditorOpened() {
        return editorOpened;
    }
}