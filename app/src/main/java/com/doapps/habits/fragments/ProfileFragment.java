package com.doapps.habits.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.doapps.habits.BuildConfig;
import com.doapps.habits.R;
import com.doapps.habits.activity.MainActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class ProfileFragment extends Fragment {
    static boolean[] editorOpened;
    private String inputPassword = "";
    private static final String TAG = ProfileFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View result = inflater.inflate(R.layout.fragment_profile, container, false);
        editorOpened = new boolean[]{false};
        final TextView name = (TextView) result.findViewById(R.id.name);
        final TextView email = (TextView) result.findViewById(R.id.email);
        final Button btnDelete = (Button) result.findViewById(R.id.btn_delete_user);
//        final ImageView imageView = (ImageView) result.findViewById(R.id.profile_photo);
        final FloatingActionButton fab = (FloatingActionButton) result.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if (!editorOpened[0]) {
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.userInfo, new ProfileEditFragment())
                        .commit();
                editorOpened[0] = true;
            }
        });


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            if (user.getPhotoUrl() != null && !user.getPhotoUrl().toString().contains("facebook")) {
                final Button btnFacebook = (Button) result.findViewById(R.id.btn_connect_facebook);
                btnFacebook.setVisibility(View.VISIBLE);

                FacebookSdk.sdkInitialize(getContext().getApplicationContext());
                final CallbackManager callbackManager =
                        ((MainActivity) getActivity()).getCallbackManager();
                LoginManager.getInstance().registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(final LoginResult result) {
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "facebook:onSuccess:" + result);
                                }
                                final AuthCredential credential =
                                        FacebookAuthProvider.getCredential(result.getAccessToken().getToken());
                                user.linkWithCredential(credential)
                                        .addOnCompleteListener(task -> {
                                            Log.d("FA", "linkWithCredential:onComplete:" + task.isSuccessful());
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext().getApplicationContext(),
                                                        "Successfully connected with Facebook",
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
                                Log.d(TAG, "facebook:onCancel");
                                // ...
                            }

                            @Override
                            public void onError(final FacebookException error) {
                                Log.d(TAG, "facebook:onError", error);
                            }
                        });
                // set up facebook btn
                btnFacebook.setOnClickListener(view ->
                        LoginManager.getInstance().
                                logInWithReadPermissions(getActivity(),
                                        Arrays.asList("email", "public_profile")));
            }


            btnDelete.setOnClickListener(view -> reauthorise(user));
            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
//            getChildFragmentManager()
//                    .beginTransaction()
//                    .add(R.id.userInfo, new EditPhotoFragment())
//                    .commit();
        }

        return result;
    }

    private void reauthorise(final FirebaseUser user) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Please re-enter your password");

        // Set up the input
        final EditText input = new EditText(getActivity());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            inputPassword = input.getText().toString();
            if (!inputPassword.isEmpty() && user.getEmail() != null) {
                final AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), inputPassword);
                user.reauthenticate(credential).addOnCompleteListener(task ->
                        user.delete().addOnCompleteListener(t -> {
                            if (task.isSuccessful()) {
                                Log.d("FA", "User account deleted.");
                            } else {
                                Toast.makeText(getContext().getApplicationContext(), "Password isn't correct",
                                        Toast.LENGTH_SHORT);
                            }
                        }));
            } else if (inputPassword.isEmpty()) {
                Toast.makeText(getContext().getApplicationContext(), "Password was empty",
                        Toast.LENGTH_SHORT);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }
}
