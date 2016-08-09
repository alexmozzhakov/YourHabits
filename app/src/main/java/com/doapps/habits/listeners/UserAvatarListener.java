package com.doapps.habits.listeners;

import android.util.Log;

import com.doapps.habits.helper.AvatarManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Observable;
import java.util.Observer;

public class UserAvatarListener implements Observer {
    @Override
    public void update(final Observable observable, final Object o) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final UserProfileChangeRequest changeRequest =
                    new UserProfileChangeRequest.Builder()
                            .setPhotoUri(AvatarManager.listener.getUri()).build();
            user.updateProfile(changeRequest)
                    .addOnFailureListener(e -> Log.e("fail", e.getMessage()));
        }
    }
}
