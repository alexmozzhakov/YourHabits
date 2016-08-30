package com.doapps.habits.helper;

import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

/**
 * @see GlideDrawableImageViewTarget
 */
public class GlideDrawableViewBackgroundTarget extends ViewBackgroundTarget<GlideDrawable> {
    private final int maxLoopCount;
    private GlideDrawable resource;

    public GlideDrawableViewBackgroundTarget(ImageView view) {
        this(view, GlideDrawable.LOOP_FOREVER);
    }

    public GlideDrawableViewBackgroundTarget(ImageView view, int maxLoopCount) {
        super(view);
        this.maxLoopCount = maxLoopCount;
    }

    @Override
    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
        super.onResourceReady(resource, glideAnimation);
        this.resource = resource;
        resource.setLoopCount(maxLoopCount);
        resource.start();
    }

    @Override
    protected void setResource(GlideDrawable resource) {
        setBackground(resource);
    }

    @Override
    public void onStart() {
        if (resource != null) {
            resource.start();
        }
    }

    @Override
    public void onStop() {
        if (resource != null) {
            resource.stop();
        }
    }
}
