package com.doapps.habits.slider.swipeselector;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.doapps.habits.R;
import com.doapps.habits.models.OnItemSelectedListener;
import com.doapps.habits.models.StringSelector;


/*
 * SwipeStringSelector library for Android
 * Copyright (c) 2016 Iiro Krankka (http://github.com/roughike).
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
public class SwipeStringSelector extends FrameLayout implements StringSelector {
    private static final int DEFAULT_INDICATOR_SIZE = 6;
    private static final int DEFAULT_INDICATOR_MARGIN = 8;
    private static final String STATE_SELECTOR = "STATE_SELECTOR";

    private SwipeAdapter mAdapter;

    public SwipeStringSelector(final Context context) {
        super(context);
        init(context, null, 0);
    }

    public SwipeStringSelector(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SwipeStringSelector(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(final Context context, final AttributeSet attrs,
                      final int defStyleAttr) {
        final TypedArray ta = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.SwipeStringSelector, defStyleAttr, 0);

        int indicatorSize;
        int indicatorMargin;
        int leftButtonResource;
        int rightButtonResource;

        java.lang.String customFontPath;
        int titleTextAppearance;

        try {
            indicatorSize = (int) ta.getDimension(R.styleable.SwipeStringSelector_swipe_indicatorSize,
                    PixelUtils.dpToPixel(context, DEFAULT_INDICATOR_SIZE));
            indicatorMargin = (int) ta.getDimension(R.styleable.SwipeStringSelector_swipe_indicatorMargin,
                    PixelUtils.dpToPixel(context, DEFAULT_INDICATOR_MARGIN));

            leftButtonResource = ta.getResourceId(R.styleable.SwipeStringSelector_swipe_leftButtonResource,
                    R.drawable.ic_action_navigation_chevron_left);
            rightButtonResource = ta.getResourceId(R.styleable.SwipeStringSelector_swipe_rightButtonResource,
                    R.drawable.ic_action_navigation_chevron_right);

            customFontPath = ta.getString(R.styleable.SwipeStringSelector_swipe_customFontPath);
            titleTextAppearance = ta.getResourceId(R.styleable.SwipeStringSelector_swipe_titleTextAppearance,
                    -1);
        } finally {
            ta.recycle();
        }

        final LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.swipeselector_layout, this);

        final ViewPager pager =
                (ViewPager) findViewById(R.id.swipeselector_layout_swipePager);
        final ImageView leftButton =
                (ImageView) findViewById(R.id.swipeselector_layout_leftButton);
        final ImageView rightButton =
                (ImageView) findViewById(R.id.swipeselector_layout_rightButton);

        mAdapter = new SwipeAdapter.Builder()
                .viewPager(pager)
                .indicatorSize(indicatorSize)
                .indicatorMargin(indicatorMargin)
                .leftButtonResource(leftButtonResource)
                .rightButtonResource(rightButtonResource)
                .leftButton(leftButton)
                .rightButton(rightButton)
                .customFontPath(customFontPath)
                .titleTextAppearance(titleTextAppearance)
                .build();
        pager.setAdapter(mAdapter);
    }

    /**
     * Set a listener to be fired every time a different item is chosen.
     *
     * @param listener the listener that gets fired on item selection
     */
    @Override
    public void setOnItemSelectedListener(final OnItemSelectedListener<String> listener) {
        mAdapter.setOnItemSelectedListener(listener);
    }

    /**
     * A method for giving this SwipeStringSelector something to show.
     *
     * @param strings an array of {@link String} to show
     *                   inside this view.
     */
    @Override
    public void setItems(final String... strings) {
        mAdapter.setItems(strings);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        final Bundle bundle = mAdapter.onSaveInstanceState();
        bundle.putParcelable(STATE_SELECTOR, super.onSaveInstanceState());
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        final Bundle bundle = (Bundle) state;
        mAdapter.onRestoreInstanceState(bundle);
        state = bundle.getParcelable(STATE_SELECTOR);
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void dispatchSaveInstanceState(final SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(final SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    @Override
    public SwipeAdapter getAdapter() {
        return mAdapter;
    }
}
