package com.doapps.habits.listeners;

import android.net.Uri;
import android.widget.ImageView;

import com.doapps.habits.helper.AvatarManager;
import com.doapps.habits.helper.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.Observable;
import java.util.Observer;

public class ProfileFragmentAvatarListener implements Observer {
    private final ImageView mAvatarImage;

    public ProfileFragmentAvatarListener(final ImageView avatarImage) {
        mAvatarImage = avatarImage;
    }

    @Override
    public void update(final Observable observable, final Object o) {
        final Uri avatarUri = AvatarManager.listener.getUri();
        if (avatarUri.toString().contains("graph")) {
            Picasso.with(mAvatarImage.getContext().getApplicationContext())
                    .load(avatarUri + "?type=large")
                    .transform(new RoundedTransformation())
                    .into(mAvatarImage);
        } else {
            Picasso.with(mAvatarImage.getContext().getApplicationContext())
                    .invalidate(avatarUri);
            Picasso.with(mAvatarImage.getContext().getApplicationContext())
                    .load(avatarUri)
                    .transform(new RoundedTransformation())
                    .into(mAvatarImage);
        }
    }
}
