package com.habit_track.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.habit_track.R;

public class TimeLineView extends View {

    private Drawable mMarker;
    private Drawable mStartLine;
    private Drawable mEndLine;
    private int mMarkerSize;
    private int mLineSize;

    private Rect mBounds;


    public TimeLineView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public static int getTimeLineViewType(final int position, final int total_size) {
        if (total_size == 1) {
            return LineType.ONLY_ONE;
        } else if (position == 0) {
            return LineType.BEGIN;
        } else if (position == total_size - 1) {
            return LineType.END;
        } else {
            return LineType.NORMAL;
        }
    }

    private void init(final AttributeSet attrs) {
        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.timeline_style);
        mMarker = typedArray.getDrawable(R.styleable.timeline_style_marker);
        mStartLine = typedArray.getDrawable(R.styleable.timeline_style_line);
        mEndLine = typedArray.getDrawable(R.styleable.timeline_style_line);
        mMarkerSize = typedArray.getDimensionPixelSize(R.styleable.timeline_style_marker_size, 25);
        mLineSize = typedArray.getDimensionPixelSize(R.styleable.timeline_style_line_size, 2);
        typedArray.recycle();

        if (mMarker == null) {
            mMarker = ContextCompat.getDrawable(this.getContext(), R.drawable.marker);
        }
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //Width measurements of the width and height and the inside view of child controls
        final int w = mMarkerSize + getPaddingLeft() + getPaddingRight();
        final int h = mMarkerSize + getPaddingTop() + getPaddingBottom();

        // Width and height to determine the final view through a systematic approach to decision-making
        final int widthSize = resolveSizeAndState(w, widthMeasureSpec, 0);
        final int heightSize = resolveSizeAndState(h, heightMeasureSpec, 0);

        setMeasuredDimension(widthSize, heightSize);
        initDrawable();
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // When the view is displayed when the callback
        // Positioning Drawable coordinates, then draw
        initDrawable();
    }

    private void initDrawable() {
        final int pLeft = getPaddingLeft();
        final int pRight = getPaddingRight();
        final int pTop = getPaddingTop();
        final int pBottom = getPaddingBottom();

        final int width = getWidth();// Width of current custom view
        final int height = getHeight();

        final int cWidth = width - pLeft - pRight;// Circle width
        final int cHeight = height - pTop - pBottom;

        final int markSize = Math.min(mMarkerSize, Math.min(cWidth, cHeight));

        if (mMarker != null) {
            mMarker.setBounds(pLeft, pTop, pLeft + markSize, pTop + markSize);
            mBounds = mMarker.getBounds();
        }

        final int centerX = mBounds.centerX();
        final int lineLeft = centerX - (mLineSize >> 1);
        if (mStartLine != null) {
            mStartLine.setBounds(lineLeft, 0, mLineSize + lineLeft, mBounds.top);
        }

        if (mEndLine != null) {
            mEndLine.setBounds(lineLeft, mBounds.bottom, mLineSize + lineLeft, height);
        }

    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (mMarker != null) {
            mMarker.draw(canvas);
        }

        if (mStartLine != null) {
            mStartLine.draw(canvas);
        }
        if (mEndLine != null) {
            mEndLine.draw(canvas);
        }
    }

    public void setMarker(final Drawable marker) {
        mMarker = marker;
        initDrawable();
    }

    public void setStartLine(final Drawable startLine) {
        mStartLine = startLine;
        initDrawable();
    }

    public void setEndLine(final Drawable endLine) {
        mEndLine = endLine;
        initDrawable();
    }

    public void setMarkerSize(final int markerSize) {
        mMarkerSize = markerSize;
        initDrawable();
    }

    public void setLineSize(final int lineSize) {
        mLineSize = lineSize;
        initDrawable();
    }

    public void initLine(final int viewType) {

        if (viewType == LineType.BEGIN) {
            setStartLine(null);
        } else if (viewType == LineType.END) {
            setEndLine(null);
        } else if (viewType == LineType.ONLY_ONE) {
            setStartLine(null);
            setEndLine(null);
        }

        initDrawable();
    }
}
