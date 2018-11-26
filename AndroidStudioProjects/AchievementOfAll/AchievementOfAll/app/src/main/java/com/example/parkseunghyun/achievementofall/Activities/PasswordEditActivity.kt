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



class PasswordEditActivity : AppCompatActivity() {

    // jwt-token
    var jwtToken: String?= null

    var name: String ?= null
    var phoneNumber: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_edit)

        name = intent.getStringExtra("name")
        phoneNumber = intent.getStringExtra("phoneNumber")

        bt_editPassword.setOnClickListener{
            println("앙기모")
            jwtToken = loadToken()
            if(edit_password.text.toString()!= edit_passwordCheck.text.toString()){
                Toast.makeText(this, "비밀번호 체크가 틀립니다.", Toast.LENGTH_LONG).show()
            }else{
                editPassword()

            }
        }

        goInfoEdit.setOnClickListener {
            startActivity<ProfileEditActivity>(
                    "name" to name,
                    "phoneNumber" to phoneNumber
            )
            finish()
        }

    }

    private fun editPassword(){

        val pwCurrent = edit_passwordCurrent.text.toString()
        val pw = edit_password.text.toString()
        val token = jwtToken

        val jsonObject = JSONObject()

        jsonObject.put("token", token)
        jsonObject.put("passwordCurrent", pwCurrent)
        jsonObject.put("password",pw)

       VolleyHttpService.editPassword(this, jsonObject){ success ->

           // 성공 1 실패(현재비번틀릴때) 0 나머지 2
           println("비번바뀌나"+success)
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
    private fun loadToken(): String{
        var auto = PreferenceManager.getDefaultSharedPreferences(this)

        return auto.getString("token", "")
    }
}
