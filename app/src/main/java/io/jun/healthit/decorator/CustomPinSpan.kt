package io.jun.healthit.decorator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.text.style.LineBackgroundSpan
import androidx.core.content.ContextCompat
import io.jun.healthit.R

class CustomPinSpan(val context: Context): LineBackgroundSpan {

    override fun drawBackground(
        canvas: Canvas,
        paint: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        lineNumber: Int
    ) {

        ContextCompat.getDrawable(context, R.drawable.ic_pin).let {
            canvas.drawBitmap((it as BitmapDrawable).bitmap, null, Rect(40, -10, 80, 30), null)
        }
    }
}