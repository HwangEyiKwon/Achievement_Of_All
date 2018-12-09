package com.example.parkseunghyun.achievementofall.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.example.parkseunghyun.achievementofall.R

class ContentSuccessActivity : AppCompatActivity() {

    var jwtToken: String ?= null
    var contentName: String ?= null
    var confirmButton: Button?= null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward)

        confirmButton = findViewById(R.id.reward_confirm_button)
        confirmButton!!.setOnClickListener {

            finish()

        }

    }

}