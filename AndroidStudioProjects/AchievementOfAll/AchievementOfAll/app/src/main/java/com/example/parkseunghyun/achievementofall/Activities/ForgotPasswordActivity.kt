package com.example.parkseunghyun.achievementofall.Activities

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Patterns
import android.widget.Toast
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject

/*
    REFACTORED.
 */


class ForgotPasswordActivity : AppCompatActivity() {

    private var userEmail: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        initButtonListener()

    }

    private fun initButtonListener() {
        button_to_send_email.setOnClickListener {

            if (!Patterns.EMAIL_ADDRESS.matcher(user_email_to_find_pw.text).matches()) {

                Toast.makeText(this, "이메일 형식이 아닙니다. \n Modal@gmail.com", Toast.LENGTH_SHORT).show();

            } else {

                userEmail = user_email_to_find_pw.text.toString()
                sendEmailForFindingPW()

            }
        }

        button_goto_login.setOnClickListener {

            startActivity<LoginActivity>()
            finish()

        }
    }

    private fun sendEmailForFindingPW(){

        val jsonObjectToFindPW = JSONObject()
        jsonObjectToFindPW.put("email", userEmail)

        VolleyHttpService.sendMail(this, jsonObjectToFindPW) { success ->

            if(success.getBoolean("success")){
                Toast.makeText(this,"이메일이 발송되었습니다. \n 확인해주세요.",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"이메일이 발송 실패하였습니다. \n 다시 시도 해주세요.",Toast.LENGTH_SHORT).show();
            }

        }
    }


}


