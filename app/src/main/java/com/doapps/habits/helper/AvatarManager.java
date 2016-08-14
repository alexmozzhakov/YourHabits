package com.doapps.habits.helper;

import android.net.Uri;
import android.util.Log;

import com.doapps.habits.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Observable;

public class AvatarManager extends Observable {
    private static final String TAG = AvatarManager.class.getSimpleName();
    private volatile boolean changed;
    private Uri mUri = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
    public static final AvatarManager listener = new AvatarManager();

    public void invalidateUrl() {
        mUri = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
    }

    public Uri getUri() {
        return mUri;
    }

    public void setUri(final Uri uri) {
        mUri = uri;
        changed = true;
        if (BuildConfig.DEBUG) {
            Log.i("AvatarManager", "Notifying " + countObservers() + " observers about avatar change");
            Log.i(TAG, "New uri is " + uri);
        }
        notifyObservers();
    }

    @Override
    public synchronized boolean hasChanged() {
        return changed;
    }

}
