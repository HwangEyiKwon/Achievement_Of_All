package com.example.parkseunghyun.achievementofall

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import com.example.parkseunghyun.achievementofall.Configurations.GlideLoadingFlag
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import org.json.JSONObject

/**
    REFACTORED
 */

// ConfirmJoinActivity
// 컨텐츠 참여에 필요한 화면
class ConfirmJoinActivity : AppCompatActivity() {

    private var pickedDateTextView: TextView? = null

    private var selectedYear: Int? = null
    private var selectedMonthOfYear: Int? = null
    private var selectedDayOfMonth: Int? = null

    private var agreeCheckBox: CheckBox? = null
    private var ruleView: TextView? = null

    private var content: String? = null
    private var jwtToken: String? = null
    private var confirmJoinButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirming_join)

        ruleView = findViewById(R.id.id_content_rule)
        agreeCheckBox = findViewById(R.id.checkBox)
        confirmJoinButton = findViewById(R.id.confirm_join_button)

        confirmJoinButton!!.isEnabled = false

        // 참가 동의란을 누를 경우
        agreeCheckBox!!.setOnClickListener {

            if ( agreeCheckBox!!.isChecked ) {

                confirmJoinButton!!.isEnabled = true

            } else if ( ! agreeCheckBox!!.isChecked ) {

                confirmJoinButton!!.isEnabled = false

            }

        }

        selectedYear = intent.extras.getInt("selectedYear")
        selectedMonthOfYear = intent.extras.getInt("selectedMonthOfYear")
        selectedDayOfMonth = intent.extras.getInt("selectedDayOfMonth")
        content = intent.extras.getString("contentName")
        jwtToken = intent.extras.getString("token")

        val jsonObjectForGetRule = JSONObject()
        jsonObjectForGetRule.put("contentName", content)
        jsonObjectForGetRule.put("startYear", selectedMonthOfYear.toString())
        jsonObjectForGetRule.put("startMonth", selectedDayOfMonth.toString())
        jsonObjectForGetRule.put("startDay",selectedYear.toString())

        // 컨텐츠 참가에 숙지해야할 규칙을 받아옵니다.
        VolleyHttpService.getContentRule(this, jsonObjectForGetRule) { success ->

            if (success.get("success") == true) {

                ruleView!!.text = success.getString("description")

            } else {

            }

        }

        val jsonObjectForConfirmJoin = JSONObject()
        jsonObjectForConfirmJoin.put("contentName", content)
        jsonObjectForConfirmJoin.put("token", jwtToken)
        jsonObjectForConfirmJoin.put("year", selectedYear!!)
        jsonObjectForConfirmJoin.put("month", selectedMonthOfYear!!)
        jsonObjectForConfirmJoin.put("day", selectedDayOfMonth!!)

        val selectedTime = "선택된 날짜는 $selectedYear / $selectedMonthOfYear / $selectedDayOfMonth 입니다."
        pickedDateTextView = findViewById(R.id.id_picked_date)
        pickedDateTextView?.text = selectedTime

        confirmJoinButton!!.setOnClickListener {

            joinComplete(jsonObjectForConfirmJoin)

        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }

    // joinComplete
    // 참가 완료할 경우
    private fun joinComplete(jsonObject: JSONObject) {

        VolleyHttpService.contentJoinComplete(this, jsonObject){ success ->

            if(success.getBoolean("success")){

                GlideLoadingFlag.setJoinedContentFlag(GlideLoadingFlag.FLAG_UPDATED)
                finish()

            } else {

            }

        }

    }

}
