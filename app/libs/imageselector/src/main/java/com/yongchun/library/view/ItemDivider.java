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

    ItemDivider(Context context) {
        mDrawable = ContextCompat.getDrawable(context, R.drawable.item_divider);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = ScreenUtils.dip2px(parent.getContext(), 16);
        int right = parent.getWidth() - left;

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDrawable.getIntrinsicHeight();
            mDrawable.setBounds(left, top, right, bottom);
            mDrawable.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.set(0, 0, 0, mDrawable.getIntrinsicWidth());
    }

}