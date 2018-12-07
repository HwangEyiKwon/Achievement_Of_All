package com.example.parkseunghyun.achievementofall.Activities

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R
import kotlinx.android.synthetic.main.activity_password_edit.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject

/*
    REFACTORED.
 */

class PasswordEditActivity : AppCompatActivity() {

    private var jwtToken: String?= null
    private var userName: String ?= null
    private var phoneNumber: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_edit)

        initButtonListener()

        userName = intent.getStringExtra("name")
        phoneNumber = intent.getStringExtra("phoneNumber")


    }

    private fun initButtonListener() {

        button_edit_pw.setOnClickListener {

            jwtToken = loadJwtToken()

            if (new_pw.text.toString() != new_pw_check.text.toString()) {

                Toast.makeText(this, "비밀번호 체크가 틀립니다.", Toast.LENGTH_LONG).show()

            } else {

                editPasswordRequest()

            }
        }

        button_go_Info_edit.setOnClickListener {
            startActivity<ProfileEditActivity>(
                    "name" to userName,
                    "phoneNumber" to phoneNumber
            )
            finish()
        }
    }

    private fun editPasswordRequest(){

        val pwCurrent = current_pw.text.toString()
        val pw = new_pw.text.toString()
        val token = jwtToken

        val jsonObject = JSONObject()

        jsonObject.put("token", token)
        jsonObject.put("passwordCurrent", pwCurrent)
        jsonObject.put("password",pw)

       VolleyHttpService.editPassword(this, jsonObject){ success ->

           // 성공 1 실패(현재비번틀릴때) 0 나머지 2
           when(success.getInt("success")){
               0->{
                   Toast.makeText(this, "현재 비밀번호가 틀립니다.", Toast.LENGTH_LONG).show()
               }
               1->{
                   Toast.makeText(this, "수정 완료.", Toast.LENGTH_LONG).show()
                   finish()
               }
               2->{
                   Toast.makeText(this, "ERROR.", Toast.LENGTH_LONG).show()
               }
           }

       }
    }
    private fun loadJwtToken(): String{
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPref.getString("token", "")
    }
}
