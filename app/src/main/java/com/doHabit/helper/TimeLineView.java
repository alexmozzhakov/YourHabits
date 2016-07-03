package com.dohabit.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.dohabit.R;
import com.dohabit.models.LineType;

public class TimeLineView extends View {

    private Drawable mMarker;
    private Drawable mStartLine;
    private Drawable mEndLine;
    private int mMarkerSize;
    private int mLineSize;

    private Rect mBounds;


    public TimeLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public static int getTimeLineViewType(int position, int total_size) {
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

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.timeline_style);
        mMarker = typedArray.getDrawable(R.styleable.timeline_style_marker);
        mStartLine = typedArray.getDrawable(R.styleable.timeline_style_line);
        mEndLine = typedArray.getDrawable(R.styleable.timeline_style_line);
        mMarkerSize = typedArray.getDimensionPixelSize(R.styleable.timeline_style_marker_size, 25);
        mLineSize = typedArray.getDimensionPixelSize(R.styleable.timeline_style_line_size, 2);
        typedArray.recycle();

        if (mMarker == null) {
            mMarker = ContextCompat.getDrawable(getContext(), R.drawable.marker);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //Width measurements of the width and height and the inside view of child controls
        int w = mMarkerSize + getPaddingLeft() + getPaddingRight();
        int h = mMarkerSize + getPaddingTop() + getPaddingBottom();

        // Width and height to determine the final view through a systematic approach to decision-making
        int widthSize = View.resolveSizeAndState(w, widthMeasureSpec, 0);
        int heightSize = View.resolveSizeAndState(h, heightMeasureSpec, 0);

        setMeasuredDimension(widthSize, heightSize);
        initDrawable();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // When the view is displayed when the callback
        // Positioning Drawable coordinates, then draw
        initDrawable();
    }

    private void initDrawable() {
        int pLeft = getPaddingLeft();
        int pRight = getPaddingRight();
        int pTop = getPaddingTop();
        int pBottom = getPaddingBottom();

        int width = getWidth();// Width of current custom view
        int height = getHeight();

        int cWidth = width - pLeft - pRight;// Circle width
        int cHeight = height - pTop - pBottom;

        int markSize = Math.min(mMarkerSize, Math.min(cWidth, cHeight));

        if (mMarker != null) {
            mMarker.setBounds(pLeft, pTop, pLeft + markSize, pTop + markSize);
            mBounds = mMarker.getBounds();
        }

        int centerX = mBounds.centerX();
        int lineLeft = centerX - (mLineSize >> 1);
        if (mStartLine != null) {
            mStartLine.setBounds(lineLeft, 0, mLineSize + lineLeft, mBounds.top);
        }

        if (mEndLine != null) {
            mEndLine.setBounds(lineLeft, mBounds.bottom, mLineSize + lineLeft, height);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
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

    public void setMarker(Drawable marker) {
        mMarker = marker;
        initDrawable();
    }

    private void removeStartLine() {
        mStartLine = null;
        initDrawable();
    }

    private void removeEndLine() {
        mEndLine = null;
        initDrawable();
    }

    public void setMarkerSize(int markerSize) {
        mMarkerSize = markerSize;
        initDrawable();
    }

    public void setLineSize(int lineSize) {
        mLineSize = lineSize;
        initDrawable();
    }

    public void initLine(int viewType) {

        if (viewType == LineType.BEGIN) {
            removeStartLine();
        } else if (viewType == LineType.END) {
            removeEndLine();
        } else if (viewType == LineType.ONLY_ONE) {
            removeStartLine();
            removeEndLine();
        }

        initDrawable();
    }
}