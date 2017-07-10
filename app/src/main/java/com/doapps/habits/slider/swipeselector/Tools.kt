package com.doapps.habits.slider.swipeselector

import android.content.Context

fun Float.dpToPixel(context: Context): Int {
  val resources = context.resources
  val metrics = resources.displayMetrics
  return (this * (metrics.densityDpi / 160.0f)).toInt()
}