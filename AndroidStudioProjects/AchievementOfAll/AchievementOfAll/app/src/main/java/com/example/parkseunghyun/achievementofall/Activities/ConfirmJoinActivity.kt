package com.example.parkseunghyun.achievementofall

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.TextView
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import org.json.JSONObject
import java.util.*


class ConfirmJoinActivity : AppCompatActivity() {

    var pickedDateTextView: TextView? = null
    var datepicker: DatePicker? = null
    val current_time = TimeZone.getTimeZone("Asia/Seoul")
    val calendar = GregorianCalendar(current_time)

    var selectedYear: Int? = null
    var selectedMonthOfYear: Int? = null
    var selectedDayOfMonth: Int? = null

    var areYouAgreeToJoin: CheckBox? = null

    var content: String?= null
    var jwtToken: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_confirming_join)


        val confirmJoinButton = findViewById(R.id.confirm_join_button) as Button
        confirmJoinButton.isEnabled = false


        areYouAgreeToJoin = findViewById(R.id.checkBox) as CheckBox
        areYouAgreeToJoin!!.setOnClickListener {
            if (areYouAgreeToJoin!!.isChecked) {
                confirmJoinButton.isEnabled = true
            }
            else {
                confirmJoinButton.isEnabled = false
            }
        }

        val intent = getIntent()

        selectedYear = intent.extras.getInt("selectedYear")
        selectedMonthOfYear = intent.extras.getInt("selectedMonthOfYear")
        selectedDayOfMonth = intent.extras.getInt("selectedDayOfMonth")
        content = intent.extras.getString("contentName")
        jwtToken = intent.extras.getString("token")

        val jsonObject = JSONObject()
        jsonObject.put("contentName", content)
        jsonObject.put("token", jwtToken)
        jsonObject.put("year", selectedYear!!)
        jsonObject.put("month", selectedMonthOfYear!!)
        jsonObject.put("day", selectedDayOfMonth!!)




        var selectedTime: String = "선택된 날짜는 " + selectedYear + " / " + selectedMonthOfYear + " / " + selectedDayOfMonth+ " 입니다."
        pickedDateTextView = findViewById(R.id.id_picked_date)
        pickedDateTextView?.setText(selectedTime)
        println(selectedTime)

        confirmJoinButton.setOnClickListener {

            joinComplete(jsonObject)

        }
    }
    private fun joinComplete(jsonObject: JSONObject) {

//        val contentHomeActivity = activ as ContentsHomeActivity
//
//        var activity: con
//        var ca = activity?.getContentsHomeActivity()
//        ca!!.finish()
//        finish()

        VolleyHttpService.contentJoinComplete(this, jsonObject){ success ->

            if(success.getBoolean("success")== true){
                println(success)
                finish()
            }else{
                println("????")
            }




//            var activity: ContentsHomeActivity ?= null
//            var ca = activity!!.getInstatnce()
//            ca.finish()
//            finish()


        }

    }
}
