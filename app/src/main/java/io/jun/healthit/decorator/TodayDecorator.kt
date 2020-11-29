package io.jun.healthit.decorator

import android.graphics.Color
import android.graphics.Typeface
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class TodayDecorator: DayViewDecorator {

    private val today = CalendarDay.today()

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return today == day
    }

    override fun decorate(view: DayViewFacade?) {
        view?.apply {
            addSpan(StyleSpan(Typeface.BOLD))
            addSpan(ForegroundColorSpan(Color.parseColor("#F3B88F")))
        }
    }

}