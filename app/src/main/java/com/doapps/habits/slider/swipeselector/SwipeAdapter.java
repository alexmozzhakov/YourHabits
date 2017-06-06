package com.doapps.habits.slider.swipeselector;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.doapps.habits.R;
import com.doapps.habits.models.IOnItemSelectedListener;

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

    private final int mTitleTextAppearance;

    private final ImageView mLeftButton;
    private final ImageView mRightButton;

    private final int mSweetSixteen;
    private final int mContentLeftPadding;
    private final int mContentRightPadding;

    private IOnItemSelectedListener<String> mOnItemSelectedListener;
    private String[] mItems;

    private int mCurrentPosition;

    SwipeAdapter(ViewPager viewPager, int leftButtonResource, int rightButtonResource,
                 ImageView leftButton, ImageView rightButton,
                 int titleTextAppearance) {
        mContext = viewPager.getContext();

        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(this);

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

        mLeftButton.setAlpha(0f);
    }

    /**
     * methods used by SwipeStringSelector
     */
    void setOnItemSelectedListener(IOnItemSelectedListener<String> listener) {
        mOnItemSelectedListener = listener;
    }

    void setItems(String... items) {
        mItems = items;
        mCurrentPosition = 0;
        setActiveIndicator(0);
        notifyDataSetChanged();
    }

    private String getSelectedItem() {
        return mItems[mCurrentPosition];
    }

    Bundle onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putInt(STATE_CURRENT_POSITION, mCurrentPosition);
        return bundle;
    }

    void onRestoreInstanceState(Bundle state) {
        mViewPager.setCurrentItem(state.getInt(STATE_CURRENT_POSITION), false);
        notifyDataSetChanged();
    }

    /**
     * Override methods / listeners
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TextView layout =
                (TextView) View.inflate(mContext, R.layout.swipeselector_content_item, null);

        layout.setText(mItems[position]);

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
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mItems != null ? mItems.length : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void onPageSelected(int position) {
        if (getCount() == 0) {
            return;
        }
        setActiveIndicator(position);

        handleLeftButtonVisibility(position);
        handleRightButtonVisibility(position);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(mLeftButton) && mCurrentPosition >= 1) {
            mViewPager.setCurrentItem(mCurrentPosition - 1, true);
        } else if (view.equals(mRightButton) && mCurrentPosition <= getCount() - 1) {
            mViewPager.setCurrentItem(mCurrentPosition + 1, true);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // ignored
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // ignored
    }

    /**
     * Private convenience methods used by this class.
     */
    private void setActiveIndicator(int position) {
        mCurrentPosition = position;

        if (mOnItemSelectedListener != null) {
            mOnItemSelectedListener.onItemSelected(getSelectedItem());
        }
    }

    @SuppressWarnings("deprecation")
    private static void setTextAppearanceCompat(TextView textView, int appearanceRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(appearanceRes);
        } else {
            textView.setTextAppearance(textView.getContext(), appearanceRes);
        }
    }

    private void handleLeftButtonVisibility(int position) {
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

    private void handleRightButtonVisibility(int position) {
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

    private static void animate(float alpha, ImageView button) {
        button.animate()
                .alpha(alpha)
                .setDuration(120)
                .start();
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }
}
