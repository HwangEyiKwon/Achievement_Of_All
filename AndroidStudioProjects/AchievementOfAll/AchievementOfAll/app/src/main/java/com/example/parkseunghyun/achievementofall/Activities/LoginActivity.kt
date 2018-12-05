package com.example.parkseunghyun.achievementofall.Activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Patterns
import android.widget.Toast
import com.example.parkseunghyun.achievementofall.Configurations.MyFirebaseInstanceIDService
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject

/*
    REFACTORED.
 */

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initButtonListener()

        // 회원가입 직후 로그인 화면 전환
        if(intent.getStringExtra("email") != null){
            val afterSignup = intent.getStringExtra("email")
            user_email_to_find_pw.setText(afterSignup)
        }

    }

    private fun initButtonListener() {
        button_login.setOnClickListener {

            val userEmail = user_email_to_find_pw.text.toString()
            val userPW = edit_text_user_pw.text.toString()

            // 로그인
            if (!Patterns.EMAIL_ADDRESS.matcher(user_email_to_find_pw.text).matches()) {
                Toast.makeText(this, "이메일 형식이 아닙니다. \n Modal@gmail.com", Toast.LENGTH_SHORT).show();
            } else {
                loginRequest(userEmail, userPW)
            }
        }

        button_find_password.setOnClickListener {

            startActivity<ForgotPasswordActivity>()
            finish()

        }
    }

    // SharedPreferences
    private fun saveDataForAutoLogin(email: String, password: String){

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val editorForSaveLoginData = sharedPref.edit()

        editorForSaveLoginData
                .putString("email", email)
                .putString("password",password)
                .apply()

    }

    // SharedPreferences (jwt-token)
    private fun saveJwtToken(token: String){

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val editorForSaveJwtToken = sharedPref.edit()
        editorForSaveJwtToken
                .putString("token", token)
                .apply()

    }

    // 로그인
    private fun loginRequest(email: String, password: String){

        val jsonObject = JSONObject()
        jsonObject.put("email", email)
        jsonObject.put("password",password)

        VolleyHttpService.login(this, jsonObject) { success ->
            if (success.get("success") == true) { // 로그인 성공

                val fcmService = MyFirebaseInstanceIDService()
                fcmService.onTokenRefresh()

                val jsonObjectForFCM = fcmService.jsonObject as JSONObject
                jsonObjectForFCM.put("email", email)
                sendFCMToken(jsonObjectForFCM)

                // 자동 로그인을 위한 정보 저장
                saveDataForAutoLogin(email, password)

                val jwtToken = success.getJSONObject("headers").get("token")
                saveJwtToken(jwtToken.toString())
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_LONG).show()

                val goToHome = Intent(applicationContext, HomeActivity::class.java)
                startActivity(goToHome)

            } else { // 로그인 실패
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendFCMToken(jsonObject: JSONObject){

        VolleyHttpService.sendToken(this, jsonObject) { success ->
            if (success) {
//                Toast.makeText(this, "FCM 토큰 성공", Toast.LENGTH_LONG).show()
            } else {
//                Toast.makeText(this, "FCM 토큰 실패", Toast.LENGTH_LONG).show()
            }
        }

    }
}


