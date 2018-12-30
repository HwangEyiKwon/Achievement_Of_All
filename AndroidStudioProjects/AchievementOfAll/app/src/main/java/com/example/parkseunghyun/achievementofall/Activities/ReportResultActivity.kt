package com.example.parkseunghyun.achievementofall.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import com.example.parkseunghyun.achievementofall.Configurations.RequestCodeCollection
import com.example.parkseunghyun.achievementofall.R


// ReportResultActivity
// 신고 결과 화면
// 관리자가 처리한 신고 결과를 보여줍니다.
class ReportResultActivity : AppCompatActivity(){

    var ownIntent: Intent?=null

    private var resultView: TextView? = null
    private var resultView2: TextView? = null
    private var contentNameView: TextView? = null
    private var reasonView: TextView? = null
    private var confirmButton: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_result)

        ownIntent = intent

        val contentName = ownIntent!!.getStringExtra("contentName")
        val reason = ownIntent!!.getStringExtra("reason")
        val result = ownIntent!!.getStringExtra("result")

        resultView = findViewById(R.id.result_view1)
        resultView2 = findViewById(R.id.result_view2)
        contentNameView = findViewById(R.id.content_name)
        reasonView = findViewById(R.id.reasonView)
        confirmButton = findViewById(R.id.bt_accept)

        resultView!!.text = result
        contentNameView!!.text = contentName

        resultView2!!.setMovementMethod(ScrollingMovementMethod())
        reasonView!!.setMovementMethod(ScrollingMovementMethod())

        // 결과에 따라 다른 화면을 보여줍니다.
        when (result) {

            "success" -> {

                resultView!!.text = "신고가 승인되었습니다!"
                resultView2!!.text = "관리자의 확인 결과,\n접수하신 신고요청이 승인되었습니다.\n따라서 컨텐츠를 다시 정상적으로 진행하실 수 있으며,\n인증하신 영상이 성공으로 처리되었습니다.\n신고된 유저들에게는 패널티를 부여할 예정입니다."
                reasonView!!.text = reason.replace("\"", "").replace("[", "").replace("]", "")
                confirmButton!!.text = "CONFIRM"

            }

            "fail" -> {

                resultView!!.text = "신고가 거절되었습니다!"
                resultView2!!.text = "관리자의 확인 결과,\n회원님의 영상은 실패로 처리되었습니다.\n따라서 접수하신 신고요청이 거절되어\n목표 달성 실패로 처리되었습니다."
                reasonView!!.text = reason.replace("\"", "").replace("[", "").replace("]", "")
                confirmButton!!.text = "PENALTY 확인"

            }

        }

        // 승인할 경우
        confirmButton!!.setOnClickListener {

            if(result.equals("success")) {

                finish()

            } else {

                val goToReportResult = Intent(this, PenaltyActivity::class.java)
                goToReportResult.putExtra("contentName", contentName)
                startActivityForResult(goToReportResult, RequestCodeCollection.REQUEST_RETURN_FROM_CONTENT_PENALTY)

            }

        }


    }

    // onActivityResult
    // 호출됬던 Acitivty가 끝나면 작동합니다.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if ( requestCode == RequestCodeCollection.REQUEST_RETURN_FROM_CONTENT_PENALTY ) {

            finish()

        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }

}