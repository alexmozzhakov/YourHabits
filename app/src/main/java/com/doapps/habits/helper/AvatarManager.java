package com.doapps.habits.helper;

import android.net.Uri;
import android.util.Log;

import com.doapps.habits.BuildConfig;

import java.util.Observable;

public class AvatarManager extends Observable {
    private volatile boolean changed;
    private Uri uri;
    public static final AvatarManager listener = new AvatarManager();

    public Uri getUri() {
        return uri;
    }

    public void setUri(final Uri uri) {
        this.uri = uri;
        changed = true;
        if (BuildConfig.DEBUG) {
            Log.i("AvatarManager", "Notifying " + countObservers() + " observers about avatar change");
        }
        notifyObservers();
    }

    @Override
    public synchronized boolean hasChanged() {
        return changed;
    }

}
