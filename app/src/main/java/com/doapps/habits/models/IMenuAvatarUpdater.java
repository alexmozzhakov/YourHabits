package com.doapps.habits.models;

import android.net.Uri;

@FunctionalInterface
public interface IMenuAvatarUpdater {
    void updateMenuAvatar(Uri uri);
}
