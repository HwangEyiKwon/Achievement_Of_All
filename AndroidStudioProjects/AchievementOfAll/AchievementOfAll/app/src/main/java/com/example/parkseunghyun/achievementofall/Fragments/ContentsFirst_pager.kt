package com.example.parkseunghyun.achievementofall.Fragments

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.parkseunghyun.achievementofall.Configurations.GlobalVariables
import com.example.parkseunghyun.achievementofall.Configurations.ResultObject
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.ContentsHomeActivity
import com.example.parkseunghyun.achievementofall.Decorator.EventDecorator
import com.example.parkseunghyun.achievementofall.Decorator.OneDayDecorator
import com.example.parkseunghyun.achievementofall.Decorator.SaturdayDecorator
import com.example.parkseunghyun.achievementofall.Decorator.SundayDecorator
import com.example.parkseunghyun.achievementofall.Interfaces.VideoInterface
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

class ContentsFirst_pager : Fragment(), EasyPermissions.PermissionCallbacks {

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
    private var uri: Uri? = null
    private var pathToStoredVideo: String? = null

    private var globalVariables: GlobalVariables?= GlobalVariables()
    private var ipAddress: String = globalVariables!!.ipAddress

    private var forRemoveFile: File? = null

    /**/
//    private var tmpContentName = "NoSmoking"
//    private var tmpMyEmail = "shp3@gmail.com"
    /**/

    override fun onResume() {
        super.onResume()
        println("ONRESUME CONTENTFIRST PAGER")
        getCalendarInfo(jwtToken!!,contentName!!)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mView =  inflater!!.inflate(R.layout.contents_fragment_1, container, false)

        val contentHomeActivity = activity as ContentsHomeActivity
        jwtToken = contentHomeActivity.jwtToken.toString()
        contentName = contentHomeActivity.content.toString()
        joinState = contentHomeActivity.joinState
        startDate = contentHomeActivity.startDate
        endDate = contentHomeActivity.endDate

        println("캘랜더 페이지에서!!!!"+jwtToken+contentName)


        goToVideoButton = mView?.findViewById(R.id.go_to_video_button)
        goToVideoButton?.setOnClickListener{
            val videoCaptureIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            if (videoCaptureIntent.resolveActivity(activity.packageManager) != null) {
                startActivityForResult(videoCaptureIntent, REQUEST_VIDEO_CAPTURE)
            }
        }

        if(joinState != 1){
            goToVideoButton!!.isEnabled = false
        }else{
            goToVideoButton!!.isEnabled = true
        }

        return mView

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_VIDEO_CAPTURE) {
            uri = data?.data
            if (EasyPermissions.hasPermissions(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                pathToStoredVideo = getRealPathFromURIPath(uri!!, activity)


                uploadVideoToServer(pathToStoredVideo!!)

            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.read_file), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE)
            }

            if (EasyPermissions.hasPermissions(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Don't do anything.
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.read_file), READ_REQUEST_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (uri != null) {
            if (EasyPermissions.hasPermissions(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                pathToStoredVideo = getRealPathFromURIPath(uri!!, activity)

                uploadVideoToServer(pathToStoredVideo!!)
            }
            if (EasyPermissions.hasPermissions(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Don't do anything
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {

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

    private fun uploadVideoToServer(pathToVideoFile: String) {
        val videoFile = File(pathToVideoFile)


        val videoBody = RequestBody.create(MediaType.parse("video/*"), videoFile)
        val vFile = MultipartBody.Part.createFormData("video", videoFile.name, videoBody)
        /**/

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
                return chain!!.proceed(request)
            }
        })

        val client = httpClient.build()

        /**/
        val retrofit = Retrofit.Builder()
                .baseUrl(ipAddress)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client) // >>>>>>
                .build()
        val vInterface = retrofit.create(VideoInterface::class.java)
        val serverCom = vInterface.uploadVideoToServer(vFile)

        serverCom.enqueue(object : Callback<ResultObject> {
            override fun onResponse(call: Call<ResultObject>, response: Response<ResultObject>) {
                val result = response.body()

                if (!TextUtils.isEmpty(result.success)) {
                    Toast.makeText(activity, "인증영상 업로드 완료", Toast.LENGTH_LONG).show()
                }

                // Remove video from my storage.
                forRemoveFile = File(pathToStoredVideo)
                forRemoveFile?.delete()
                var resolver: ContentResolver? = activity.contentResolver
                resolver?.delete(uri, null, null)

            }

            override fun onFailure(call: Call<ResultObject>, t: Throwable) {
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
        if(joinState == 3)
            calendar!!.visibility = View.GONE
        else
            calendar!!.visibility = View.VISIBLE

        calendar!!.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(startDate!!.getInt("year"),startDate!!.getInt("month")-1,startDate!!.getInt("day")))
                .setMaximumDate(CalendarDay.from(endDate!!.getInt("year"),endDate!!.getInt("month")-1,endDate!!.getInt("day")))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        calendar!!.addDecorators(
                SundayDecorator(),
                SaturdayDecorator(),
                OneDayDecorator())

//        val calendar2 = Calendar.getInstance()
//        calendar2.add(Calendar.MONTH, -2)

        val successDates = mutableListOf<CalendarDay>()
        val failDates = mutableListOf<CalendarDay>()
        val notYetDates = mutableListOf<CalendarDay>()

        val sDate = mutableListOf<CalendarDay>()
        val eDate = mutableListOf<CalendarDay>()


        sDate.add(CalendarDay.from(startDate!!.getInt("year"),startDate!!.getInt("month")-1,startDate!!.getInt("day")))
        eDate.add(CalendarDay.from(endDate!!.getInt("year"),endDate!!.getInt("month")-1,endDate!!.getInt("day")))

        for (i in 0..(jsonArray.length() - 1)) {

            var y = jsonArray.getJSONObject(i).getString("year").toInt()
            var m = jsonArray.getJSONObject(i).getString("month").toInt()-1
            var d = jsonArray.getJSONObject(i).getString("day").toInt()

            var day = CalendarDay.from(y,m,d)

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

//            calendar2.add(Calendar.DATE, 5) // 5가 뭐지?
        }


        calendar!!.setOnDateChangedListener(OnDateSelectedListener { widget, date, selected -> Log.e("DAY", "DAY:$date") })
        calendar!!.addDecorators(EventDecorator(Color.RED, successDates, activity, "success"))
        calendar!!.addDecorators(EventDecorator(Color.RED, failDates, activity, "fail"))
        calendar!!.addDecorators(EventDecorator(Color.RED, notYetDates, activity, "notYet"))

        calendar!!.addDecorators(EventDecorator(Color.RED, sDate, activity, "startDate"))
        calendar!!.addDecorators(EventDecorator(Color.RED, eDate, activity, "endDate"))
    }


}

