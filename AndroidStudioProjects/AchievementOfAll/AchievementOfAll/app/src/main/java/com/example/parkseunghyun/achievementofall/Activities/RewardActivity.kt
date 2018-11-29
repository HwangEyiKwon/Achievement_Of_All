package com.example.parkseunghyun.achievementofall.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R
import org.json.JSONObject

class RewardActivity : AppCompatActivity() {

    var jwtToken: String ?= null
    var contentName: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward)

        jwtToken = intent.getStringExtra("token")
        contentName = intent.getStringExtra("contentName")

        rewardCheck ()

    }
    fun rewardCheck (){

        val jsonObject = JSONObject()
        jsonObject.put("token", jwtToken)
        jsonObject.put("contentName", contentName)

        VolleyHttpService.rewardCheck(this, jsonObject) { success ->

            println(success)
        }
    }


}