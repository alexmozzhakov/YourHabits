package com.doapps.habits.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.doapps.habits.R
import com.doapps.habits.helper.Progressing

/**
 * Simple single android view component that can be used to showing a round progress bar.
 * It can be customized with size, stroke size, colors and text etc.
 * Progress change will be animated.
 * Created by Kri54stoffer, http://kmdev.se
 */
// TODO: make it extend ProgressBar and not view
class CircularProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr), Progressing {

  private var mViewHeight: Int = 0

  private val mStartAngle = -90f      // Always start from top (default is: "3 o'clock on a watch.")
  private var mSweepAngle = 0f              // How long to sweep from mStartAngle
  private val mMaxSweepAngle = 360f         // Max degrees to sweep = full circle
  private var mStrokeWidth = 5f             // Width of outline
  private val mAnimationDuration = 400       // Animation duration for progress change
  private val mMaxProgress = 100             // Max progress to use
  private var mRoundedCorners = false     // Set to true if rounded corners should be applied to outline ends
  private var mProgressColor = ContextCompat.getColor(context, R.color.colorAccent)   // Outline color

  private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    mViewHeight = height
    drawOutlineArc(canvas)
  }

  private fun drawOutlineArc(canvas: Canvas) {

    val diameter = mViewHeight - mStrokeWidth * 2

    val outerOval = RectF(mStrokeWidth, mStrokeWidth, diameter, diameter)

    mPaint.color = mProgressColor
    mPaint.strokeWidth = mStrokeWidth
    mPaint.isAntiAlias = true
    mPaint.strokeCap = if (mRoundedCorners) Paint.Cap.ROUND else Paint.Cap.BUTT
    mPaint.style = Paint.Style.STROKE
    canvas.drawArc(outerOval, mStartAngle, mSweepAngle, false, mPaint)
  }

  private fun calcSweepAngleFromProgress(progress: Int): Float {
    return mMaxSweepAngle / mMaxProgress * progress
  }

  /**
   * Set progress of the circular progress bar.
   * @param progress progress between 0 and 100.
   */
  override fun setProgress(progress: Int) {
    val animator = ValueAnimator.ofFloat(mSweepAngle, calcSweepAngleFromProgress(progress))
    animator.interpolator = DecelerateInterpolator()
    animator.duration = mAnimationDuration.toLong()
    animator.addUpdateListener { valueAnimator ->
      mSweepAngle = valueAnimator.animatedValue as Float
      invalidate()
    }
    animator.start()
  }
}
