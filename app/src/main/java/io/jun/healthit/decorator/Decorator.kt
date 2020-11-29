package io.jun.healthit.decorator

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.style.LineBackgroundSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import io.jun.healthit.model.data.Memo
import io.jun.healthit.util.EtcUtil
import io.jun.healthit.util.Setting

class Decorator(memos: List<Memo>, val color: TagColor): DayViewDecorator {

    //String 타입인 날짜를 Date 타입으로 바꿔준 후, 다시 CalendarDay 타입으로 바꿔서 맵핑
    private val dates = memos.map { it.date?.let { strDate ->
        CalendarDay.from(EtcUtil.stringToDate(strDate))
    }}

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(CustomDotSpan(Color.parseColor(color.hex)))
    }

    class CustomDotSpan : LineBackgroundSpan {
        private val radius: Float
        private var colors = IntArray(1)

        constructor() {
            this.radius = DotSpan.DEFAULT_RADIUS
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
}