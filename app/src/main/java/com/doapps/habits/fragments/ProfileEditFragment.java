package com.doapps.habits.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.doapps.habits.BuildConfig;
import com.doapps.habits.R;
import com.doapps.habits.activity.EditPhotoActivity;
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
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;

@SuppressWarnings("FeatureEnvy")
public class ProfileEditFragment extends Fragment {

    private String inputPassword = "";
    private static final String TAG = ProfileEditFragment.class.getSimpleName();
    private FloatingActionButton fab;
    private ImageView avatar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        avatar = (ImageView) getActivity().findViewById(R.id.avatarImage);
        setUpPhotoEdition(avatar, getActivity(), this);

        fab.setImageResource(R.drawable.ic_check_white_24dp);
        fab.setOnClickListener(view -> {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user != null && user.getEmail() != null) {
                        String email =
                                String.valueOf(((TextView) result.findViewById(R.id.edit_email)).getText()).toLowerCase();
                        String name =
                                String.valueOf(((TextView) result.findViewById(R.id.edit_name)).getText());

                        if (!isUpdated(user, email, name)) { // same info
                            getFragmentManager().beginTransaction().remove(this).commit();
                            Log.i("EditProfile", "Nothing to update");
                            return;
                        }
                        if (MainActivity.isFacebook(user)) {
                            // TODO: 14/07/2016 change to working solution
                            CallbackManager callbackManager =
                                    ((MainActivity) getActivity()).getCallbackManager();
                            FacebookSdk.sdkInitialize(getContext().getApplicationContext());
                            LoginManager.getInstance().registerCallback(callbackManager,
                                    new FacebookCallback<LoginResult>() {
                                        @Override
                                        public void onSuccess(LoginResult result) {
                                            if (BuildConfig.DEBUG) {
                                                Log.d(TAG, "facebook:onSuccess:" + result);
                                            }
                                            AuthCredential credential =
                                                    FacebookAuthProvider.getCredential(result.getAccessToken().getToken());
                                            user.reauthenticate(credential)
                                                    .addOnCompleteListener(task -> Log.d(TAG, "User re-authenticated."));
                                            updateUser(getActivity(), user, name, email);
                                        }

                                        @Override
                                        public void onCancel() {
                                            Log.d(TAG, "facebook:onCancel");
                                            // ...
                                        }

                                        @Override
                                        public void onError(FacebookException error) {
                                            Log.d(TAG, "facebook:onError", error);
                                        }
                                    });

                            getFragmentManager().beginTransaction().remove(this).commit();
                        } else {
                            getPasswordFromUser();
                            // Prompt the user to re-provide their sign-in credentials
                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(user.getEmail(), inputPassword);
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(task -> Log.d(TAG, "User re-authenticated."));
                            if (inputPassword.isEmpty()) { // entered password empty
                                Toast.makeText(getContext().getApplicationContext(),
                                        "Please enter correct password", Toast.LENGTH_SHORT)
                                        .show();
                                getPasswordFromUser();
                                return;
                            }
                        }

                        updateUser(getActivity(), user, name, email);
                        getFragmentManager().beginTransaction().remove(this).commit();
                    } else {
                        Log.e(TAG, "User is null");
                    }
                }

        );


        return result;
    }

    private static boolean isUpdated(UserInfo user, String email, String name) {
        boolean nameUpdated = !name.isEmpty() && !name.equals(user.getDisplayName());
        boolean emailUpdated = !email.isEmpty() && !email.equals(user.getEmail());
        return nameUpdated || emailUpdated;
    }

    private void getPasswordFromUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Please re-enter your password");

        // Set up the input
        EditText input = new EditText(getActivity());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> inputPassword = input.getText().toString());
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            getFragmentManager().beginTransaction().remove(this).commit();
        });

        builder.show();
    }

    private static void updateUser(Activity activity, FirebaseUser user,
                                   String name, String email) {
        TextView nameView =
                (TextView) activity.findViewById(R.id.name);
        TextView emailView =
                (TextView) activity.findViewById(R.id.email);

        if (!name.isEmpty() && !name.equals(user.getDisplayName())) {
            UserProfileChangeRequest profileUpdates =
                    new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            NavigationView navigationView =
                                    (NavigationView) activity.findViewById(R.id.navigationView);

                            ((TextView) navigationView
                                    .getHeaderView(0).findViewById(R.id.name_info))
                                    .setText(user.getDisplayName());

                            nameView.setText(user.getDisplayName());

                            Log.d(TAG, "User profile updated.");
                        }
                    });
        }
        if (!email.isEmpty() && !email.equals(user.getEmail())) {
            user.updateEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            NavigationView navigationView =
                                    (NavigationView) activity.findViewById(R.id.navigationView);

                            ((TextView) navigationView
                                    .getHeaderView(0).findViewById(R.id.email_info))
                                    .setText(user.getEmail());

                            emailView.setText(user.getEmail());

                            Log.d(TAG, "User email address updated.");
                        }
                    });
        }

    }

    private static void setUpPhotoEdition(ImageView avatar, Activity activity, Fragment fragment) {
        avatar.setImageResource(R.drawable.ic_photo_plus);
        if (avatar.getBackground() != null) {
            avatar.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        }
        avatar.setOnClickListener(view -> {
            fragment.getParentFragment().getChildFragmentManager().beginTransaction()
                    .remove(fragment).commit();
            Intent intent = new Intent(activity, EditPhotoActivity.class);
            activity.startActivity(intent);
        });
    }


    private static void removePhotoEdition(ImageView avatar) {
        avatar.setImageResource(R.drawable.fix);
        if (avatar.getBackground() != null) {
            avatar.getBackground().clearColorFilter();
        }
        avatar.setOnClickListener(null);
    }

    @Override
    public void onPause() {
        removePhotoEdition(avatar);
        // Set up fab back
        fab.setImageResource(R.drawable.ic_edit_white_24dp);
        fab.setOnClickListener(v -> {
            if (!ProfileFragment.getEditorOpened()[0]) {
                getParentFragment().getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.userInfo, new ProfileEditFragment())
                        .commit();
                ProfileFragment.getEditorOpened()[0] = true;
            }
        });

        // Sets editors state to close
        ProfileFragment.getEditorOpened()[0] = false;
        // Close keyboard
        ((MainActivity) getActivity()).closeImm();
        super.onPause();
    }
}
