package com.example.parkseunghyun.achievementofall

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import java.util.*
import android.widget.*
import android.widget.CheckBox




class ConfirmJoinActivity : AppCompatActivity() {

    var pickedDateTextView: TextView? = null
    var datepicker: DatePicker? = null
    val current_time = TimeZone.getTimeZone("Asia/Seoul")
    val calendar = GregorianCalendar(current_time)

    var selectedYear: Int? = null
    var selectedMonthOfYear: Int? = null
    var selectedDayOfMonth: Int? = null

    var areYouAgreeToJoin: CheckBox? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.acitivity_confirming_join)

        val confirmJoinButton = findViewById(R.id.confirm_join_button) as Button
        confirmJoinButton.isEnabled = false
        confirmJoinButton.setOnClickListener {

        }

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

        var selectedTime: String = "선택된 날짜는 " + selectedYear + " / " + selectedMonthOfYear + " / " + selectedDayOfMonth+ " 입니다."
        pickedDateTextView = findViewById(R.id.id_picked_date)
        pickedDateTextView?.setText(selectedTime)
        println(selectedTime)
    }
}
