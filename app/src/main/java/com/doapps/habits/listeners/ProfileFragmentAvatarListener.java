package com.doapps.habits.listeners;

import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.doapps.habits.BuildConfig;
import com.doapps.habits.R;
import com.doapps.habits.fragments.ProfileFragment;
import com.doapps.habits.helper.AvatarManager;

import java.util.Observable;
import java.util.Observer;

public class ProfileFragmentAvatarListener implements Observer {
    private final FragmentManager mFragmentManager;

    public ProfileFragmentAvatarListener(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

    @Override
    public void update(Observable observable, Object o) {
        if (BuildConfig.DEBUG) {
            Log.i("update", String.valueOf(AvatarManager.listener.getLargeUri()));
        }

        mFragmentManager.beginTransaction()
                .replace(R.id.content_frame, new ProfileFragment())
                .commit();
    }
}
