package com.example.parkseunghyun.achievementofall.Fragments

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.parkseunghyun.achievementofall.ContentsHomeActivity
import com.example.parkseunghyun.achievementofall.CalendarDecorator.EventDecorator
import com.example.parkseunghyun.achievementofall.CalendarDecorator.OneDayDecorator
import com.example.parkseunghyun.achievementofall.CalendarDecorator.SaturdayDecorator
import com.example.parkseunghyun.achievementofall.CalendarDecorator.SundayDecorator
import com.example.parkseunghyun.achievementofall.Configurations.*
import com.example.parkseunghyun.achievementofall.Interfaces.VideoUploadInterface
import com.example.parkseunghyun.achievementofall.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.*

/*
[일반적인 경로]
    joinState체크

        참가중 (시작전)
            joinState, duration, 달력, {남은 인증시간 -> 컨텐츠 시작까지 로 바꿔서} 띄우고
            참여하기 Button 비활
            인증하기 Button 비활

            진행현황에서 보상 받기 Button 비활성
            프로그레스바 0
            {% 진행중입니다 -> 컨텐츠 시작 전입니다.}

        참가중 (진행중)
            joinState, duration, 달력, {남은 인증시간} 띄우고
            참여하기 Button 비활
            인증하기 Button은 다음 인증일 체크해서 활성/비활성: 일단은 현상유지하기 TODO 이거 더 못올리게 처리해야하나

            진행현황에서 보상 받기 Button 비활성
            환급가능 금액, 보상가능 금액 받아다가 출력
            프로그레스바 0
            {% 진행중입니다 -> 컨텐츠 시작 전입니다.}


        목표 달성 성공
            joinState, duration, 달력, {남은 인증시간} 띄우고
            참여하기 Button 비활
            인증하기 Button 비활성

            진행현황에서 보상 받기 Button 비활성
            환급가능 금액, 보상가능 금액 받아다가 출력
            프로그레스바는 100이겠지.
            {% 진행중입니다 -> 컨텐츠 시작 전입니다.}


        목표 달성 실패
            joinState, duration, 달력, {남은 인증시간} 띄우고
            참여하기 Button 비활
            인증하기 Button은 비활성

            진행현황에서 보상 받기 Button 비활성
            환급가능 금액, 보상가능 금액 받아다가 출력
            프로그레스바 0
            {% 진행중입니다 -> % 진행중에 실패하셨습니다.}



        미참가중
            joinState, duration, 달력, {남은 인증시간 -> 아직 참여중이지 않습니다} 띄우고
            참여하기 Button 활성화
            인증하기 Button 비활

            진행현황에서 보상 받기 Button 비활성
            프로그레스바 0
            {% 진행중입니다 -> 아직 참가중이지 않습니다.}

 */


/*
    남은 시간 알고리즘
 */

class ContentsMyInfoPager : Fragment(), EasyPermissions.PermissionCallbacks {
    private var mContext: Context? = null
    private var mView: View? = null
    private var calendar: MaterialCalendarView? = null
    private var goToVideoButton: Button? = null

    // 사용자의 jwt-token
    private var jwtToken: String ?= null
    private var contentName: String? = null
    private var joinState: Int ?= null
    private var startDate: JSONObject ?= null
    private var endDate: JSONObject ?= null

    private val REQUEST_VIDEO_CAPTURE = 300
    private val READ_REQUEST_CODE = 200
    private val WRITE_REQUEST_CODE = 400
    private var uri: Uri? = null
    private var pathToStoredVideo: String? = null

    private var globalVariables: GlobalVariables?= GlobalVariables()
    private var ipAddress: String = globalVariables!!.ipAddress

    private var forRemoveFile: File? = null

    private var contentsName: TextView? = null

    private var remainingDays:TextView?=null
    private var remainingHours:TextView?=null
    private var remainingMinutes:TextView?=null

    private var remainingDaysText:TextView?=null
    private var remainingHoursText:TextView?=null
    private var remainingMinutesText:TextView?=null

    private var contents_over_msg:TextView?= null
    private var contents_not_joined:TextView? = null

    private var diffOfDay: Long?= null
    private var diffOfHour: Long?= null
    private var diffOfMinute: Long?= null

    private var videoCaptureIntent:Intent? = null

    var tt: TimerTask ? = null
    var timer: Timer? = null

    private var fab_open: Animation? = null
    private var fab_close: Animation? = null
    private var isFabOpen:Boolean = false
    private var fab: FloatingActionButton? = null
    private var fab1: TextView? = null
    private var fab2: TextView? = null
    private var fabText: LinearLayout? = null

    var nextYear: Int ? = null
    var nextMonth: Int ? = null
    var nextDay: Int ? = null

    var y: Int ? = null
    var m: Int ? = null
    var d: Int ? = null



    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mContext = context
        mView =  inflater!!.inflate(R.layout.fragment_contents_myinfo, container, false)
        remainingDays = mView?.findViewById(R.id.remaining_days)
        remainingHours = mView?.findViewById(R.id.remaining_hours)
        remainingMinutes = mView?.findViewById(R.id.remaining_minutes)

        remainingDaysText = mView?.findViewById(R.id.remaining_days_text)
        remainingHoursText = mView?.findViewById(R.id.remaining_hours_text)
        remainingMinutesText = mView?.findViewById(R.id.remaining_minutes_text)

        contents_over_msg = mView?.findViewById(R.id.contents_over_msg)
        contents_not_joined = mView?.findViewById(R.id.contents_not_joined)

        fab = mView?.findViewById(R.id.fab)
//        fab1 = mView?.findViewById(R.id.fab1)
//        fab2 = mView?.findViewById(R.id.fab2)
        fabText = mView?.findViewById(R.id.fab_text)

        fab_open = AnimationUtils.loadAnimation(mContext!!, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(mContext!!, R.anim.fab_close);

        val contentHomeActivity = mContext!! as ContentsHomeActivity
        jwtToken = contentHomeActivity.loadJWTToken()
        contentName = contentHomeActivity.content.toString()
        joinState = contentHomeActivity.joinState
        startDate = contentHomeActivity.startDate
        endDate = contentHomeActivity.endDate

        contentsName = mView?.findViewById(R.id.id_contents_name_1)
        contentsName?.setText(contentName)


        println("캘랜더 페이지에서!!!!"+jwtToken+contentName)
        getCalendarInfo(jwtToken!!,contentName!!)

        goToVideoButton = mView?.findViewById(R.id.go_to_video_button)
        goToVideoButton?.setOnClickListener{
            videoCaptureIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            if (videoCaptureIntent!!.resolveActivity(mContext!!.packageManager) != null) {

                if (EasyPermissions.hasPermissions(mContext!!, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (EasyPermissions.hasPermissions(mContext!!, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        if (EasyPermissions.hasPermissions(mContext!!, android.Manifest.permission.CAMERA)) {
                            startActivityForResult(videoCaptureIntent, RequestCodeCollection.REQUEST_RETURN_FROM_VIDEO_RECORD)
                            // 1) 모든 권한이 있다면 바로 카메라 실행
                        } else {
                            EasyPermissions.requestPermissions(this, getString(R.string.read_file), RequestCodeCollection.GRANT_REQUEST_CAMERA, Manifest.permission.CAMERA)
                            // 4) READ권한, WRITE권한이 있는데 카메라 권한이 없다면 권한 요청 -> onPermissionsGranted 함수에서 바로 카메라 실행.
                        }
                    } else {
                        EasyPermissions.requestPermissions(this, getString(R.string.read_file), RequestCodeCollection.GRANT_REQUEST_WRITE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        // 3) READ권한은 있고 WRITE권한이 없다면 권한 요청 -> onPermissionsGranted 함수에서 체인형태로 CAMERA 이어서 확인.
                    }
                } else {
                    EasyPermissions.requestPermissions(this, getString(R.string.read_file), RequestCodeCollection.GRANT_REQUEST_READ, Manifest.permission.READ_EXTERNAL_STORAGE)
                    // 2) READ권한이 없다면 권한 요청 -> onPermissionsGranted 함수에서 체인형태로 WRITE, CAMERA 이어서 확인.
                }
            }
        }

        fab?.setOnClickListener {
            anim()
        }


        if(joinState != 1){
            goToVideoButton!!.isEnabled = false
            goToVideoButton!!.setTextColor(resources.getColor(R.color.icongrey))
        }else{
            goToVideoButton!!.isEnabled = true
            goToVideoButton!!.setTextColor(resources.getColor(R.color.colorPrimaryDark))
        }

        return mView
    }

    fun anim() {

        if (isFabOpen) {
            fabText?.startAnimation(fab_close)
            isFabOpen = false
        } else {
            fabText?.startAnimation(fab_open)
            isFabOpen = true
        }
    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK ) {

            when (requestCode){
                RequestCodeCollection.REQUEST_RETURN_FROM_VIDEO_RECORD -> {

                    uri = data?.data
                    pathToStoredVideo = getRealPathFromURIPath(uri!!, activity)
                    goToVideoButton!!.isEnabled = false //TODO 이거 false임
                    uploadVideoToServer(pathToStoredVideo!!)

                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {

        when(requestCode){
            READ_REQUEST_CODE -> {
                if (EasyPermissions.hasPermissions(mContext!!, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (EasyPermissions.hasPermissions(mContext!!, android.Manifest.permission.CAMERA)) {
                        startActivityForResult(videoCaptureIntent, RequestCodeCollection.REQUEST_RETURN_FROM_VIDEO_RECORD)
                    } else {
                        EasyPermissions.requestPermissions(this, getString(R.string.read_file), RequestCodeCollection.GRANT_REQUEST_CAMERA, Manifest.permission.CAMERA)
                    }
                } else {
                    EasyPermissions.requestPermissions(this, getString(R.string.read_file), RequestCodeCollection.GRANT_REQUEST_WRITE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }

            WRITE_REQUEST_CODE -> {
                if (EasyPermissions.hasPermissions(mContext!!, android.Manifest.permission.CAMERA)) {
                    startActivityForResult(videoCaptureIntent, RequestCodeCollection.GRANT_REQUEST_CAMERA)
                } else {
                    EasyPermissions.requestPermissions(this, getString(R.string.read_file), RequestCodeCollection.GRANT_REQUEST_CAMERA, Manifest.permission.CAMERA)
                }
            }

            REQUEST_VIDEO_CAPTURE -> {
                startActivityForResult(videoCaptureIntent, RequestCodeCollection.GRANT_REQUEST_CAMERA)
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {

    }



    private fun getCalendarInfo(token: String, contentName: String){

        val jsonObject = JSONObject()
        jsonObject.put("token", token)
        jsonObject.put("contentName", contentName)

        VolleyHttpService.getCalendarInfo(mContext!!, jsonObject){ success ->

            settingCalendar(success)

        }

    }

    private fun uploadVideoToServer(pathToVideoFile: String) {

        val videoFile = File(pathToVideoFile)
        val videoBody = RequestBody.create(MediaType.parse("video/*"), videoFile)
        val vFile = MultipartBody.Part.createFormData("video", videoFile.name, videoBody)

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(object:Interceptor {

            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {

                val original = chain!!.request()
                val request = original.newBuilder()
                        .header("content_name", contentName)
                        .header("jwt_token", jwtToken)
                        .method(original.method(), original.body())
                        .build()
                return chain.proceed(request)

            }

        })

        val client = httpClient.build()
        val retrofit = Retrofit.Builder()
                .baseUrl(ipAddress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        val vInterface = retrofit.create(VideoUploadInterface::class.java)
        val serverCom = vInterface.uploadVideoToServer(vFile)

        serverCom.enqueue(object : Callback<ResultObjectFromRetrofit2> {

            override fun onResponse(call: Call<ResultObjectFromRetrofit2>, response: Response<ResultObjectFromRetrofit2>) {

                val result = response.body()

                if (!TextUtils.isEmpty(result.success)) {

                    GlideLoadingFlag.setThumbnailFlag(true)
                    Toast.makeText(mContext, "인증영상 업로드 완료", Toast.LENGTH_LONG).show()

                }

                getCalendarInfo(jwtToken!!,contentName!!)

                forRemoveFile = File(pathToStoredVideo)
                forRemoveFile?.delete()

                val resolver: ContentResolver? = mContext!!.contentResolver
                resolver?.delete(uri, null, null)

            }

            override fun onFailure(call: Call<ResultObjectFromRetrofit2>, t: Throwable) {

            }
        })
    }

    private fun getRealPathFromURIPath(contentURI: Uri, activity: Activity): String? {
        val cursor = activity.contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) {

            return contentURI.path

        } else {

            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            return cursor.getString(idx)

        }
    }


    private fun settingCalendar(jsonArray: JSONArray){

        calendar = mView?.findViewById(R.id.calendarView)

        if(joinState == 3) {
//            calendar!!.visibility = View.GONE
        } else {
            calendar!!.visibility = View.VISIBLE
        }

        calendar!!.setHeaderTextAppearance(R.color.abc_background_cache_hint_selector_material_dark)
        calendar!!.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(startDate!!.getInt("year"),startDate!!.getInt("month")-1, startDate!!.getInt("day")))
                .setMaximumDate(CalendarDay.from(endDate!!.getInt("year"),endDate!!.getInt("month")-1, endDate!!.getInt("day")))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit()

        calendar!!.addDecorators(
                SundayDecorator(),
                SaturdayDecorator(),
                OneDayDecorator())

        val successDates = mutableListOf<CalendarDay>()
        val failDates = mutableListOf<CalendarDay>()
        val notYetDates = mutableListOf<CalendarDay>()

        val sDate = mutableListOf<CalendarDay>()
        val eDate = mutableListOf<CalendarDay>()

        sDate.add(CalendarDay.from(startDate!!.getInt("year"),startDate!!.getInt("month") -1, startDate!!.getInt("day")))
        eDate.add(CalendarDay.from(endDate!!.getInt("year"),endDate!!.getInt("month") -1, endDate!!.getInt("day")))

        val loopCount = jsonArray.length() - 1

        println("데이터 주냐?"+ loopCount)

        if(loopCount == -1){
            nextYear = startDate!!.getInt("year")
            nextMonth = startDate!!.getInt("month") - 1
            nextDay = startDate!!.getInt("day")
        }

        for (i in 0..loopCount) {

            y = jsonArray.getJSONObject(i).getString("year").toInt()
            m = jsonArray.getJSONObject(i).getString("month").toInt() - 1 // 서버에서 1~ 12로 주는데 여기서 0~11로... 일단 서버에서 11 준거로.
            d = jsonArray.getJSONObject(i).getString("day").toInt()

            var day = CalendarDay.from(y!!, m!!, d!!)

            if( i == loopCount ) {
                println("i는 무엇인가"+ i)
                // y, m, d + 3일 + 자정
                println("TESTINGING----" + y + "년" + ( m!! + 1 ) + "월" + ( d!! + 3 ) + "일")



                nextYear = y
                nextMonth = m // 0~11
                nextDay = d!! + 3
            }


            println(jsonArray.getJSONObject(i))
            if(jsonArray.getJSONObject(i).getInt("authen")==1){// success
                println("SUCCESS")
                successDates.add(day)
            }else if(jsonArray.getJSONObject(i).getInt("authen")==0){ // fail
                println("FAIL")
                failDates.add(day)
            }else{ // not yet
                notYetDates.add(day)
            }

        }

        tt = object : TimerTask() {
            override fun run() {

                val calendarCurrent = Calendar.getInstance()
                var calendarNext = Calendar.getInstance()

                // nextMonth가 0~11

                calendarNext.set(nextYear!!, nextMonth!! , nextDay!! + 1, 0, 0)

                var diffInSecondUnit = (calendarNext.timeInMillis - calendarCurrent.timeInMillis) / (1000) // 차이 with 초 단위
                var diffInMinuteUnit = diffInSecondUnit / 60

                println("TESTING 남은 시간은 ---- " + diffInMinuteUnit + "분::::")
                println("TESTTTTT 다음 인증 월 " + calendarNext.get(Calendar.MONTH) + " "+calendarCurrent.get(Calendar.MONTH))

//                        diffOfDay = ((diffInMinuteUnit / (60 * 60 * 24)) / 24) // 일 계산
//                        diffOfHour = ((diffInMinuteUnit / (60 * 60 * 24))  % 24)   // 시간 계산
//                        diffOfMinute = ((diffInMinuteUnit / 60) % 60)  // 분 계산

                println("TESTBLACK: 조인 스테이트 " + joinState)
                if(joinState == 3){
                    val notJoinedUpdateMsg = notJoinedThreadHandler.obtainMessage()
                    notJoinedThreadHandler.sendMessage(notJoinedUpdateMsg)
                    println("TESTBLACK: 미참가중")
                }
                // 다음인증일이 EndDate를 넘었다면? (마지막 인증까지 완료가 된거야)
                else if(calendarNext.get(Calendar.YEAR)!! >= endDate!!.getInt("year") && calendarNext.get(Calendar.MONTH) >= (endDate!!.getInt("month") -1) && calendarNext.get(Calendar.DAY_OF_MONTH) > endDate!!.getInt("day")){

                    val doneUpdateMsg = doneUpdateThreadHandler.obtainMessage()
                    doneUpdateThreadHandler.sendMessage(doneUpdateMsg)
                    println("TESTBLACK: 마지막 인증까지 완료")

                }
                else{

                    diffOfDay = ((diffInSecondUnit / (60 * 60 * 24))) // 일 계산
                    diffOfHour = ((diffInSecondUnit - (diffOfDay!!*60*60*24)) / (60*60))   // 시간 계산
                    diffOfMinute = ((diffInSecondUnit - diffOfDay!!*60*60*24 - diffOfHour!!*3600)/60)  // 분 계산
                    val diffUpdateMsg = diffUpdateThreadHandler.obtainMessage()
                    diffUpdateThreadHandler.sendMessage(diffUpdateMsg)

                }

                println("TESTING 현재 시간은: "+ calendarCurrent.get(Calendar.DAY_OF_MONTH) + "일" + calendarCurrent.get(Calendar.HOUR_OF_DAY) + "분" + calendarCurrent.get(Calendar.MINUTE) + "분")
                println("TESTING 다음 인증은 언제 직전까지?: "+ calendarNext.get(Calendar.DAY_OF_MONTH) + "일" + calendarNext.get(Calendar.HOUR_OF_DAY) + "분" + calendarNext.get(Calendar.MINUTE) + "분")
                println("TESTING 남은 시간은 ---- " + diffInSecondUnit + "초::::" + diffOfDay + "일" + diffOfHour +"시간" + diffOfMinute + "분")
                println("TESTING 컨텐츠 종료일 ---- " + endDate!!.getInt("year") + "년:::" + (endDate!!.getInt("month")) + "월" + endDate!!.getInt("day") +"일")



            }
        }

        // 타이머 재생성 방지 (타 액티비티 수행 후 재생성 등)
        if(timer == null){
            println("TESTING --- HOW MANY TIMER MADE")
            timer = Timer()
            timer!!.schedule(tt, 0, 1000) // 초 단위로 업데이트 이루어짐
        }


        calendar!!.setOnDateChangedListener(OnDateSelectedListener { widget, date, selected -> Log.e("DAY", "DAY:$date") })
        calendar!!.addDecorators(EventDecorator(Color.RED, successDates, mContext!! , "success"))
        calendar!!.addDecorators(EventDecorator(Color.RED, failDates, mContext!!, "fail"))
        calendar!!.addDecorators(EventDecorator(Color.RED, notYetDates, mContext!!, "notYet"))

        calendar!!.addDecorators(EventDecorator(Color.RED, sDate, mContext!!, "startDate"))
        calendar!!.addDecorators(EventDecorator(Color.RED, eDate, mContext!!, "endDate"))
    }


    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }

    val diffUpdateThreadHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {

            remainingDays!!.setText(diffOfDay.toString())
            remainingHours!!.setText(diffOfHour.toString())
            remainingMinutes!!.setText(diffOfMinute.toString())

        }
    }

    val doneUpdateThreadHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {

            diffOfDay = 0 // 일 계산
            diffOfHour = 0   // 시간 계산
            diffOfMinute = 0  // 분 계산

            remainingDays!!.visibility = View.GONE
            remainingDaysText!!.visibility = View.GONE
            remainingHours!!.visibility = View.GONE
            remainingHoursText!!.visibility = View.GONE
            remainingMinutes!!.visibility = View.GONE
            remainingMinutesText!!.visibility = View.GONE

            contents_over_msg!!.visibility = View.VISIBLE

            goToVideoButton!!.isEnabled = false //TODO 이거 false임
//            goToVideoButton!!.isEnabled = true

//            goToVideoButton!!.setTextColor(resources.getColor(R.color.icongrey))

        }
    }

    val notJoinedThreadHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {

            diffOfDay = 0 // 일 계산
            diffOfHour = 0   // 시간 계산
            diffOfMinute = 0  // 분 계산

            remainingDays!!.visibility = View.GONE
            remainingDaysText!!.visibility = View.GONE
            remainingHours!!.visibility = View.GONE
            remainingHoursText!!.visibility = View.GONE
            remainingMinutes!!.visibility = View.GONE
            remainingMinutesText!!.visibility = View.GONE

            contents_not_joined!!.visibility = View.VISIBLE
//            goToVideoButton?.setTextColor(resources.getColor(R.color.icongrey, null))

            goToVideoButton!!.isEnabled = false

        }
    }


}

