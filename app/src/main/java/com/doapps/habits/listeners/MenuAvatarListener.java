package com.doapps.habits.listeners;

import android.net.Uri;

import com.doapps.habits.helper.AvatarManager;
import com.doapps.habits.models.MenuAvatarUpdater;

import java.util.Observable;
import java.util.Observer;


public class MenuAvatarListener implements Observer {

    private final MenuAvatarUpdater mMainActivity;

    public MenuAvatarListener(final MenuAvatarUpdater mainActivity) {
        mMainActivity = mainActivity;
    }

    @SuppressWarnings("FeatureEnvy")
    @Override
    public void update(final Observable observable, final Object o) {
        if (AvatarManager.listener.getUri().toString().contains("graph")) {
            mMainActivity.updateMenuAvatar(
                    Uri.parse(AvatarManager.listener.getUri() + "?type=medium"));
        } else {
            mMainActivity.updateMenuAvatar(AvatarManager.listener.getUri());
        }
    }
}
