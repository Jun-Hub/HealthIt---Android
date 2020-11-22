package io.jun.healthit.decorator

import android.graphics.Color
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import io.jun.healthit.model.data.Memo
import io.jun.healthit.util.EtcUtil

class TagYellowDecorator(memos: List<Memo>): DayViewDecorator {

    //String 타입인 날짜를 Date 타입으로 바꿔준 후, 다시 CalendarDay 타입으로 바꿔서 맵핑
    private val dates = memos.map { it.date?.let { strDate ->
        CalendarDay.from(EtcUtil.stringToDate(strDate))
    }
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(CustomDotSpan(Color.parseColor("#F1C40F")))
    }
}