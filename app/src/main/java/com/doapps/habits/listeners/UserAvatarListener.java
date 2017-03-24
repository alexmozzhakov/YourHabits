package com.doapps.habits.listeners;

import android.util.Log;

import com.doapps.habits.helper.AvatarManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Observable;
import java.util.Observer;

public class UserAvatarListener implements Observer {
    /**
     * TAG is defined for logging errors and debugging information
     */
    private static final String TAG = UserAvatarListener.class.getSimpleName();

    @Override
    public void update(Observable observable, Object o) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && !AvatarManager.listener.getUri().equals(user.getPhotoUrl())) {
            UserProfileChangeRequest changeRequest =
                    new UserProfileChangeRequest.Builder()
                            .setPhotoUri(AvatarManager.listener.getUri()).build();
            user.updateProfile(changeRequest)
                    .addOnFailureListener(e -> Log.e("fail", e.getMessage()));
        } else {
            Log.i(TAG, "URL hasn't changed");
        }
    }
}
