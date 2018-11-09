package com.example.parkseunghyun.achievementofall

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_signup.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject


class SignupActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        bt_signup.setOnClickListener{
            signup()
        }
        goLogin.setOnClickListener {
            startActivity<LoginActivity>()
        }

    }
    private fun signup(){

        val email = text_email2.text.toString()
        val password = text_password2.text.toString()
        val jsonObject = JSONObject()

        jsonObject.put("email", email)
        jsonObject.put("password",password)

        VolleyHttpService.signup(this, jsonObject){ success ->
            if(success){
                Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_LONG).show()
                startActivity<LoginActivity>(
                        "email" to email
                )

            }else{
                Toast.makeText(this, "회원가입 실패!", Toast.LENGTH_LONG).show()
            }

        }
    }
}
