package com.doapps.habits.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

public class PicassoRoundedTransformation implements Transformation {
    private int radius;

    @Override
    public Bitmap transform(final Bitmap source) {
        final Paint paint = new Paint();
        radius = source.getHeight() >> 1;
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawRoundRect(new RectF(0, 0, source.getWidth(),
                source.getHeight()), radius, radius, paint);

        source.recycle();

        return output;
    }

    @Override
    public String key() {
        return "rounded(radius=" + radius + ")";
    }
}
