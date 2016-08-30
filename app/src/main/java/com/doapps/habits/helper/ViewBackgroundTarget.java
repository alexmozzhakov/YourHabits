package com.doapps.habits.helper;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.ViewTarget;

/**
 * @see ImageViewTarget
 */
abstract class ViewBackgroundTarget<Z> extends ViewTarget<View, Z> implements GlideAnimation.ViewAdapter {
    ViewBackgroundTarget(View view) {
        super(view);
    }

    @Override
    public void onLoadCleared(Drawable placeholder) {
        setBackground(placeholder);
    }

    @Override
    public void onLoadStarted(Drawable placeholder) {
        setBackground(placeholder);
    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        setBackground(errorDrawable);
    }

    @Override
    public void onResourceReady(Z resource, GlideAnimation<? super Z> glideAnimation) {
        if (glideAnimation == null || !glideAnimation.animate(resource, this)) {
            setResource(resource);
        }
    }

    @Override
    public void setDrawable(Drawable drawable) {
        setBackground(drawable);
    }

    @Override
    public Drawable getCurrentDrawable() {
        return view.getBackground();
    }

    void setBackground(Drawable drawable) {
        view.setBackground(drawable);
    }

    abstract void setResource(Z resource);
}
