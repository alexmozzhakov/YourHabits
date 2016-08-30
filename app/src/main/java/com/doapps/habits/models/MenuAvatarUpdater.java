package com.doapps.habits.models;

import android.net.Uri;

@FunctionalInterface
public interface MenuAvatarUpdater {
    void updateMenuAvatar(Uri uri);
}
