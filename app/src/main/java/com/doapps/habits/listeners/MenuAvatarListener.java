package com.doapps.habits.listeners;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.widget.ImageView;

import com.doapps.habits.BuildConfig;
import com.doapps.habits.R;
import com.doapps.habits.helper.PicassoRoundedTransformation;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

public class MenuAvatarListener implements LifecycleObserver, Observer<Uri> {
    private final Lifecycle lifecycle;
    private final Context context;
    private final NavigationView navigationView;
    public static final String TAG = MenuAvatarListener.class.getSimpleName();

    public MenuAvatarListener(LifecycleOwner lifecycleOwner, Context context, NavigationView navigationView) {
        this.lifecycle = lifecycleOwner.getLifecycle();
        this.context = context;
        this.navigationView = navigationView;
        this.lifecycle.addObserver(this);
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void cleanup() {
        lifecycle.removeObserver(this);
    }

    @Override
    public void onChanged(@Nullable Uri uri) {
        if (uri != null) {
            ImageView avatar =
                    navigationView.getHeaderView(0).findViewById(R.id.profile_photo);
            if (BuildConfig.DEBUG) {
                Log.i("updateMenuAvatar", String.valueOf(uri));
            }
            Picasso.with(context.getApplicationContext()).invalidate(uri);
            Picasso.with(context.getApplicationContext())
                    .load(uri)
                    .transform(new PicassoRoundedTransformation())
                    .into(avatar);
            avatar.invalidate();
        } else {
            Log.e(TAG, "URI is null");
        }
    }
}
