package com.doapps.habits.listeners;

import android.net.Uri;
import android.widget.ImageView;

import com.doapps.habits.helper.AvatarManager;
import com.doapps.habits.helper.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.Observable;
import java.util.Observer;

public class ProfileFragmentAvatarListener implements Observer {
    private final ImageView avatarImage;

    public ProfileFragmentAvatarListener(final ImageView avatarImage) {
        this.avatarImage = avatarImage;
    }

    @Override
    public void update(final Observable observable, final Object o) {
        final Uri avatarUri = AvatarManager.listener.getUri();
        Picasso.with(avatarImage.getContext().getApplicationContext())
                .load(avatarUri)
                .transform(new RoundedTransformation())
                .into(avatarImage);
    }
}
