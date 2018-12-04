package com.example.parkseunghyun.achievementofall.Decorator;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.example.parkseunghyun.achievementofall.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

/**
 * Decorate several days with a dot
 */
public class EventDecorator implements DayViewDecorator {

    private final Drawable drawable;
    private int color;
    private HashSet<CalendarDay> dates;

    public EventDecorator(int color, Collection<CalendarDay> dates,Activity context, String which) {

        if(which == "success"){
            drawable = context.getResources().getDrawable(R.drawable.ic_icons_check);
        }else if(which == "fail"){
            drawable = context.getResources().getDrawable(R.drawable.ic_icons_wrong);
        }else if(which == "notYet"){
            drawable = context.getResources().getDrawable(R.drawable.ic_icons_notyet);
        }else if(which == "startDate"){
            drawable = context.getResources().getDrawable(R.drawable.ic_icons_start);
        }else {
            drawable = context.getResources().getDrawable(R.drawable.ic_icons_end);
        }


//        if(which.equals("success")){
//            drawable = context.getResources().getDrawable(R.drawable.ic_icons_check);
//        }
//        if(which.equals("fail")){
//            drawable = context.getResources().getDrawable(R.drawable.ic_icons_wrong);
//        }
//        if(which.equals("notYet")){
//            drawable = context.getResources().getDrawable(R.drawable.ic_icons_notyet);
//        }
//        if(which.equals("startDate")){
//            drawable = context.getResources().getDrawable(R.drawable.ic_icons_start);
//        }
//        else if(which.equals("endDate")){
//            drawable = context.getResources().getDrawable(R.drawable.ic_icons_end);
//        }



        this.color = color;
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
        //view.addSpan(new DotSpan(5, color)); // 날자밑에 점
    }
}