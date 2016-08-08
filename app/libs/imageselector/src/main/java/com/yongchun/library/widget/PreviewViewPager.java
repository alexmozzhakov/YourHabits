package com.yongchun.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class PreviewViewPager extends android.support.v4.view.ViewPager {

    public PreviewViewPager(final Context context) {
        super(context);
    }

    public PreviewViewPager(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (final IllegalArgumentException ex) {
            Log.e("PreviewViewPager", ex.getMessage());
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (final IllegalArgumentException ex) {
            Log.e("PreviewViewPager", ex.getMessage());
        }
        return false;
    }
}
