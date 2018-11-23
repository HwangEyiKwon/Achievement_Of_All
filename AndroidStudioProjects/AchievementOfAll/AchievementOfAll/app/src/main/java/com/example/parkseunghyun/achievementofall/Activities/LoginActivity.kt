package com.example.parkseunghyun.achievementofall.Activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.parkseunghyun.achievementofall.Configurations.MyFirebaseInstanceIDService
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 회원가입 직후 로그인 화면 전환
        if(intent.getStringExtra("email")!=null){
            val afterSignup = intent.getStringExtra("email")
            text_email.setText(afterSignup)
        }

        // 로그인 버튼 리스너
        bt_login.setOnClickListener{

            var email = text_email.text.toString()
            var password = text_password.text.toString()
//            isChecked = autoLogin.isChecked

            // 로그인
            login(email, password)
        }
        goPassword.setOnClickListener {

            startActivity<ForgotPasswordActivity>()
            finish()

        }

    }

    // SharedPreferences
    private fun saveData(email: String, password: String){
        var auto = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = auto.edit()

        editor.putString("email", email)
                .putString("password",password)
                .apply()
    }


    // SharedPreferences (jwt-token)
    private fun saveToken(token: String){
        var auto = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = auto.edit()
        editor.putString("token", token)
                .apply()
    }



    // 로그인
    private fun login(email: String, password: String){

        val jsonObject = JSONObject()
        jsonObject.put("email", email)
        jsonObject.put("password",password)

        VolleyHttpService.login(this, jsonObject) { success ->
            if (success.get("success") == true) { // 로그인 성공

                val fcmService = MyFirebaseInstanceIDService()
                fcmService.onTokenRefresh()
                val jsonObject = fcmService.jsonObject as JSONObject

                jsonObject.put("email", email)

                sendToken(jsonObject)

                // 자동 로그인을 위한 정보 저장
                saveData(email, password)

                Toast.makeText(this, "로그인 성공", Toast.LENGTH_LONG).show()

                var jwt_token = success.getJSONObject("headers").get("token")

                // 토큰 저장
                saveToken(jwt_token.toString())

                val goToHome = Intent(applicationContext, HomeActivity::class.java)
//                goToHome.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                startActivity(goToHome)

//                startActivity<HomeActivity>()
//                finish()


            } else { // 로그인 실패
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun sendToken(jsonObject: JSONObject){

        VolleyHttpService.sendToken(this, jsonObject) { success ->
            if (success) {
                Toast.makeText(this, "FCM 토큰 성공", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "FCM 토큰 실패", Toast.LENGTH_LONG).show()
            }
        }
    }
}


