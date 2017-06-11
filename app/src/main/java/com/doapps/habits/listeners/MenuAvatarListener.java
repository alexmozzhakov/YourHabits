package com.doapps.habits.listeners;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.widget.ImageView;

import com.doapps.habits.BuildConfig;
import com.doapps.habits.R;
import com.doapps.habits.helper.PicassoRoundedTransformation;
import com.doapps.habits.slider.swipeselector.PixelUtils;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

public class MenuAvatarListener implements LifecycleObserver, Observer<Uri> {
    public static final String TAG = MenuAvatarListener.class.getSimpleName();
    private final Lifecycle lifecycle;
    private final Context context;
    private final NavigationView navigationView;

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
            int padding = (int) PixelUtils.dpToPixel(context, 68);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                navigationView.getHeaderView(0).findViewById(R.id.fields_info).setPaddingRelative(padding, 0, 0, 0);
            } else {
                navigationView.getHeaderView(0).findViewById(R.id.fields_info).setPadding(padding, 0, 0, 0);
            }
        } else {
            Log.e(TAG, "URI is null");
        }
    }
}
