package com.example.parkseunghyun.achievementofall.Activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R
import org.json.JSONObject

// ReportActivity
// 신고 화면
class ReportActivity : AppCompatActivity(){

    var ownIntent: Intent?=null
    var rejectTextView: TextView?=null
    var failedContentTextView: TextView?=null
    var acceptButton:Button? = null
    var reportButton:Button? = null

    var reportReason:EditText? = null

    var reportUserSplit:String?=null
    var reportReasonSplit:String?=null

    var reportView: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporting)

        ownIntent = intent

        rejectTextView = findViewById(R.id.rejectView)
        failedContentTextView = findViewById(R.id.fail_which_content)

        acceptButton = findViewById(R.id.bt_accept)
        reportButton = findViewById(R.id.bt_report)

        reportReason = findViewById(R.id.report_reason)

        val contentName = ownIntent!!.getStringExtra("contentName")
        val rejectUserArray = ownIntent!!.getStringExtra("rejectUserArray")
        val rejectReasonArray = ownIntent!!.getStringExtra("rejectReasonArray")

        failedContentTextView!!.text = contentName.toString()


        reportUserSplit = rejectUserArray.replace("[","")
        reportUserSplit = reportUserSplit!!.replace("]", "")

        reportReasonSplit = rejectReasonArray.replace("[","")
        reportReasonSplit = reportReasonSplit!!.replace("]","")


        var reportUserList = reportUserSplit!!.split(",")
        var reportReasonList = reportReasonSplit!!.split(",")


        // 실패 사유와 인증 사용자들을 보여줍니다.
        for(index in reportUserList.indices) {

            val idString = reportUserList.get(index).replace("\"", "").split("@")

            if( reportUserList.get(index).length < 4 ) {

                reportView += idString[0].replaceRange(0, reportUserList.get(index).length - 1, "**") + "@" + idString[1]

            } else {

                reportView += idString[0].replaceRange(1, 3, "***") + "@" + idString[1]

            }
            reportView += " 님 \n : "
            reportView += reportReasonList.get(index)
            reportView += "\n\n"

        }

        rejectTextView!!.setMovementMethod(ScrollingMovementMethod())
        rejectTextView!!.text = reportView

        // 실패 승인 버튼을 누를 경우
        acceptButton!!.setOnClickListener {

            val jsonObject = JSONObject()
            jsonObject.put("contentName", contentName)
            jsonObject.put("token",loadToken())

            //TODO 여기서 서버통신
            VolleyHttpService.failAccept(this, jsonObject) { success ->
                if (success.get("success") == true) {
                    Toast.makeText(this, "승인 완료", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "서버통신 실패", Toast.LENGTH_LONG).show()
                }
            }
        }

        // 신고 버튼을 누를 경우
        reportButton!!.setOnClickListener{
            if (reportReason!!.text.toString().replace(" ","").equals("")) {
                Toast.makeText(this, "신고 사유를 적어주셔야 합니다.", Toast.LENGTH_LONG).show()
            }
            else{
                //TODO 여기서 서버통신
                val jsonObject = JSONObject()
                jsonObject.put("contentName", contentName)
                jsonObject.put("token",loadToken())
                jsonObject.put("reportReason", reportReason!!.text.toString())

                VolleyHttpService.failReport(this, jsonObject) { success ->
                    if (success.get("success") == true) {
                        Toast.makeText(this, "신고가 완료되었습니다.\n심사결과가 나올 때 까지\n실패가 보류됩니다.", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this, "서버 통신 실패", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    // loadToken
    // JWT 토큰을 SharedPreference에서 불러옵니다.
    private fun loadToken(): String{
        var auto = PreferenceManager.getDefaultSharedPreferences(this)

        return auto.getString("token", "")
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }

}