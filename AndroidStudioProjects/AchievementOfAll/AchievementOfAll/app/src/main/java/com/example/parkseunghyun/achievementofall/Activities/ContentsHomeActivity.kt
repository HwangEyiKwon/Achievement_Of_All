package com.example.parkseunghyun.achievementofall

//import android.support.v7.widget.RecyclerView

//import adapter.StoriesAdapter
//import model.StoriesModel
import adapter.ContentsPagerAdapter
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
import com.example.parkseunghyun.achievementofall.Activities.PenaltyActivity
import com.example.parkseunghyun.achievementofall.Activities.ReportActivity
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.Interfaces.RecyclerViewClickListener
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.contents_pager_container.*
import model.StoriesModel
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class ContentsHomeActivity : AppCompatActivity(), RecyclerViewClickListener, DatePickerDialog.OnDateSetListener {

    // 타 사용자 인증 영상

    private var othersArrayList=  mutableListOf<StoriesModel>()
    private var recyclerView: RecyclerView? = null
    private var storiesAdapter: StoriesAdapter? = null
    private var text_joinedORnot: TextView? = null
    private var remainingTime: TextView? = null
    private var contentJoinButton: Button? = null
    private var pAdapter: ContentsPagerAdapter? = null

    // 사용자의 jwt-token
    var jwtToken: String ?= null
    var contentName: TextView ?= null
    var contentDuration: TextView ?= null
    var startDate: JSONObject?= null
    var endDate: JSONObject?= null

    var content: String ?= null
    var joinState: Int ?= null
    var tmpRequestCode: Int ?= null

    var cm: Int ?= null
    var rm: Int ?= null

    var selectedYear: Int? = null
    var selectedMonthOfYear: Int? = null
    var selectedDayOfMonth: Int? = null
    var tmpCalendar: Calendar? = null

    var contentsHomeContext: Context? = null
    val REQUEST_FOR_UPDATE_CONTENTS = 222

    var IS_FCM_FLAG = 0

    var viewPager: ViewPager ?= null

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
        println("TEST KAPPA 들어왓니 " + requestCode)

        tmpRequestCode = requestCode
        when (requestCode) {

            REQUEST_FOR_UPDATE_CONTENTS -> {
                println("TEST------ 1")
                getParticipatedInfo()
                /* */
            }

            101 -> {
                println("TEST------ 2")
                /* TODO: 여기서 스토리를 없애는 처리를 해야된다.*/
                getParticipatedInfo()
            }

            88 -> {
                println("TEST KAPPA 왜 일로안와 ")
                getParticipatedInfo()

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

        contentName = findViewById(R.id.contentName)
        contentDuration = findViewById(R.id.duration)

        contentDuration!!.setText("기간")

        jwtToken = loadToken()

        if(intent.getStringExtra("contentName")!=null){
            content = intent.getStringExtra("contentName")
            contentName!!.setText(content)
        }

        var ownIntent:Intent = intent

        if(ownIntent.getStringExtra("fcm_category") != null){
            if(ownIntent.getStringExtra("fcm_category").equals("목표 달성 실패 알림") || ownIntent.getStringExtra("fcm_category").equals("목표 달성 성공 알림")){
                IS_FCM_FLAG = 1
            } else { IS_FCM_FLAG = 0 }
        }


        getParticipatedInfo()

        val cal = Calendar.getInstance()
        println("CALENDER TEST: " + cal)

        contentJoinButton = findViewById(R.id.button_to_join)
        contentJoinButton?.setOnClickListener {
            contentJoin()
        }


        if(ownIntent.getStringExtra("fcm_category") == null){
            // 아무것도 안해도 됨
            println("TEST 334 -- 아무것도 안함")
        }
        else if(ownIntent.getStringExtra("fcm_category").equals("목표 달성 실패 알림")){
            // TODO 여기에 ContentHomeActivity 들어가야됨.
            println("TEST 334 -- 목표 달성 실패 알림")
//            viewPager?.currentItem = 1
//            contents_circle_indicator.createDotPanel(3, R.drawable.indicator_dot_off, R.drawable.indicator_dot_on, 1)


            val goToPenalty = Intent(this, PenaltyActivity::class.java)
            goToPenalty.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            goToPenalty.putExtra("contentName", content)
//            goToPenalty.putExtra("currentMoney", cm)
            startActivityForResult(goToPenalty, 88)// PENALTY_CODE 여야하는데 그냥 REWARD랑 동일하게 둬도 정상동작.

        }
        else if(ownIntent.getStringExtra("fcm_category").equals("인증 시간이 얼마 남지 않았어요!")){
            println("TEST 334 -- 인증시간 얼마안남음")
            Toast.makeText(this, "인증버튼을 눌러 인증을 시작해주세요", Toast.LENGTH_SHORT).show()
        }
        else if(ownIntent.getStringExtra("fcm_category").equals("목표 달성 성공 알림")){
            println("TEST 334 -- 목표달성 성공")
            Toast.makeText(this, "보상 확인 버튼을 눌러주세요", Toast.LENGTH_SHORT).show()
//            viewPager?.currentItem = 1
//            contents_circle_indicator.createDotPanel(3, R.drawable.indicator_dot_off, R.drawable.indicator_dot_on, 1)

        }
        else if(ownIntent.getStringExtra("fcm_category").equals("과반수의 반대로 인증에 실패하셨습니다")){
            println("TEST 334 -- 과반수의 반대로 인증에 실패하셨습니다")
            Toast.makeText(this, "과반수의 반대로 인증에 실패하셨습니다 - 나머지 짜세요", Toast.LENGTH_SHORT).show()
            val rejectUserArray = ownIntent.getStringExtra("rejectUserArray")
            val rejectReasonArray = ownIntent.getStringExtra("rejectReasonArray")

            println("TESTINBLACK ----- USERs" + rejectUserArray.length + " :::: " + rejectUserArray!!)
            println("TESTINBLACK ----- REASONs" + rejectReasonArray.length + " :::: " + rejectReasonArray!!)


            val goToReport = Intent(this, ReportActivity::class.java)
            goToReport.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            goToReport.putExtra("contentName", content)
            goToReport.putExtra("rejectUserArray", rejectUserArray)
            goToReport.putExtra("rejectReasonArray", rejectReasonArray)

            startActivityForResult(goToReport, 88)// TODO 일단 REWARD랑 동일하게 두되 오류나면 수정필요


            // TODO 여기서 신고 Activity 만들어야된다.

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
                    contentJoinButton!!.setTextColor(resources.getColor(R.color.icongrey))
                    contentDuration!!.setText(d)
                }
                1 -> {
                    text_joinedORnot?.setText("참가중 (진행중)")
                    contentJoinButton!!.isEnabled = false
                    contentJoinButton!!.setTextColor(resources.getColor(R.color.icongrey))
                    contentDuration!!.setText(d)
                }
                2 -> {
                    text_joinedORnot?.setText("목표 달성 성공")
                    contentJoinButton!!.setTextColor(resources.getColor(R.color.icongrey))
                    contentJoinButton!!.isEnabled = false
                    contentDuration!!.setText(d)
                }
                3-> {
                    text_joinedORnot?.setText("미참가중")
                    contentJoinButton!!.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                    contentJoinButton!!.isEnabled = true

                }
                4->{
                    text_joinedORnot?.setText("목표 달성 실패")
                    contentJoinButton!!.setTextColor(resources.getColor(R.color.icongrey))
                    contentJoinButton!!.isEnabled = false
                    contentDuration!!.setText(d)
                }
            }
            viewPager = findViewById(R.id.contents_pager_container)
            pAdapter = adapter.ContentsPagerAdapter(supportFragmentManager)
            viewPager!!.adapter = pAdapter

            if(tmpRequestCode == 88 || IS_FCM_FLAG == 1){
                println("TEST KAPPA -- ")
                viewPager?.currentItem = 1
                contents_circle_indicator.createDotPanel(3, R.drawable.indicator_dot_off, R.drawable.indicator_dot_on, 1)

            }

            viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                override fun onPageScrollStateChanged(p0: Int) {

                }
                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

                }
                override fun onPageSelected(p0: Int) {
                    contents_circle_indicator.selectDot(p0)
                }
            })

            if(tmpRequestCode != 88 && IS_FCM_FLAG != 1){
                contents_circle_indicator.createDotPanel(3, R.drawable.indicator_dot_off, R.drawable.indicator_dot_on, 0)
            }
            getOthers()

        }
    }

    private fun getOthers(){

        val jsonObject = JSONObject()
        jsonObject.put("token", jwtToken)
        jsonObject.put("contentName", content)

        VolleyHttpService.getOthers(this, jsonObject){ success ->
            println(success)
            var others = success.getJSONArray("others")

            println("TESTER___" + others)

            recyclerView = findViewById(R.id.recystories)
            val layoutManager = LinearLayoutManager(this@ContentsHomeActivity, LinearLayoutManager.HORIZONTAL, false)
            recyclerView!!.layoutManager = layoutManager
            recyclerView!!.itemAnimator = DefaultItemAnimator()
            othersArrayList = ArrayList()

            for(i in 0..(others.length()-1)){
                val storiesModel = StoriesModel()
                var other = others[i] as JSONObject
                storiesModel.email = other.getString("email")
                storiesModel.name = other.getString("name")
                storiesModel.contentName = content
                println("STORY TEST: 스토리 뷰 생성할때 서버로부터 받는 email 및 name? ----" + storiesModel.email + " 그리고 " + storiesModel.name + " 컨텐츠는 " + storiesModel.contentName)
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
