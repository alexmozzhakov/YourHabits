package com.doapps.habits.listeners;

import com.doapps.habits.helper.AvatarManager;
import com.doapps.habits.models.MenuAvatarUpdater;

import java.util.Observable;
import java.util.Observer;


public class MenuAvatarListener implements Observer {

    private final MenuAvatarUpdater mMainActivity;

    public MenuAvatarListener(MenuAvatarUpdater mainActivity) {
        mMainActivity = mainActivity;
    }

    @Override
    public void update(Observable observable, Object o) {
        mMainActivity.updateMenuAvatar(AvatarManager.listener.getMediumUri());
    }
}
