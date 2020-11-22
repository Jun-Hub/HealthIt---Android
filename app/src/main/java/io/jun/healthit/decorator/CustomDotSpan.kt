package io.jun.healthit.decorator

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.LineBackgroundSpan
import com.prolificinteractive.materialcalendarview.spans.DotSpan.DEFAULT_RADIUS
import io.jun.healthit.util.Setting

class CustomDotSpan : LineBackgroundSpan {
    private val radius: Float
    private var colors = IntArray(1)

    constructor() {
        this.radius = DEFAULT_RADIUS
        this.colors[0] = 0
    }

    constructor(color: Int) {
        this.radius = Setting.DECORATOR_RADIUS
        this.colors[0] = color
    }

    //한 날짜에 여러개의 점을 찍을 때
    constructor(radius: Float, colors: IntArray) {
        this.radius = radius
        this.colors = colors
    }

    override fun drawBackground(canvas: Canvas, paint: Paint,
                                left: Int, right: Int, top: Int, baseline: Int, bottom: Int,
                                charSequence: CharSequence,
                                start: Int, end: Int, lineNum: Int) {

        val total = if (colors.size > 2) 3 else colors.size
        //leftMost는 점이 두 개 이상일 때 점과 점사이의 거리
        var leftMost = (total - 1) * -12

        for (i in 0 until total) {
            val oldColor = paint.color
            if (colors[i] != 0) {
                paint.color = colors[i]
            }
            canvas.drawCircle(((left + right) / 2 - leftMost).toFloat(), bottom + radius, radius, paint)
            paint.color = oldColor
            leftMost += 24
        }
    }

}

