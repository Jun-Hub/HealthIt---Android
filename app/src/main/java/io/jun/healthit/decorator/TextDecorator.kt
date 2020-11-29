package io.jun.healthit.decorator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.style.LineBackgroundSpan
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import io.jun.healthit.R
import io.jun.healthit.model.data.Inbody
import io.jun.healthit.util.EtcUtil
import io.jun.healthit.util.Setting

class TextDecorator(val context: Context, val inbody: Inbody, val flag: Int): DayViewDecorator {

    private val date = CalendarDay.from(EtcUtil.stringToDate(inbody.date))

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return date == day
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(CustomTextSpan())
    }

    inner class CustomTextSpan: LineBackgroundSpan {

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
            val string = when(flag) {
                0 -> inbody.weight.toString()
                1 -> inbody.skeletalMuscle.toString()
                else -> inbody.percentFat.toString()
            }

            if(string != "null") {
                canvas.drawText(string,
                    65f,
                    80f,
                    Paint().apply {
                        textAlign = Paint.Align.CENTER
                        textSize = 33f
                        style = Paint.Style.FILL_AND_STROKE
                        color = ContextCompat.getColor(context, R.color.colorSky)
                    })
            }
            else {  //선택된 보기 스피너 중 해당 데이터가 null이면 점만 찍어주기
                paint.color = Color.parseColor("#C5D6DB")
                Setting.DECORATOR_RADIUS.let {
                    canvas.drawCircle(((left + right) / 2).toFloat(), bottom + it, it, paint)
                }
            }
        }
    }
}