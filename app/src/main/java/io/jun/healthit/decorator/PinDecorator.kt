package io.jun.healthit.decorator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.text.style.LineBackgroundSpan
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import io.jun.healthit.R
import io.jun.healthit.model.data.Memo
import io.jun.healthit.util.stringToDate

class PinDecorator(val context: Context, memos: List<Memo>): DayViewDecorator {

    private val dates = memos.map {
        it.date?.let { strDate ->
            CalendarDay.from(stringToDate(strDate))
        }
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(CustomPinSpan(context))
    }

    class CustomPinSpan(val context: Context) : LineBackgroundSpan {

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
                canvas.drawBitmap((it as BitmapDrawable).bitmap, null, Rect(35, -15, 75, 25), null)
            }
        }
    }
}