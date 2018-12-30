package com.example.parkseunghyun.achievementofall.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import com.example.parkseunghyun.achievementofall.R

// RewardActivity
// 보상 화면
// 달성에 성공하여 추후 받게될 보상을 보여줍니다.
class RewardActivity : AppCompatActivity() {

    var jwtToken: String ?= null
    var contentName: String ?= null

    var currentMoney: TextView ?= null
    var rewardMoney: TextView ?= null
    var finalMoney: TextView ?= null

    var rewardConfirmButton: Button?= null

    var cm: Int ?= null
    var rm: Int ?= null
    var fm: Int ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_success)

        currentMoney = findViewById(R.id.current_money)
        rewardMoney = findViewById(R.id.final_penalty)
        finalMoney = findViewById(R.id.final_money)

        rewardConfirmButton = findViewById(R.id.reward_confirm_button)

        // cm = 현재 보유
        // rm = 보상
        // fm = 최종 반환 금액
        cm = intent.getIntExtra("currentMoney",0)
        rm = intent.getIntExtra("penaltyMoney",0)
        fm = cm!! + rm!!

        currentMoney!!.text = cm.toString()
        rewardMoney!!.text = rm.toString()
        finalMoney!!.text = fm.toString()

        rewardConfirmButton!!.setOnClickListener {
            finish()
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }

}