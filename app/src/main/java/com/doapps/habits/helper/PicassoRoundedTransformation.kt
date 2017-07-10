package com.doapps.habits.helper

import android.graphics.*
import com.squareup.picasso.Transformation

class PicassoRoundedTransformation : Transformation {
  private var radius: Int = 0

  override fun transform(source: Bitmap): Bitmap {
    val paint = Paint()
    radius = source.height shr 1 // shift right
    paint.isAntiAlias = true
    paint.shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

    val output = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    canvas.drawRoundRect(RectF(0f, 0f, source.width.toFloat(),
        source.height.toFloat()), radius.toFloat(), radius.toFloat(), paint)

    source.recycle()

    return output
  }

  override fun key() = "rounded(radius=$radius)"
}
