package com.example.parkseunghyun.achievementofall.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.example.parkseunghyun.achievementofall.R

class RewardActivity : AppCompatActivity() {

    var jwtToken: String ?= null
    var contentName: String ?= null

    var currentMoney: TextView ?= null
    var rewardMoney: TextView ?= null
    var finalMoney: TextView ?= null

    var cm: Int ?= null
    var rm: Int ?= null
    var fm: Int ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward)


        currentMoney = findViewById(R.id.current_money)
        rewardMoney = findViewById(R.id.final_reward)
        finalMoney = findViewById(R.id.final_money)

        cm = intent.getIntExtra("currentMoney",0)
        rm = intent.getIntExtra("rewardMoney",0)
        fm = cm!!+rm!!

        currentMoney!!.text = cm.toString()
        rewardMoney!!.text = rm.toString()
        finalMoney!!.text = fm.toString()


    }

}