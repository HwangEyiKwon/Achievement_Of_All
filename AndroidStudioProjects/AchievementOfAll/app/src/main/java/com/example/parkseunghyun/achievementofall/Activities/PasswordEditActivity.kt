package com.example.parkseunghyun.achievementofall.Activities

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.widget.Toast
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R
import kotlinx.android.synthetic.main.activity_password_edit.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject

/**
    REFACTORED.
 */

// PasswordEditActivity
// 프로필 수정(비밀번호 수정) 화면
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

    // initButtonListener
    // 버튼이 눌렸을 때 작동합니다.
    private fun initButtonListener() {

        // 수정 버튼을 누를 경우
        button_edit_pw.setOnClickListener {

            jwtToken = loadJwtToken()

            if (new_pw.text.toString() != new_pw_check.text.toString()) {

                Toast.makeText(this, "비밀번호 체크가 틀립니다.", Toast.LENGTH_LONG).show()

            } else {

                editPasswordRequest()

            }
        }

        // 사용자 정보 수정 버튼을 누를 경우
        button_go_Info_edit.setOnClickListener {

            startActivity<ProfileEditActivity>(

                    "name" to userName,
                    "phoneNumber" to phoneNumber

            )

            finish()

        }
    }

    // editPasswordRequest
    // 비밀번호 수정을 요청합니다.
    private fun editPasswordRequest(){

        val pwCurrent = current_pw.text.toString()
        val pw = new_pw.text.toString()
        val token = jwtToken

        val jsonObject = JSONObject()

        jsonObject.put("token", token)
        jsonObject.put("passwordCurrent", pwCurrent)
        jsonObject.put("password",pw)

       VolleyHttpService.editPassword(this, jsonObject){ success ->

           /** 실패(현재비번틀릴때) 0, 성공 1, 나머지 2 */
           when(success.getInt("success")){

               0 -> {

                   Toast.makeText(this, "현재 비밀번호가 틀립니다.", Toast.LENGTH_LONG).show()

               }

               1 -> {

                   Toast.makeText(this, "수정 완료.", Toast.LENGTH_LONG).show()
                   finish()

               }

               2 -> {

                   Toast.makeText(this, "ERROR.", Toast.LENGTH_LONG).show()

               }

           }

       }
    }

    // loadJWTToken
    // SharedPreference에서 JWT 토큰을 가져옵니다.
    private fun loadJwtToken(): String{

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPref.getString("token", "")

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }
}
