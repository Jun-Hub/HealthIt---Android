package io.jun.healthit.decorator

import android.content.Context
import android.graphics.Color
import com.prolificinteractive.materialcalendarview.*
import io.jun.healthit.model.data.Memo
import io.jun.healthit.util.EtcUtil

class TagOrangeDecorator(val context: Context, memos: List<Memo>): DayViewDecorator {

    //String 타입인 날짜를 Date 타입으로 바꿔준 후, 다시 CalendarDay 타입으로 바꿔서 맵핑
    private val dates = memos.map { it.date?.let { strDate ->
        CalendarDay.from(EtcUtil.stringToDate(strDate)) }
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(CustomDotSpan(Color.parseColor("#E67E22")))
    }
}