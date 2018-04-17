package com.doapps.habits.slider.swipeselector

import android.content.res.Resources

/**
 * Converts dps to pixels using the formula:
 * pixels = density * density independent pixels
 *
 * @return dimension in pixels
 */
fun Float.dpToPixel(): Int {
  return (this * Resources.getSystem().displayMetrics.density).toInt()
}