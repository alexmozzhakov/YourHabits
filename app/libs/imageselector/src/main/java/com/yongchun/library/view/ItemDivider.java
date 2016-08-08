package com.yongchun.library.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yongchun.library.R;
import com.yongchun.library.utils.ScreenUtils;

class ItemDivider extends RecyclerView.ItemDecoration {
    private final Drawable mDrawable;

    ItemDivider(final Context context) {
        mDrawable = ContextCompat.getDrawable(context, R.drawable.item_divider);
    }

    @Override
    public void onDrawOver(final Canvas c, final RecyclerView parent, final RecyclerView.State state) {
        final int left = ScreenUtils.dip2px(parent.getContext(), 16);
        final int right = parent.getWidth() - left;

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDrawable.getIntrinsicHeight();
            mDrawable.setBounds(left, top, right, bottom);
            mDrawable.draw(c);
        }
    }

    @Override
    public void getItemOffsets(final Rect outRect, final View view, final RecyclerView parent,
                               final RecyclerView.State state) {
        outRect.set(0, 0, 0, mDrawable.getIntrinsicWidth());
    }

}