package com.example.parkseunghyun.achievementofall.CalendarDecorator

import android.graphics.Color
import android.text.style.ForegroundColorSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.*

/**
 *  REFACTORED
 */


// SundayDecorator
// 달력에 보여줄 이미지를 설정합니다.
// 일요일 날짜를 표시합니다.
class SundayDecorator : DayViewDecorator {

    private val calendar = Calendar.getInstance()

    override fun shouldDecorate(day: CalendarDay): Boolean {

        day.copyTo(calendar)
        val weekDay = calendar.get(Calendar.DAY_OF_WEEK)

        return weekDay == Calendar.SUNDAY

    }

    override fun decorate(view: DayViewFacade) {

        view.addSpan(ForegroundColorSpan(Color.RED))

    }

}