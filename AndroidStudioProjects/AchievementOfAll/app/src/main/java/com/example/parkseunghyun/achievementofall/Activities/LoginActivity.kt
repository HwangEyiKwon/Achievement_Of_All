package com.example.parkseunghyun.achievementofall.Activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.MotionEvent
import android.widget.Toast
import com.example.parkseunghyun.achievementofall.Configurations.FirebaseInstanceIDService
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject

/**
    REFACTORED.
 */

// LoginActivity
// 로그인 화면
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 초기화
        initButtonListener()

    }

    // initButtonListener
    // 버튼이 눌렸을 때 작동합니다.
    private fun initButtonListener() {

        button_login.setOnClickListener {

            val userEmail = user_email_to_login.text.toString()
            val userPW = edit_text_user_pw.text.toString()

            // 이메일 형식을 확인합니다.
            if (!Patterns.EMAIL_ADDRESS.matcher(user_email_to_login.text).matches()) {

                Toast.makeText(this, "이메일 형식이 아닙니다. \n Modal@gmail.com", Toast.LENGTH_SHORT).show()

            } else {

                // 로그인 요청
                loginRequest(userEmail, userPW)

            }
        }

        // 비밀번호 찾기 버튼을 누를 경우
        button_find_password.setOnClickListener {

            startActivity<ForgotPasswordActivity>()
            finish()

        }

        // 이메일 입력란의 입력 값에 따라 알림을 띄웁니다.
        user_email_to_login!!.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {

                login_info_text.text = ""

                if (editable.toString().replace(" ","").equals("")) {

                    login_info_text.text = "Email을 입력해주세요."

                } else {

                    if (Patterns.EMAIL_ADDRESS.matcher(user_email_to_login.text).matches()) {

                        login_info_text.text = ""

                    } else {

                        login_info_text.setTextColor(resources.getColor(R.color.colorAccent))
                        login_info_text.text = "이메일 형식에 맞게 입력 해주세요 ..."

                    }

                }

            }
        })

    }

    // saveDataForAutoLogin
    // 받아온 데이터를 SharedPreference에 저장합니다.
    private fun saveDataForAutoLogin(email: String, password: String){

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val editorForSaveLoginData = sharedPref.edit()

        editorForSaveLoginData
                .putString("email", email)
                .putString("password",password)
                .apply()

    }

    // saveJwtToken
    // JWT 토큰을 SharedPreference에 저장합니다.
    private fun saveJwtToken(token: String){

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val editorForSaveJwtToken = sharedPref.edit()
        editorForSaveJwtToken
                .putString("token", token)
                .apply()

    }

    // loginRequest
    // 입력란에서 받아온 데이터로 로그인을 진행합니다.
    private fun loginRequest(email: String, password: String){

        val jsonObjectForLogin = JSONObject()
        jsonObjectForLogin.put("email", email)
        jsonObjectForLogin.put("password",password)

        VolleyHttpService.login(this, jsonObjectForLogin) { success ->

            if (success.get("success") == true) {

                // 새로운 FCM 토큰을 발급받습니다.
                val fcmService = FirebaseInstanceIDService()
                fcmService.onTokenRefresh()

                val jsonObjectForFCM = fcmService.jsonObjectForRefreshFCM as JSONObject
                jsonObjectForFCM.put("email", email)
                sendFCMToken(jsonObjectForFCM)

                saveDataForAutoLogin(email, password)

                // 새로운 JWT 토큰을 발급받습니다.
                val jwtToken = success.getJSONObject("headers").get("token")
                saveJwtToken(jwtToken.toString())
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_LONG).show()

                // 로그인에 성공하여 HomeActivity로 이동합니다.
                val goToHome = Intent(applicationContext, HomeActivity::class.java)
                startActivity(goToHome)

            } else {

                Toast.makeText(this, "로그인 실패", Toast.LENGTH_LONG).show()

            }
        }
    }

    // sendFCMToken
    // FCM Token을 전송합니다.
    private fun sendFCMToken(jsonObject: JSONObject){

        VolleyHttpService.sendToken(this, jsonObject) { success ->

            if (success) {

            } else {
                Toast.makeText(this, "FCM 토큰 전송 실패", Toast.LENGTH_SHORT).show()
            }

        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }
}


