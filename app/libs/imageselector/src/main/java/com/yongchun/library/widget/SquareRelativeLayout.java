package com.yongchun.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class SquareRelativeLayout extends RelativeLayout {

    public SquareRelativeLayout(final Context context) {
        super(context);
    }

    public SquareRelativeLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareRelativeLayout(final Context context, final AttributeSet attrs,
                                final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        // Set a square layout.
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

}
