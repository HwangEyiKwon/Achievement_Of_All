package com.example.parkseunghyun.achievementofall

//import android.support.v7.widget.RecyclerView

//import adapter.StoriesAdapter
//import model.StoriesModel
import adapter.StoriesAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.parkseunghyun.achievementofall.Activities.HomeActivity
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.Interfaces.RecyclerViewClickListener
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.contents_pager_container.*
import model.StoriesModel
import org.jetbrains.anko.startActivityForResult
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import android.R.attr.data


class ContentsHomeActivity : AppCompatActivity(), RecyclerViewClickListener, DatePickerDialog.OnDateSetListener {

    // 타 사용자 인증 영상

    private var othersArrayList=  mutableListOf<StoriesModel>()
    private var recyclerView: RecyclerView? = null
    private var storiesAdapter: StoriesAdapter? = null
    private var text_joinedORnot: TextView? = null
    private var remainingTime: TextView? = null
    private var contentJoinButton: Button? = null

    // 사용자의 jwt-token
    var jwtToken: String ?= null
    var contentName: TextView ?= null
    var contentDuration: TextView ?= null
    var startDate: JSONObject?= null
    var endDate: JSONObject?= null

    var content: String ?= null
    var joinState: Int ?= null
    var contentId: String ?= null


    var selectedYear: Int? = null
    var selectedMonthOfYear: Int? = null
    var selectedDayOfMonth: Int? = null
    var tmpCalendar: Calendar? = null

    var contentsHomeContext: Context? = null
    val REQUEST_FOR_UPDATE_CONTENTS = 222

    override fun onBackPressed() {
//        super.onBackPressed()

//        val goToHome = Intent(applicationContext, HomeActivity::class.java)
//        goToHome.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
//        startActivity(goToHome)
        finish()
    }


    override fun recyclerViewListClicked(v: View, position: Int) {
        Toast.makeText(getApplicationContext(), "position is $position", Toast.LENGTH_LONG)
    }


    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {

        selectedYear = year
        selectedMonthOfYear = monthOfYear + 1
        selectedDayOfMonth = dayOfMonth

//        println("CHECK DATE: " + selectedYear + "/" + selectedMonthOfYear + "/" + selectedDayOfMonth)

        val goToConfirmingPage = Intent(applicationContext, ConfirmJoinActivity::class.java)
        goToConfirmingPage.putExtra("selectedYear", selectedYear!!) /*송신*/
        goToConfirmingPage.putExtra("selectedMonthOfYear", selectedMonthOfYear!!)
        goToConfirmingPage.putExtra("selectedDayOfMonth", selectedDayOfMonth!!)
        goToConfirmingPage.putExtra("contentName", content!!)
        goToConfirmingPage.putExtra("token", jwtToken!!)

        startActivityForResult(goToConfirmingPage, REQUEST_FOR_UPDATE_CONTENTS)
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_FOR_UPDATE_CONTENTS -> {
                println("TEST------ 1")
                getParticipatedInfo()
                getOthers()
                /* */
            }
            101 -> {
                println("TEST------ 2")
                /* TODO: 여기서 스토리를 없애는 처리를 해야된다.*/
                getParticipatedInfo()
                getOthers()
            }

        }
        println("ONACTIVITYTEST")
    }

    override fun onRestart() {
        super.onRestart()
        println("RESTART contentHOME")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contents_home)

        contentsHomeContext = this

        text_joinedORnot = findViewById(R.id.id_joined_OR_not)

        remainingTime = findViewById(R.id.ydh_remaining_time)
        remainingTime?.setText("남은 인증시간")

        contentName = findViewById(R.id.contentName)
        contentDuration = findViewById(R.id.duration)

        contentDuration!!.setText("기간")

        jwtToken = loadToken()

        if(intent.getStringExtra("contentName")!=null){
            content = intent.getStringExtra("contentName")
            contentName!!.setText(content)
        }

        getParticipatedInfo()
        getOthers()

        val cal = Calendar.getInstance()
        println("CALENDER TEST: " + cal)

        contentJoinButton = findViewById(R.id.button_to_join)
        contentJoinButton?.setOnClickListener {
            contentJoin()
        }

    }

    private fun loadToken(): String{
        var auto = PreferenceManager.getDefaultSharedPreferences(this)

        return auto.getString("token", "")
    }

    private fun getParticipatedInfo(){

        val jsonObject = JSONObject()
        jsonObject.put("token", jwtToken)
        jsonObject.put("contentName", content)

        VolleyHttpService.getParticipatedInfo(this, jsonObject){ success ->
            println(success)
            println("팔티시페이트")

            // Start Date + End Date (+ 추가)
            // 3: 참여 X

            // 0: 시작전 컨텐츠
            // 1: 진행중 컨텐츠
            // 2: 종료된 컨텐츠
            joinState = success.getInt("joinState")
            startDate = success.getJSONObject("startDate")
            endDate = success.getJSONObject("endDate")

            var d = "${startDate!!.getInt("year")}/${startDate!!.getInt("month")}/${startDate!!.getInt("day")} \n~ ${endDate!!.getInt("year")}/${endDate!!.getInt("month")}/${endDate!!.getInt("day")}"

            when(joinState){
                0 -> {
                    text_joinedORnot?.setText("참가중 (시작전)")
                    contentJoinButton!!.isEnabled = false
                    contentDuration!!.setText(d)
                }
                1 -> {
                    text_joinedORnot?.setText("참가중 (진행중)")
                    contentJoinButton!!.isEnabled = false
                    contentDuration!!.setText(d)
                }
                2 -> {
                    text_joinedORnot?.setText("참가중 (종료)")
                    contentJoinButton!!.isEnabled = false
                    contentDuration!!.setText(d)
                }
                3-> {
                    text_joinedORnot?.setText("미참가중")
                    contentJoinButton!!.isEnabled = true

                }
            }
            val viewPager = findViewById<ViewPager>(R.id.contents_pager_container)
            val pAdapter = adapter.ContentsPagerAdapter(supportFragmentManager)
            viewPager.adapter = pAdapter

            viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                override fun onPageScrollStateChanged(p0: Int) {

                }
                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

                }
                override fun onPageSelected(p0: Int) {
                    contents_circle_indicator.selectDot(p0)
                }
            })

            contents_circle_indicator.createDotPanel(3, R.drawable.indicator_dot_off, R.drawable.indicator_dot_on, 0)


        }
    }
    private fun getOthers(){

        val jsonObject = JSONObject()
        jsonObject.put("token", jwtToken)
        jsonObject.put("contentName", content)

        VolleyHttpService.getOthers(this, jsonObject){ success ->
            println(success)
            var others = success.getJSONArray("others")

            recyclerView = findViewById(R.id.recystories)
            val layoutManager = LinearLayoutManager(this@ContentsHomeActivity, LinearLayoutManager.HORIZONTAL, false)
            recyclerView!!.layoutManager = layoutManager
            recyclerView!!.itemAnimator = DefaultItemAnimator()
            othersArrayList = ArrayList()

            for(i in 0..(others.length()-1)){
                val storiesModel = StoriesModel()
                var other = others[i] as JSONObject
                println(other)
                storiesModel.email = other.getString("email")
                storiesModel.name = other.getString("name")
                storiesModel.contentName = content
                othersArrayList!!.add(storiesModel)
            }

            storiesAdapter = StoriesAdapter(this@ContentsHomeActivity, othersArrayList!!, this)
            recyclerView!!.adapter = storiesAdapter

        }
    }
    private fun contentJoin(){

        val jsonObject = JSONObject()
        jsonObject.put("token", jwtToken)
        jsonObject.put("contentName", content)
        VolleyHttpService.contentJoin(this, jsonObject){ success ->

            println(success)

            var now: Calendar = Calendar.getInstance()
            var datePickerDialog: DatePickerDialog = DatePickerDialog.newInstance(this@ContentsHomeActivity
                    , now.get(Calendar.YEAR)
                    , now.get(Calendar.MONTH)
                    , now.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.setTitle(("시작 날짜 선택"))

            /* TODO: 아래 함수로 들어가서 서버랑 통신하면서 선택가능일자 받아와서 처리. */
            availableDaySetting(datePickerDialog, success)
            /*------*/
            datePickerDialog.show(fragmentManager, "DatePicker")

            super.onResume()
        }
    }


    private fun availableDaySetting(datePickerDialog: DatePickerDialog, startDates: JSONObject) {
        /*
        TODO: 서버에서 시작가능일의 개수, 시작가능일을 받아와서 arrayOfNulls의 size에다가 넣고
        TODO: 아래다가가 for문으로 시작가능일들을 calendars 배열에 쑤셔넣음. 그럼 됨.
        */


        var dates = startDates.getJSONArray("startDate")
        var calendars = arrayOfNulls<Calendar>(dates.length())


        var ymd: JSONObject

        for(i in 0 .. dates.length()-1 ){

            ymd = dates[i] as JSONObject

            /* < TODO: 현재는 임시 데이터 > */
            tmpCalendar = Calendar.getInstance()
            tmpCalendar!!.set(Calendar.YEAR, ymd.getInt("year"))
            tmpCalendar!!.set(Calendar.MONTH, ymd.getInt("month")-1)
            tmpCalendar!!.set(Calendar.DAY_OF_MONTH, ymd.getInt("day"))

            calendars[i] = tmpCalendar
            //            calendars.plus(tmpCalendar)

        }

        println("CHECK CALENDAR ----" + tmpCalendar)
        datePickerDialog.selectableDays = calendars
        datePickerDialog.highlightedDays = calendars
    }



}
