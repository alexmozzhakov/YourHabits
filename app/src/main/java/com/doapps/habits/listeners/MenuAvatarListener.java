package com.doapps.habits.listeners;

import com.doapps.habits.helper.AvatarManager;
import com.doapps.habits.models.IMenuAvatarUpdater;

import java.util.Observable;
import java.util.Observer;


public class MenuAvatarListener implements Observer {

    private final IMenuAvatarUpdater mMainActivity;

    public MenuAvatarListener(IMenuAvatarUpdater mainActivity) {
        mMainActivity = mainActivity;
    }

    @Override
    public void update(Observable observable, Object o) {
        mMainActivity.updateMenuAvatar(AvatarManager.listener.getMediumUri());
    }
}
