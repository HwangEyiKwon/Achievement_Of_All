package com.example.parkseunghyun.achievementofall.Activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Patterns
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

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initButtonListener()

        if(intent.getStringExtra("email") != null){

            val afterSignup = intent.getStringExtra("email")
            user_email_to_find_pw.setText(afterSignup)

        }

    }

    private fun initButtonListener() {
        button_login.setOnClickListener {

            val userEmail = user_email_to_find_pw.text.toString()
            val userPW = edit_text_user_pw.text.toString()

            if (!Patterns.EMAIL_ADDRESS.matcher(user_email_to_find_pw.text).matches()) {

                Toast.makeText(this, "이메일 형식이 아닙니다. \n Modal@gmail.com", Toast.LENGTH_SHORT).show()

            } else {

                loginRequest(userEmail, userPW)

            }
        }

        button_find_password.setOnClickListener {

            startActivity<ForgotPasswordActivity>()
            finish()

        }
    }

    private fun saveDataForAutoLogin(email: String, password: String){

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val editorForSaveLoginData = sharedPref.edit()

        editorForSaveLoginData
                .putString("email", email)
                .putString("password",password)
                .apply()

    }

    private fun saveJwtToken(token: String){

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val editorForSaveJwtToken = sharedPref.edit()
        editorForSaveJwtToken
                .putString("token", token)
                .apply()

    }

    private fun loginRequest(email: String, password: String){

        val jsonObject = JSONObject()
        jsonObject.put("email", email)
        jsonObject.put("password",password)

        VolleyHttpService.login(this, jsonObject) { success ->

            if (success.get("success") == true) {

                val fcmService = FirebaseInstanceIDService()
                fcmService.onTokenRefresh()

                val jsonObjectForFCM = fcmService.jsonObjectForRefreshFCM as JSONObject
                jsonObjectForFCM.put("email", email)
                sendFCMToken(jsonObjectForFCM)

                saveDataForAutoLogin(email, password)

                val jwtToken = success.getJSONObject("headers").get("token")
                saveJwtToken(jwtToken.toString())
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_LONG).show()

                val goToHome = Intent(applicationContext, HomeActivity::class.java)
                startActivity(goToHome)

            } else {

                Toast.makeText(this, "로그인 실패", Toast.LENGTH_LONG).show()

            }
        }
    }

    private fun sendFCMToken(jsonObject: JSONObject){

        VolleyHttpService.sendToken(this, jsonObject) { success ->

            if (success) {

            } else {

            }

        }

    }
}


