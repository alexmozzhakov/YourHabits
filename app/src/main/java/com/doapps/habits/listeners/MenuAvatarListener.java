package com.doapps.habits.listeners;

import com.doapps.habits.helper.AvatarManager;
import com.doapps.habits.models.MenuAvatarUpdater;

import java.util.Observable;
import java.util.Observer;


public class MenuAvatarListener implements Observer {

    private final MenuAvatarUpdater mainActivity;

    public MenuAvatarListener(final MenuAvatarUpdater mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    public void update(final Observable observable, final Object o) {
        mainActivity.updateMenuAvatar(AvatarManager.listener.getUri());

    }
}
