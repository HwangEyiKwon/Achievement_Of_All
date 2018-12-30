package com.example.parkseunghyun.achievementofall.Activities

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R
import org.json.JSONObject

// PenaltyActivity
// 패널티 화면
class PenaltyActivity : AppCompatActivity() {

    var jwtToken: String ?= null
    var contentName: String ?= null

    var currentMoney: TextView ?= null
    var penaltyMoney: TextView ?= null
    var finalMoney: TextView ?= null

    var penaltyConfirmButton: Button?= null

    var cm: Int ?= null
    var rm: Int ?= null
    var fm: Int ?= null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_penalty)

        penaltyConfirmButton = findViewById(R.id.penalty_confirm_button)

        contentName = intent.getStringExtra("contentName")

        getCurrentMoney()

        currentMoney = findViewById(R.id.current_money)
        penaltyMoney = findViewById(R.id.final_penalty)
        finalMoney = findViewById(R.id.final_money)

        // 확인 버튼을 통해 패널티 화면을 종료합니다.
        penaltyConfirmButton!!.setOnClickListener {

            finish()

        }



    }

    // getCurrentMoney
    // 현재 보유 중인 돈을 불러옵니다.
    private fun getCurrentMoney(){

        val jsonObject = JSONObject()
        jsonObject.put("token", loadToken())
        jsonObject.put("contentName", contentName)


        VolleyHttpService.getContentMoney(this, jsonObject){ success ->

            rm = success.getInt("penalty")
            cm = success.getInt("penalty")
            fm = success.getInt("money")

            currentMoney!!.text = cm.toString() // 현재 보유
            penaltyMoney!!.text = rm.toString() // 패널티 = 현재 보유 (모두 잃었기 때문에)
            finalMoney!!.text = fm.toString() // 최종 반환 금액 = 0

        }
    }

    // loadToken
    // SharedPreference에서 JWT 토큰을 가져옵니다.
    private fun loadToken(): String{
        var auto = PreferenceManager.getDefaultSharedPreferences(this)

        return auto.getString("token", "")
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }

}