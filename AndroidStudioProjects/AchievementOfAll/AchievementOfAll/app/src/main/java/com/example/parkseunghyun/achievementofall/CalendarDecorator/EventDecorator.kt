package com.example.parkseunghyun.achievementofall.CalendarDecorator

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable

import com.example.parkseunghyun.achievementofall.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.util.HashSet

/**
 *  REFACTORED
 */

class EventDecorator(private val color: Int, dates: Collection<CalendarDay>, context: Context, which: String) : DayViewDecorator {

    private var drawable: Drawable? = null
    private val dates: HashSet<CalendarDay>

    init {

        if (which == "startDate") {

            drawable = context.resources.getDrawable(R.drawable.ic_icons_start)

        } else if (which == "endDate"){

            drawable = context.resources.getDrawable(R.drawable.ic_icons_end)

        } else if (which == "success") {

            drawable = context.resources.getDrawable(R.drawable.ic_icons_check)

        } else if (which == "fail") {

            drawable = context.resources.getDrawable(R.drawable.ic_icons_wrong)

        } else if (which == "notYet") {

            drawable = context.resources.getDrawable(R.drawable.ic_icons_notyet)

        }

        this.dates = HashSet(dates)

    }

    override fun shouldDecorate(day: CalendarDay): Boolean {

        return dates.contains(day)

    }

    override fun decorate(view: DayViewFacade) {

        view.setSelectionDrawable(drawable!!)

    }

}