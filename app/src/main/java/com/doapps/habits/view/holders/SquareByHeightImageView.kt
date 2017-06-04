package com.doapps.habits.view.holders

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet

class SquareByHeightImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {
    //TODO: move to RoundByHeightImageView here and in menu header
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredHeight)
    }
}