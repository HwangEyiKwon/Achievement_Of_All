package com.example.parkseunghyun.achievementofall

import adapter.ContentsPagerAdapter
import adapter.StoriesAdapter
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.parkseunghyun.achievementofall.Activities.PenaltyActivity
import com.example.parkseunghyun.achievementofall.Activities.ReportActivity
import com.example.parkseunghyun.achievementofall.Configurations.RequestCodeCollection
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.container_contents_pager.*
import model.StoriesModel
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

/**
    REFACTORED
    TODO: 다른 Pager들과의 상호작용이 제대로 되나 다시 볼 필요 있음.
 */

class ContentsHomeActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private var storyArrayList=  mutableListOf<StoriesModel>()
    private var storyView: RecyclerView? = null
    private var storiesAdapter: StoriesAdapter? = null


    private var contentJoinButton: Button? = null
    private var contentsPagerAdapter: ContentsPagerAdapter? = null
    private var contentsViewPager: ViewPager? = null

    private var textContentName: TextView? = null
    private var textJoinState: TextView? = null

    private var contentDuration: TextView? = null
    private var tmpRequestCode: Int = -1

    private var selectedYear: Int? = null
    private var selectedMonthOfYear: Int? = null
    private var selectedDayOfMonth: Int? = null

    var startDate: JSONObject? = null
    var endDate: JSONObject? = null

    var content: String? = null
    var joinState: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contents_home)


        textJoinState = findViewById(R.id.id_joined_OR_not)
        textContentName = findViewById(R.id.contentName)
        contentDuration = findViewById(R.id.duration)
        contentJoinButton = findViewById(R.id.button_to_join)

        contentDuration!!.text = "기간"
        content = intent.getStringExtra("contentName")
        textContentName!!.text = content

        initViewComponents()

        contentJoinButton?.setOnClickListener {

            goToJoinConfirm()

        }

        /** 위에까지가 View 생성, 여기서부터 FCM인지 체크해서 남은 동작 처리.*/
        if( RequestCodeCollection.IS_FCM_FLAG == false ) {

        } else {

            RequestCodeCollection.IS_FCM_FLAG = false

            val fcmCategory = intent.getStringExtra("fcm_category")

            if( fcmCategory.equals("목표 달성 실패 알림") ) {

                contentsViewPager?.currentItem = 1
                app_desc_indicator.createDotPanel(3, R.drawable.indicator_dot_off, R.drawable.indicator_dot_on, 1)

                val goToPenalty = Intent(this, PenaltyActivity::class.java)
                goToPenalty.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                goToPenalty.putExtra("contentName", content)

                startActivityForResult(goToPenalty, RequestCodeCollection.REQUEST_RETURN_FROM_CONTENT_PENALTY)

            } else if ( fcmCategory.equals("인증 시간이 얼마 남지 않았어요!") ) {

                Toast.makeText(this, "인증버튼을 눌러 인증을 시작해주세요", Toast.LENGTH_SHORT).show()

            } else if( fcmCategory.equals("목표 달성 성공 알림") ) {

                Toast.makeText(this, "보상 확인 버튼을 눌러주세요", Toast.LENGTH_SHORT).show()

            } else if( fcmCategory.equals("과반수의 반대로 인증에 실패하셨습니다") ) {

                Toast.makeText(this, "과반수의 반대로 인증에 실패하셨습니다", Toast.LENGTH_SHORT).show()

                val rejectUserArray = intent.getStringExtra("rejectUserArray")
                val rejectReasonArray = intent.getStringExtra("rejectReasonArray")

                contentsViewPager?.currentItem = 1
                app_desc_indicator.createDotPanel(3, R.drawable.indicator_dot_off, R.drawable.indicator_dot_on, 1)

                val goToReport = Intent(this, ReportActivity::class.java)
                goToReport.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                goToReport.putExtra("contentName", content)
                goToReport.putExtra("rejectUserArray", rejectUserArray)
                goToReport.putExtra("rejectReasonArray", rejectReasonArray)

                startActivityForResult(goToReport, RequestCodeCollection.REQUEST_RETURN_FROM_CONTENT_REPORT)

            } else if( fcmCategory.equals("마지막 인증까지 성공하셨습니다! 컨텐츠 종료일에 보상을 받으실 수 있습니다.") ) {

                contentsViewPager?.currentItem = 1
                app_desc_indicator.createDotPanel(3, R.drawable.indicator_dot_off, R.drawable.indicator_dot_on, 1)

                val goToReport = Intent(this, ReportActivity::class.java)
                goToReport.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                goToReport.putExtra("contentName", content)

                startActivityForResult(goToReport, RequestCodeCollection.REQUEST_RETURN_FROM_CONTENT_SUCCESS)

            } else if( fcmCategory.equals("컨텐츠에 새로운 인증영상이 올라왔습니다!") ) {

                Toast.makeText(this, "새로운 인증영상을 확인해주세요!", Toast.LENGTH_LONG).show()

            }
        }

    }

    private fun initViewComponents(){

        val jsonObject = JSONObject()
        jsonObject.put("token", loadJWTToken())
        jsonObject.put("contentName", content)

        VolleyHttpService.getParticipatedInfo(this, jsonObject){ success ->

            joinState = success.getInt("joinState")
            startDate = success.getJSONObject("startDate")
            endDate = success.getJSONObject("endDate")

            val durationView =
                    "${startDate!!.getInt("year")}/${startDate!!.getInt("month")}/${startDate!!.getInt("day")} \n~ ${endDate!!.getInt("year")}/${endDate!!.getInt("month")}/${endDate!!.getInt("day")}"

            contentJoinButton!!.setTextColor(resources.getColor(R.color.icongrey))
            contentJoinButton!!.isEnabled = false
            contentDuration!!.text = durationView

            when(joinState){

                0 -> { textJoinState?.text = "참가중 (시작전)" }

                1 -> { textJoinState?.text =  "참가중 (진행중)" }

                2 -> { textJoinState?.text = "목표 달성 성공" }

                3-> {

                    textJoinState?.text = "미참가중"

                    contentJoinButton!!.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                    contentJoinButton!!.isEnabled = true

                    contentDuration!!.text = "기간"

                }

                4->{ textJoinState?.setText("목표 달성 실패") }

            }

            getStories()

            contentsViewPager = findViewById(R.id.contents_pager_container)
            contentsPagerAdapter = adapter.ContentsPagerAdapter(supportFragmentManager)
            contentsViewPager!!.adapter = contentsPagerAdapter

            contentsViewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{

                override fun onPageScrollStateChanged(p0: Int) {}
                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
                override fun onPageSelected(p0: Int) {

                    app_desc_indicator.selectDot(p0)

                }

            })

            if( tmpRequestCode == RequestCodeCollection.REQUEST_RETURN_FROM_CONTENT_PENALTY ||
                    tmpRequestCode == RequestCodeCollection.REQUEST_RETURN_FROM_CONTENT_REPORT ||
                    tmpRequestCode == RequestCodeCollection.REQUEST_RETURN_FROM_CONTENT_SUCCESS ) {

                contentsViewPager?.currentItem = 1
                app_desc_indicator.createDotPanel(3, R.drawable.indicator_dot_off, R.drawable.indicator_dot_on, 1)

            } else {

                contentsViewPager?.currentItem = 0
                app_desc_indicator.createDotPanel(3, R.drawable.indicator_dot_off, R.drawable.indicator_dot_on, 0)

            }


        }
    }

    /** 참여하기 클릭 후 일자 선택 시 작동 .*/
    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {

        selectedYear = year
        selectedMonthOfYear = monthOfYear + 1
        selectedDayOfMonth = dayOfMonth

        val goToConfirmingPage = Intent(applicationContext, ConfirmJoinActivity::class.java)
        goToConfirmingPage.putExtra("selectedYear", selectedYear!!) /*송신*/
        goToConfirmingPage.putExtra("selectedMonthOfYear", selectedMonthOfYear!!)
        goToConfirmingPage.putExtra("selectedDayOfMonth", selectedDayOfMonth!!)
        goToConfirmingPage.putExtra("contentName", content!!)
        goToConfirmingPage.putExtra("token", loadJWTToken())

        startActivityForResult(goToConfirmingPage, RequestCodeCollection.REQUEST_RETURN_FRON_CONFIRM_JOIN)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        tmpRequestCode = requestCode

        initViewComponents()

    }

    private fun goToJoinConfirm(){

        val jsonObject = JSONObject()
        jsonObject.put("token", loadJWTToken())
        jsonObject.put("contentName", content)

        VolleyHttpService.contentJoin(this, jsonObject){ success ->

            val now: Calendar = Calendar.getInstance()
            val datePickerDialog: DatePickerDialog = DatePickerDialog.newInstance(this@ContentsHomeActivity
                    , now.get(Calendar.YEAR)
                    , now.get(Calendar.MONTH)
                    , now.get(Calendar.DAY_OF_MONTH))

            datePickerDialog.setTitle(("시작 날짜 선택"))
            availableDaySetting(datePickerDialog, success)
            datePickerDialog.show(fragmentManager, "DatePicker")

            super.onResume()

        }
    }

    private fun availableDaySetting(datePickerDialog: DatePickerDialog, startDates: JSONObject) {

        val dates = startDates.getJSONArray("startDate")
        val calendars = arrayOfNulls<Calendar>(dates.length())

        var availableStartDate: JSONObject
        var tmpDateToAdd: Calendar?

        for(indexOfAvailableDates in 0 .. (dates.length() - 1) ){

            availableStartDate = dates[indexOfAvailableDates] as JSONObject

            tmpDateToAdd = Calendar.getInstance()
            tmpDateToAdd.set(Calendar.YEAR, availableStartDate.getInt("year"))
            tmpDateToAdd.set(Calendar.MONTH, availableStartDate.getInt("month")-1)
            tmpDateToAdd.set(Calendar.DAY_OF_MONTH, availableStartDate.getInt("day"))

            calendars[indexOfAvailableDates] = tmpDateToAdd

        }

        datePickerDialog.selectableDays = calendars
        datePickerDialog.highlightedDays = calendars

    }

    private fun getStories(){

        val jsonObject = JSONObject()
        jsonObject.put("token", loadJWTToken())
        jsonObject.put("contentName", content)

        VolleyHttpService.getOthers(this, jsonObject){ success ->

            val layoutManager = LinearLayoutManager(this@ContentsHomeActivity, LinearLayoutManager.HORIZONTAL, false)
            val othersStories = success.getJSONArray("others")

            storyView = findViewById(R.id.recystories)
            storyView!!.layoutManager = layoutManager
            storyView!!.itemAnimator = DefaultItemAnimator()
            storyArrayList = ArrayList()

            for(indexOfStories in 0..(othersStories.length()-1)){

                val storiesModel = StoriesModel()
                val other = othersStories[indexOfStories] as JSONObject

                storiesModel.email = other.getString("email")
                storiesModel.name = other.getString("name")
                storiesModel.contentName = content
                storyArrayList.add(storiesModel)

            }

            storiesAdapter = StoriesAdapter(this@ContentsHomeActivity, storyArrayList)
            storyView!!.adapter = storiesAdapter

        }
    }

    fun loadJWTToken(): String{

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPref.getString("token", "")

    }

}

/** TODO 주석 지워라

생성

1. FCM으로 들어온 경우 - 마지막에 체크하면 될 듯 .
2. 일반적인 경로로 들어온 경우

[FCM]
1. "목표 달성 실패 알림" - pager를 progress로 이동시킨 뒤 Penalty Activity로; ResultCode 필요?? -> 현 코드는 progress pager로 이동시키는데에 쓰이는듯
2. "목표 달성 성공 알림" - pager만 progress로 이동 + Toast;
3. "인증 시간이 얼마 남지 않았어요!" - 여기가 끝 + Toast
4. "과반수의 반대로 인증에 실패하셨습니다" - Report Activity로; ResultCode 필요
5(아직 미구현). "새로 인증해줘야 하는 사용자가 생김"

[일반적인 경로]
    joinState체크

        참가중 (시작전)
            joinState, duration 띄우고
            참여하기 Button 비활


        참가중 (진행중)
            joinState, duration, 달력 띄우고
            참여하기 Button 비활


        목표 달성 성공
            joinState, duration, 달력 띄우고
            참여하기 Button 비활


        목표 달성 실패
            joinState, duration, 달력 띄우고
            참여하기 Button 비활


        미참가중
            joinState, duration, 달력 띄우고
            참여하기 Button 활성화

 */

