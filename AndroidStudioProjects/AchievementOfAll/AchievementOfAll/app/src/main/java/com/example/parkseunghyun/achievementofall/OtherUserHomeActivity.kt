package com.example.parkseunghyun.achievementofall

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class OtherUserHomeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.other_home_layout)

        if(intent.getStringExtra("email")!=null){
            val otherUser = intent.getStringExtra("email")
//            text_email.setText(afterSignup)
            println(otherUser)
        }
    }

}