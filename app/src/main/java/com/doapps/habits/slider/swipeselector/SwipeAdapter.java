package com.doapps.habits.slider.swipeselector;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doapps.habits.R;
import com.doapps.habits.models.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public class SwipeAdapter extends PagerAdapter implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private static final String STATE_CURRENT_POSITION = "STATE_CURRENT_POSITION";

    // For the left and right buttons when they're not visible
    private static final String TAG_HIDDEN = "TAG_HIDDEN";

    private final Context mContext;

    private final ViewPager mViewPager;

    private Typeface mCustomTypeFace;
    private final int mTitleTextAppearance;

    private final ImageView mLeftButton;
    private final ImageView mRightButton;

    private final int mSweetSixteen;
    private final int mContentLeftPadding;
    private final int mContentRightPadding;

    private OnItemSelectedListener<String> mOnItemSelectedListener;
    private List<String> mItems;

    private int mCurrentPosition;

    private SwipeAdapter(final ViewPager viewPager, final int indicatorSize, final int indicatorMargin,
                         final int leftButtonResource, final int rightButtonResource,
                         final ImageView leftButton, final ImageView rightButton,
                         final String customFontPath, final int titleTextAppearance) {
        mContext = viewPager.getContext();

        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(this);

        final LinearLayout.LayoutParams mCircleParams =
                new LinearLayout.LayoutParams(indicatorSize, indicatorSize);
        mCircleParams.leftMargin = indicatorMargin;

        if (customFontPath != null &&
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && !customFontPath.isEmpty()
                        || !customFontPath.isEmpty())) {
            mCustomTypeFace = Typeface.createFromAsset(mContext.getAssets(),
                    customFontPath);
        }

        mTitleTextAppearance = titleTextAppearance;

        mLeftButton = leftButton;
        mLeftButton.setImageResource(leftButtonResource);

        mRightButton = rightButton;
        mRightButton.setImageResource(rightButtonResource);

        // Calculate padding for the content so the left and right buttons don't overlap.
        mSweetSixteen = (int) PixelUtils.dpToPixel(mContext, 16);
        mContentLeftPadding = ContextCompat.getDrawable(mContext, leftButtonResource)
                .getIntrinsicWidth() + mSweetSixteen;
        mContentRightPadding = ContextCompat.getDrawable(mContext, rightButtonResource)
                .getIntrinsicWidth() + mSweetSixteen;

        mLeftButton.setOnClickListener(this);
        mRightButton.setOnClickListener(this);

        mLeftButton.setTag(TAG_HIDDEN);
        mLeftButton.setClickable(false);

        mLeftButton.setAlpha(0.0f);
    }

    /**
     * Using the Java Builder Pattern here, because the SwipeStringSelector class was getting
     * messy and that's where most will look at. This class is protected, and contains no
     * methods that the users can use, so it's OK for this to look like absolute vomit.
     * <p/>
     * At least that's my opinion. But my opinions are always right.
     */
    static class Builder {

        private ViewPager viewPager;
        private int indicatorSize;

        private int indicatorMargin;
        private int leftButtonResource;

        private int rightButtonResource;
        private ImageView leftButton;

        private ImageView rightButton;
        private String customFontPath;

        private int titleTextAppearance;

        SwipeAdapter.Builder viewPager(final ViewPager viewPager) {
            this.viewPager = viewPager;
            return this;
        }

        SwipeAdapter.Builder indicatorSize(final int indicatorSize) {
            this.indicatorSize = indicatorSize;
            return this;
        }

        SwipeAdapter.Builder indicatorMargin(final int indicatorMargin) {
            this.indicatorMargin = indicatorMargin;
            return this;
        }

        SwipeAdapter.Builder leftButtonResource(final int leftButtonResource) {
            this.leftButtonResource = leftButtonResource;
            return this;
        }

        SwipeAdapter.Builder rightButtonResource(final int rightButtonResource) {
            this.rightButtonResource = rightButtonResource;
            return this;
        }

        SwipeAdapter.Builder leftButton(final ImageView leftButton) {
            this.leftButton = leftButton;
            return this;
        }

        SwipeAdapter.Builder rightButton(final ImageView rightButton) {
            this.rightButton = rightButton;
            return this;
        }

        SwipeAdapter.Builder customFontPath(final String customFontPath) {
            this.customFontPath = customFontPath;
            return this;
        }

        SwipeAdapter.Builder titleTextAppearance(final int titleTextAppearance) {
            this.titleTextAppearance = titleTextAppearance;
            return this;
        }

        SwipeAdapter build() {
            return new SwipeAdapter(viewPager,
                    indicatorSize,
                    indicatorMargin,
                    leftButtonResource,
                    rightButtonResource,
                    leftButton,
                    rightButton,
                    customFontPath,
                    titleTextAppearance
            );
        }

    }
    /**
     * Protected methods used by SwipeStringSelector
     */
    void setOnItemSelectedListener(final OnItemSelectedListener<String> listener) {
        mOnItemSelectedListener = listener;
    }

    void setItems(final String... items) {
        mItems = new ArrayList<>(Arrays.asList(items));
        mCurrentPosition = 0;
        setActiveIndicator(0);
        notifyDataSetChanged();
    }

    private String getSelectedItem() {
        return mItems.get(mCurrentPosition);
    }

    Bundle onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putInt(STATE_CURRENT_POSITION, mCurrentPosition);
        return bundle;
    }

    void onRestoreInstanceState(final Bundle state) {
        mViewPager.setCurrentItem(state.getInt(STATE_CURRENT_POSITION), false);
        notifyDataSetChanged();
    }

    /**
     * Override methods / listeners
     */
    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final TextView layout =
                (TextView) View.inflate(mContext, R.layout.swipeselector_content_item, null);

        layout.setText(mItems.get(position));

        // We shouldn't get here if the typeface didn't exist.
        // But just in case, because we're paranoid.
        if (mCustomTypeFace != null) {
            layout.setTypeface(mCustomTypeFace);
        }

        if (mTitleTextAppearance != -1) {
            setTextAppearanceCompat(layout, mTitleTextAppearance);
        }

        layout.setPadding(mContentLeftPadding,
                mSweetSixteen,
                mContentRightPadding,
                mSweetSixteen);

        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mItems != null ? mItems.size() : 0;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    @Override
    public void onPageSelected(final int position) {
        if (getCount() == 0) {
            return;
        }
        setActiveIndicator(position);

        handleLeftButtonVisibility(position);
        handleRightButtonVisibility(position);
    }

    @Override
    public void onClick(final View view) {
        if (view.equals(mLeftButton) && mCurrentPosition >= 1) {
            mViewPager.setCurrentItem(mCurrentPosition - 1, true);
        } else if (view.equals(mRightButton) && mCurrentPosition <= getCount() - 1) {
            mViewPager.setCurrentItem(mCurrentPosition + 1, true);
        }
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(final int state) {

    }

    /**
     * Private convenience methods used by this class.
     */
    private void setActiveIndicator(final int position) {
        mCurrentPosition = position;

        if (mOnItemSelectedListener != null) {
            mOnItemSelectedListener.onItemSelected(getSelectedItem());
        }
    }

    private static void setTextAppearanceCompat(final TextView textView, final int appearanceRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(appearanceRes);
        } else {
            textView.setTextAppearance(textView.getContext(), appearanceRes);
        }
    }

    private void handleLeftButtonVisibility(final int position) {
        if (position < 1) {
            mLeftButton.setTag(TAG_HIDDEN);
            mLeftButton.setClickable(false);
            animate(0, mLeftButton);
        } else if (TAG_HIDDEN.equals(mLeftButton.getTag())) {
            mLeftButton.setTag(null);
            mLeftButton.setClickable(true);
            animate(1, mLeftButton);
        }
    }

    private void handleRightButtonVisibility(final int position) {
        if (position == getCount() - 1) {
            mRightButton.setTag(TAG_HIDDEN);
            mRightButton.setClickable(false);
            animate(0, mRightButton);
        } else if (TAG_HIDDEN.equals(mRightButton.getTag())) {
            mRightButton.setTag(null);
            mRightButton.setClickable(true);
            animate(1, mRightButton);
        }
    }

    private static void animate(final float alpha, final ImageView button) {
        button.animate()
                .alpha(alpha)
                .setDuration(120)
                .start();
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }
}
