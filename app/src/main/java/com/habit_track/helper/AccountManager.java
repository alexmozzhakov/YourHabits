package com.habit_track.helper;

import android.app.Activity;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.habit_track.activity.LoginActivity;

public class AccountManager {


    public static void logoutUser(Activity activity) {
        // Sign out from account manager
        FirebaseAuth.getInstance().signOut();

        // Launching the login activity
        final Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

}
