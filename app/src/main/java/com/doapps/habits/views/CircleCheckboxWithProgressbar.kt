package com.doapps.habits.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.CheckBox

class CircleCheckboxWithProgressbar(context: Context?, attrs: AttributeSet?) : CheckBox(context, attrs) {
    val circlePaint = Paint()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val viewWidthHalf = this.measuredWidth / 2f
        val viewHeightHalf = this.measuredHeight / 2f
        val radius: Float
        if (viewWidthHalf > viewHeightHalf)
            radius = viewHeightHalf - 10f
        else
            radius = viewWidthHalf - 10f
        circlePaint.style = Paint.Style.FILL
        circlePaint.isAntiAlias = true
        circlePaint.color = Color.parseColor("#000000")
        canvas.drawCircle(viewWidthHalf, viewHeightHalf, radius, circlePaint)
    }
}