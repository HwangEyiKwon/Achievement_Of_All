package com.example.parkseunghyun.achievementofall

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    override fun onRestart() {
        super.onRestart()
        println("restart")

        // userCheck -> token 확인
        val jwtToken = JSONObject()
        jwtToken.put("token", loadToken())
        jwtCheck(jwtToken)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 회원가입 직후 로그인 화면 전환
        if(intent.getStringExtra("email")!=null){
            val afterSignup = intent.getStringExtra("email")
            text_email.setText(afterSignup)
            autoLogin.isChecked = false
        }

        // SharedPreference 에서 데이터 로드
        val jsonObject = loadData()
        var email = jsonObject.getString("email")
        var password = jsonObject.getString("password")
        var isChecked = jsonObject.getBoolean("isChecked")


        // userCheck -> token 확인
        val jwtToken = JSONObject()
        jwtToken.put("token", loadToken())
        jwtCheck(jwtToken)

        // 자동 로그인 체크 (수정 필요)
        if(isChecked == true){
            if(email!="0" && password !="0")
            login(email, password, isChecked)
        }

        // 로그인 버튼 리스너
        bt_login.setOnClickListener{

            email = text_email.text.toString()
            password = text_password.text.toString()
            isChecked = autoLogin.isChecked

            // 로그인
            login(email, password, isChecked)
        }

        // 회원가입 전환 버튼 리스너
        goSignup.setOnClickListener {
            startActivity<SignupActivity>()
        }

    }

    // SharedPreferences
    private fun saveData(email: String, password: String, isChecked: Boolean){
        var auto = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = auto.edit()

        editor.putString("email", email)
                .putString("password",password)
                .putBoolean("isChecked", isChecked)
                .apply()
    }
    private fun loadData(): JSONObject{
        var auto = PreferenceManager.getDefaultSharedPreferences(this)

        val jsonObject = JSONObject()
        var email = auto.getString("email","0")
        var password = auto.getString("password","0")
        var isChecked = auto.getBoolean("isChecked",false)

        jsonObject.put("email", email)
        jsonObject.put("password",password)
        jsonObject.put("isChecked", isChecked)

        return jsonObject
    }

    // SharedPreferences (jwt-token)
    private fun saveToken(token: String){
        var auto = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = auto.edit()
        editor.putString("token", token)
                .apply()
    }
    private fun loadToken(): String{
        var auto = PreferenceManager.getDefaultSharedPreferences(this)

        return auto.getString("token", "")
    }

    // jwt Check
    private fun jwtCheck(jsonObject: JSONObject){
        VolleyHttpService.jwtCheck(this, jsonObject){ success ->
            if(success){
                Toast.makeText(this, "jwt-token 존재", Toast.LENGTH_LONG).show()
                startActivity<HomeActivity>()
            }else{
                Toast.makeText(this, "jwt-token 없음", Toast.LENGTH_LONG).show()
            }
        }
    }
    // 로그인
    private fun login(email: String, password: String, isChecked: Boolean){

        val jsonObject = JSONObject()
        jsonObject.put("email", email)
        jsonObject.put("password",password)
        println("isChecked: "+isChecked)

        VolleyHttpService.login(this, jsonObject){ success ->
            if(success.get("success") == true){ // 로그인 성공

                val fcmService = MyFirebaseInstanceIDService()
                fcmService.onTokenRefresh()
                val jsonObject = fcmService.jsonObject as JSONObject

                jsonObject.put("email", email)
                sendToken(jsonObject)

                // 자동 로그인을 위한 정보 저장
                saveData(email,password,isChecked)

                Toast.makeText(this, "로그인 성공", Toast.LENGTH_LONG).show()

                var jwt_token = success.getJSONObject("headers").get("token")

                // 토큰 저장
                saveToken(jwt_token.toString())
                startActivity<HomeActivity>()

            }else{ // 로그인 실패
                saveData("0","0",isChecked)
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun sendToken(jsonObject: JSONObject){

        VolleyHttpService.sendToken(this, jsonObject){ success ->
            if(success){
                Toast.makeText(this, "FCM 토큰 성공", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, "FCM 토큰 실패", Toast.LENGTH_LONG).show()
            }
        }
    }
}


