package com.example.parkseunghyun.achievementofall.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject

/**
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

            if (!Patterns.EMAIL_ADDRESS.matcher(user_email_to_login.text).matches()) {

                Toast.makeText(this, "이메일 형식이 아닙니다. \n Modal@gmail.com", Toast.LENGTH_SHORT).show();

            } else {

                userEmail = user_email_to_login.text.toString()
                sendEmailForFindingPW()

            }
        }

        button_goto_login.setOnClickListener {

            startActivity<LoginActivity>()
            finish()

        }
    }

    private fun sendEmailForFindingPW(){

        val loader = findViewById(R.id.loading_gif) as ImageView
        loader.visibility = View.VISIBLE
        button_to_send_email.visibility = View.GONE

        Glide
                .with(this)
                .load(R.drawable.giphy)
                .apply(RequestOptions().centerCrop())
                .into(loader)

        val jsonObjectToFindPW = JSONObject()
        jsonObjectToFindPW.put("email", userEmail)

        VolleyHttpService.sendMail(this, jsonObjectToFindPW) { success ->

            if(success.getBoolean("success")){

                Toast.makeText(this,"이메일이 발송되었습니다. \n 확인해주세요.",Toast.LENGTH_SHORT).show();
                loader.visibility = View.GONE
                finish()

            }else{

                Toast.makeText(this,"이메일이 발송 실패하였습니다. \n 다시 시도 해주세요.",Toast.LENGTH_SHORT).show();
                loader.visibility = View.GONE
                button_to_send_email.visibility = View.VISIBLE

            }

        }
    }


}


