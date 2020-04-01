package com.plutuscommerce.gbk.utils

import android.graphics.*
import com.squareup.picasso.Transformation

//Author Muhammad Mubashir 10/30/2018

class RoundedCornersTransform : Transformation {
    private fun createRoundedRectBitmap(bitmap: Bitmap,
                                        topLeftCorner: Float, topRightCorner: Float,
                                        bottomRightCorner: Float, bottomLeftCorner: Float): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height,
                Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val color = Color.WHITE
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        val path = Path()
        val radii = floatArrayOf(topLeftCorner, bottomLeftCorner, topRightCorner, topRightCorner, bottomRightCorner, bottomRightCorner, bottomLeftCorner, bottomLeftCorner)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        path.addRoundRect(rectF, radii, Path.Direction.CW)
        canvas.drawPath(path, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    override fun transform(source: Bitmap): Bitmap {
        val size = Math.min(source.width, source.height)

        val x = (source.width - size)
        val y = (source.height - size)

        val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
        if (squaredBitmap != source) {
            source.recycle()
        }

        val r = size / 15f

        val roundedBitmap = createRoundedRectBitmap(squaredBitmap, r, r, r, r)

        squaredBitmap.recycle()

        return roundedBitmap
    }

    override fun key(): String {
        return "rounded_corners"
    }
}