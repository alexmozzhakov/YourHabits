package com.doapps.habits.helper;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.bumptech.glide.request.target.DrawableImageViewTarget;

/**
 * @see DrawableImageViewTarget
 */
public class DrawableViewBackgroundTarget extends ViewBackgroundTarget<Drawable> {
    public DrawableViewBackgroundTarget(View view) {
        super(view);
    }

    @Override
    protected void setResource(Drawable resource) {
        setBackground(resource);
    }
}
