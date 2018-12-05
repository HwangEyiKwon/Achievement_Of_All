package com.example.parkseunghyun.achievementofall.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Patterns
import android.widget.Toast
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R
import kotlinx.android.synthetic.main.activity_signup.*
import org.json.JSONObject

/*
    REFACTORED.
 */

class SignupActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewComponents()


    }

    private fun initViewComponents() {
        setContentView(R.layout.activity_signup)

        button_signup.setOnClickListener {
            if (!Patterns.EMAIL_ADDRESS.matcher(signup_user_email.text).matches()) {

                Toast.makeText(this, "이메일 형식이 아닙니다. \n Modal@gmail.com", Toast.LENGTH_SHORT).show();

            } else if (signup_user_pw.text.toString() != signup_user_pw_check.text.toString()) {

                println(signup_user_pw.text)
                println(signup_user_pw_check.text)
                Toast.makeText(this, "비밀번호 체크가 틀립니다.", Toast.LENGTH_SHORT).show();

            } else {

                signUpRequest()

            }

        }
    }

    private fun signUpRequest(){

        val userEmail = signup_user_email.text.toString()
        val userPW = signup_user_pw.text.toString()
        val nickName = signup_user_nickname.text.toString()
        val phoneNumber = signup_phone_number.text.toString()
        val jsonObjectForSignUp = JSONObject()

        jsonObjectForSignUp.put("email", userEmail)
        jsonObjectForSignUp.put("password",userPW)
        jsonObjectForSignUp.put("name", nickName)
        jsonObjectForSignUp.put("phoneNumber", phoneNumber)

        VolleyHttpService.signup(this, jsonObjectForSignUp) { success ->
            if (success) {
                Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_LONG).show()

                val goToLogin = Intent(applicationContext, LoginActivity::class.java)
                goToLogin.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                goToLogin.putExtra("email", userEmail)

                startActivity(goToLogin)

            } else {
                Toast.makeText(this, "회원가입 실패!", Toast.LENGTH_LONG).show()
            }

        }
    }
}
