package com.example.parkseunghyun.achievementofall

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 회원가입 직후 화면 넘어감
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

        // 자동 로그인 체크
        if(isChecked == true){
            if(email!="0" && password !="0")
            login(email, password, isChecked)
        }

        // 로그인 버튼 리스너
        bt_login.setOnClickListener{
            email = text_email.text.toString()
            password = text_password.text.toString()
            isChecked = autoLogin.isChecked

            login(email, password, isChecked)
        }

        // 회원가입 화면 버튼 리스너
        goSignup.setOnClickListener {
            startActivity<SignupActivity>()
        }

    }

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

    private fun login(email: String, password: String, isChecked: Boolean){

        val jsonObject = JSONObject()
        jsonObject.put("email", email)
        jsonObject.put("password",password)

        println("isChecked: "+isChecked)

        VolleyHttpService.login(this, jsonObject){ success ->
            if(success){
                saveData(email,password,isChecked)
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_LONG).show()
                startActivity<ContentsHomeActivity>()
            }else{
                saveData("0","0",isChecked)
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_LONG).show()
            }
        }
    }
}


