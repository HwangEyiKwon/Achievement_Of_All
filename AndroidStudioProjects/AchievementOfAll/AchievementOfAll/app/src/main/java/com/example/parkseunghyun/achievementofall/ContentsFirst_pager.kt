package com.example.parkseunghyun.achievementofall

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
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
import android.net.Uri
import android.text.TextUtils
import android.widget.Toast
import java.io.File
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.*
import okhttp3.OkHttpClient
import java.io.IOException

class ContentsFirst_pager : Fragment(), EasyPermissions.PermissionCallbacks {

    private var mView: View? = null
    private var calendar: MaterialCalendarView? = null
    private var goToVideoButton: Button? = null

    // 사용자의 jwt-token
    private var jwtToken: String ?= null
    private var contentName: String? = null
    private var joinState: Int ?= null


    private val TAG = MainActivity::class.java.simpleName
    private val REQUEST_VIDEO_CAPTURE = 300
    private val READ_REQUEST_CODE = 200
    private var uri: Uri? = null
    private var pathToStoredVideo: String? = null

    private var globalVariables: GlobalVariables ?= GlobalVariables()
    private var ipAddress: String = globalVariables!!.ipAddress

    private var forRemoveFile: File? = null

    /**/
    private var tmpContentName = "NoSmoking"
    private var tmpMyEmail = "shp3@gmail.com"
    /**/



    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mView =  inflater!!.inflate(R.layout.contents_fragment_1, container, false)
        val contentHomeActivity = activity as ContentsHomeActivity
        jwtToken = contentHomeActivity.jwtToken.toString()
        contentName = contentHomeActivity.content.toString()
        joinState = contentHomeActivity.joinState

        println("캘랜더 페이지에서!!!!"+jwtToken+contentName)
        getCalendarInfo(jwtToken!!,contentName!!)

        goToVideoButton = mView?.findViewById(R.id.go_to_video_button)
        goToVideoButton?.setOnClickListener{
            val videoCaptureIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            if (videoCaptureIntent.resolveActivity(activity.packageManager) != null) {
                startActivityForResult(videoCaptureIntent, REQUEST_VIDEO_CAPTURE)
            }
        }
        println("join state"+joinState)
        if(joinState != 1){
            goToVideoButton!!.isEnabled = false
        }

        return mView

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_VIDEO_CAPTURE) {
            uri = data?.data
            if (EasyPermissions.hasPermissions(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                pathToStoredVideo = getRealPathFromURIPath(uri!!, activity)
                Log.d(TAG, "Recorded Video Path $pathToStoredVideo")

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
                Log.d(TAG, "Recorded Video Path $pathToStoredVideo")
                uploadVideoToServer(pathToStoredVideo!!)
            }
            if (EasyPermissions.hasPermissions(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Don't do anything
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        Log.d(TAG, "User has denied requested permission")
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
        Log.d(TAG, "Recorded Video Path $pathToVideoFile")
        Log.d(TAG, "Recorded Video is $videoFile")

        val videoBody = RequestBody.create(MediaType.parse("video/*"), videoFile)
        val vFile = MultipartBody.Part.createFormData("video", videoFile.name, videoBody)
        /**/

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(object:Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                val original = chain!!.request()
                val request = original.newBuilder()
                        .header("content_name", "${tmpContentName}")
                        .header("jwt_token", "${tmpMyEmail}")
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
                Log.d(TAG, "result is??? ---- $result")

                if (!TextUtils.isEmpty(result.success)) {
                    Toast.makeText(activity, "인증영상 업로드 완료", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "Result " + result.success)
                }

                // Remove video from my storage.
                forRemoveFile = File(pathToStoredVideo)
                forRemoveFile?.delete()
                var resolver: ContentResolver? = activity.contentResolver
                resolver?.delete(uri, null, null)

            }

            override fun onFailure(call: Call<ResultObject>, t: Throwable) {
                Log.d(TAG, "Error message ---- " + t.message)
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

        for (i in 0..(jsonArray.length() - 1)) {

            var y = jsonArray.getJSONObject(i).getString("year").toInt()
            var m = jsonArray.getJSONObject(i).getString("month").toInt()
            var d = jsonArray.getJSONObject(i).getString("day").toInt()

            var day = CalendarDay.from(y,m,d)
            dates.add(day)
            calendar2.add(Calendar.DATE, 5)
        }

        calendar!!.setOnDateChangedListener(OnDateSelectedListener { widget, date, selected -> Log.e("DAY", "DAY:$date") })
        calendar!!.addDecorators(EventDecorator(Color.RED, dates,activity))
    }


}

