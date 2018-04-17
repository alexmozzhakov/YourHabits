package com.doapps.habits.slider.swipeselector;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.doapps.habits.R;
import com.doapps.habits.models.IOnItemSelectedListener;
import com.doapps.habits.models.IStringSelector;


/**
 * The string selector with swipe selection.
 */
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
public class SwipeStringSelector extends FrameLayout implements IStringSelector {

  private static final String STATE_SELECTOR = "STATE_SELECTOR";

  private SwipeAdapter mAdapter;

  /**
   * Instantiates a new swipe string selector.
   *
   * @param context the context
   */
  public SwipeStringSelector(Context context) {
    super(context);
    init(context, null, 0);
  }

  /**
   * Instantiates a new swipe string selector.
   *
   * @param context the context
   * @param attrs the attributes
   */
  public SwipeStringSelector(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs, 0);
  }

  /**
   * Instantiates a new swipe string selector.
   *
   * @param context the context
   * @param attrs the attributes
   * @param defStyleAttr the default style attribute
   */
  public SwipeStringSelector(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs,
      int defStyleAttr) {
    TypedArray ta = context.getTheme().obtainStyledAttributes(attrs,
        R.styleable.SwipeStringSelector, defStyleAttr, 0);

    int startButtonResource;
    int endButtonResource;

    int titleTextAppearance;

    try {
      startButtonResource = ta
          .getResourceId(R.styleable.SwipeStringSelector_swipe_leftButtonResource,
              R.drawable.ic_action_navigation_chevron_left);
      endButtonResource = ta
          .getResourceId(R.styleable.SwipeStringSelector_swipe_rightButtonResource,
              R.drawable.ic_action_navigation_chevron_right);

      titleTextAppearance = ta
          .getResourceId(R.styleable.SwipeStringSelector_swipe_titleTextAppearance,
              -1);
    } finally {
      ta.recycle();
    }

    LayoutInflater inflater = LayoutInflater.from(context);
    inflater.inflate(R.layout.swipeselector_layout, this);

    ViewPager pager = findViewById(R.id.swipe_selector_layout_swipePager);
    ImageView leftButton = findViewById(R.id.swipe_selector_start_button);
    ImageView rightButton = findViewById(R.id.swipe_selector_layout_end_button);

    Configuration config = context.getResources().getConfiguration();
    if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1
        && config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
      //RTL
      mAdapter = new SwipeAdapter(pager,
          startButtonResource, endButtonResource,
          rightButton, leftButton,
          titleTextAppearance);
    } else {
      mAdapter = new SwipeAdapter(pager,
          startButtonResource, endButtonResource,
          leftButton, rightButton,
          titleTextAppearance);

    }

    pager.setAdapter(mAdapter);
  }

  /**
   * Set a listener to be fired every time a different item is chosen.
   *
   * @param listener the listener that gets fired on item selection
   */
  @Override
  public void setOnItemSelectedListener(@NonNull IOnItemSelectedListener<? super String> listener) {
    mAdapter.setOnItemSelectedListener(listener);
  }

  /**
   * A method for giving this SwipeStringSelector something to show.
   *
   * @param strings an array of {@link String} to show inside this view.
   */
  @Override
  public void setItems(@NonNull String... strings) {
    mAdapter.setItems(strings);
  }

  @Override
  public Parcelable onSaveInstanceState() {
    Bundle bundle = mAdapter.onSaveInstanceState();
    bundle.putParcelable(STATE_SELECTOR, super.onSaveInstanceState());
    return bundle;
  }

  @Override
  public void onRestoreInstanceState(Parcelable state) {
    Bundle bundle = (Bundle) state;
    mAdapter.onRestoreInstanceState(bundle);
    state = bundle.getParcelable(STATE_SELECTOR);
    super.onRestoreInstanceState(state);
  }

  @Override
  protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
    dispatchFreezeSelfOnly(container);
  }

  @Override
  protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
    dispatchThawSelfOnly(container);
  }

  @NonNull
  @Override
  public SwipeAdapter getAdapter() {
    return mAdapter;
  }
}
