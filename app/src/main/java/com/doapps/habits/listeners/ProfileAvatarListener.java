package com.doapps.habits.listeners;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.doapps.habits.activity.EditPhotoActivity;
import com.doapps.habits.helper.PicassoRoundedTransformation;
import com.squareup.picasso.Picasso;


public class ProfileAvatarListener implements LifecycleObserver, Observer<Uri> {
    public static final String TAG = ProfileAvatarListener.class.getSimpleName();
    private final Context context;
    private final ImageView avatar;
    private final Activity activity;
    private final Lifecycle lifecycle;

    public ProfileAvatarListener(Context context, ImageView avatar, Activity activity,
                                 LifecycleOwner lifecycleOwner) {
        this.context = context;
        this.avatar = avatar;
        this.activity = activity;
        this.lifecycle = lifecycleOwner.getLifecycle();
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void cleanup() {
        lifecycle.removeObserver(this);
    }

    @Override
    public void onChanged(@Nullable Uri uri) {
        if (uri != null) {
            Log.i(TAG, "Avatar update detected");
            Picasso.with(context.getApplicationContext())
                    .invalidate(uri);
            Picasso.with(context.getApplicationContext())
                    .load(uri)
                    .transform(new PicassoRoundedTransformation())
                    .fit().centerInside()
                    .into(avatar);
            avatar.invalidate();
        } else {
            Log.w(TAG, "no avatar");
            avatar.setOnClickListener(view -> {
                Intent intent = new Intent(activity, EditPhotoActivity.class);
                activity.startActivity(intent);
            });
        }

    }
}
