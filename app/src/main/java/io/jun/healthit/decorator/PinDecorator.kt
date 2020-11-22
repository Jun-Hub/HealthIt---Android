package io.jun.healthit.decorator

import android.content.Context
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import io.jun.healthit.model.data.Memo
import io.jun.healthit.util.EtcUtil

class PinDecorator(val context: Context, memos: List<Memo>): DayViewDecorator {

    private val dates = memos.map { it.date?.let { strDate ->
        CalendarDay.from(EtcUtil.stringToDate(strDate)) }
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(CustomPinSpan(context))
    }
}