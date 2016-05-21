package com.habit_track.helper;

import android.support.annotation.ColorRes;
import android.support.v7.widget.RecyclerView;
import android.view.ViewTreeObserver;

/**
 * This is a utility class that monitors a {@link RecyclerView} for changes and temporarily
 * gives the view a background so we do not see any artifacts while items are animated in or
 * out of the view, and, at the same time prevent the overdraw that would occur when we'd
 * give the {@link RecyclerView} a permanent opaque background color.
 * <p>
 * Created by Thomas Keller <me@thomaskeller.biz> on 12.05.16.
 */
public class RecyclerBackgroundSaver {

    private RecyclerView mRecyclerView;
    @ColorRes
    private int mBackgroundColor;

    private boolean mAdapterChanged = false;
    RecyclerView.AdapterDataObserver mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mAdapterChanged = true;
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mAdapterChanged = true;
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mAdapterChanged = true;
        }
    };
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener
            = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            // ignore layout changes until something actually changed in the adapter
            if (!mAdapterChanged) {
                return;
            }
            mRecyclerView.setBackgroundResource(mBackgroundColor);

            // if no animation is running (which should actually only be the case if
            // we change the adapter without animating anything, like complete data set changes),
            // do not do anything either
            if (!mRecyclerView.getItemAnimator().isRunning()) {
                return;
            }

            // remove this view tree observer, i.e. do not react on further layout changes for
            // one and the same data set change and give control to the ItemAnimatorFinishedListener
            mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            mRecyclerView.getItemAnimator().isRunning(finishListener);
        }
    };
    RecyclerView.ItemAnimator.ItemAnimatorFinishedListener finishListener
            = new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener() {
        @Override
        public void onAnimationsFinished() {
            // the animation ended, reset the adapter changed flag so the next change kicks off
            // the cycle again and add the layout change listener back
            mRecyclerView.setBackgroundResource(0);
            mAdapterChanged = false;
            mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
        }
    };


    public RecyclerBackgroundSaver(RecyclerView recyclerView, @ColorRes int backgroundColor) {
        mRecyclerView = recyclerView;
        mBackgroundColor = backgroundColor;
    }

    /**
     * Enables the background saver, i.e for the next item change, the RecyclerView's background
     * will be temporarily set to the configured background color.
     */
    public void enable() {
        mRecyclerView.getAdapter().registerAdapterDataObserver(mAdapterDataObserver);
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
    }

    /**
     * Disables the background saver, i.e. for the next animation,
     * the RecyclerView's parent background will again shine through.
     */
    public void disable() {
        mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
        if (mRecyclerView.getAdapter() != null) {
            mRecyclerView.getAdapter().unregisterAdapterDataObserver(mAdapterDataObserver);
        }
    }
}