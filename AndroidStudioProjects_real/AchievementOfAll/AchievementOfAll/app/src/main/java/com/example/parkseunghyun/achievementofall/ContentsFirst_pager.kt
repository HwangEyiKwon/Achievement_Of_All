package com.example.parkseunghyun.achievementofall

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class ContentsFirst_pager : Fragment() {

    private var mView: View? = null
    private var calendar: MaterialCalendarView? = null
    private var goToVideoButton: Button? = null

    private var calendarInfo: JSONArray?= null

    // 사용자의 jwt-token
    private var jwtToken: String ?= null
    private var contentName: String? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mView =  inflater!!.inflate(R.layout.contents_fragment_1, container, false)
        val activity = activity as ContentsHomeActivity
        jwtToken = activity.jwtToken.toString()
        contentName = activity.content.toString()
        println("캘랜더 페이지에서!!!!"+jwtToken+contentName)
        getCalendarInfo(jwtToken!!,contentName!!)

        return mView

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getCalendarInfo(token: String, contentName: String){
        val jsonObject = JSONObject()
        jsonObject.put("token", token)
        jsonObject.put("contentName", contentName)

        VolleyHttpService.getCalendarInfo(this.context, jsonObject){ success ->
            println("달력달력"+success)
            settingCalendar(success)
        }
    }
    private fun settingCalendar(jsonArray: JSONArray){

        calendar = mView?.findViewById(R.id.calendarView)
        calendar!!.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1))
                .setMaximumDate(CalendarDay.from(2030, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        calendar!!.addDecorators(
                SundayDecorator(),
                SaturdayDecorator(),
                OneDayDecorator())

        val calendar2 = Calendar.getInstance()
        calendar2.add(Calendar.MONTH, -2)
        val dates = mutableListOf<CalendarDay>()

//        for (i in 0..29) {
//            val day = CalendarDay.from(calendar2)
//            println("데이데이데이데"+ CalendarDay.from(2018,1,1))
//            dates.add(day)
//            calendar2.add(Calendar.DATE, 5)
//        }
        for (i in 0..(jsonArray.length() - 1)) {

            var y = jsonArray.getJSONObject(i).getString("year").toInt()
            var m = jsonArray.getJSONObject(i).getString("month").toInt()
            var d = jsonArray.getJSONObject(i).getString("day").toInt()

            var day = CalendarDay.from(y,m,d)
            dates.add(day)
            calendar2.add(Calendar.DATE, 5)
        }


        goToVideoButton = mView?.findViewById(R.id.go_to_video_button)
        goToVideoButton?.setOnClickListener{
            var intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            startActivity(intent)
        }


        calendar!!.setOnDateChangedListener(OnDateSelectedListener { widget, date, selected -> Log.e("DAY", "DAY:$date") })
        calendar!!.addDecorators(EventDecorator(Color.RED, dates,activity))
    }

//    override fun onStart() {
//        super.onStart()
//        initializePlayer()
//    }

//
//    fun initializePlayer(view: View){
//        // Create a default TrackSelector
//        val bandwidthMeter =  DefaultBandwidthMeter();
//        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter);
//        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory);
//
//        //Initialize the player
//        val player = ExoPlayerFactory.newSimpleInstance(this.context, trackSelector);
//
//        //Initialize simpleExoPlayerView
//        val simpleExoPlayerView = view.findViewById(R.id.simpleExoPlayerView) as SimpleExoPlayerView
//
//        simpleExoPlayerView.setPlayer(player)
//
//        // Produces DataSource instances through which media data is loaded.
//        val dataSourceFactory = DefaultDataSourceFactory(this.context, Util.getUserAgent(this.context, "CloudinaryExoplayer"));
//
//        // Produces Extractor instances for parsing the media data.
//        val extractorsFactory = DefaultExtractorsFactory();
//
//        // This is the MediaSource representing the media to be played.
//        val videoUri = Uri.parse("http://192.168.8.97:3000/video");
//        val videoSource =  ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);
//
//        // Prepare the player with the source.
//        player.prepare(videoSource);
//
//    }

}

