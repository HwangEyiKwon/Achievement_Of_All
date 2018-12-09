package com.example.parkseunghyun.achievementofall.CalendarDecorator

import android.graphics.Color
import android.text.style.ForegroundColorSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

/**
 *  REFACTORED
 */

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