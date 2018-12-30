package com.example.parkseunghyun.achievementofall.CalendarDecorator

import android.graphics.Color
import android.text.style.ForegroundColorSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

/**
 *  REFACTORED
 */

// OneDayDecorator
// 달력에 보여줄 이미지를 설정합니다.
// 오늘 날짜를 표시합니다.
class OneDayDecorator : DayViewDecorator {

    private var date: CalendarDay? = null

    init {

        date = CalendarDay.today()

    }

    override fun shouldDecorate(day: CalendarDay): Boolean {

        return date != null && day == date

    }

    override fun decorate(view: DayViewFacade) {

        view.addSpan(ForegroundColorSpan(Color.GREEN))

    }

}