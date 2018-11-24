package com.example.parkseunghyun.achievementofall.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R
import kotlinx.android.synthetic.main.activity_signup.*
import org.json.JSONObject


class SignupActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)


        bt_signup.setOnClickListener{
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(signup_email.text).matches())
            {
                Toast.makeText(this,"이메일 형식이 아닙니다. \n Modal@gmail.com",Toast.LENGTH_SHORT).show();

            }else if(signup_password.text.toString() != signup_password_check.text.toString()){
                println(signup_password.text)
                println(signup_password_check.text)
                Toast.makeText(this,"비밀번호 체크가 틀립니다.",Toast.LENGTH_SHORT).show();
            }else{
                signup()
            }

        }


    }
    private fun signup(){

        val email = signup_email.text.toString()
        val password = signup_password.text.toString()
        val name = signup_nickname.text.toString()
        val phoneNumber = signup_phone_number.text.toString()
        val jsonObject = JSONObject()

        jsonObject.put("email", email)
        jsonObject.put("password",password)
        jsonObject.put("name", name)
        jsonObject.put("phoneNumber", phoneNumber)

        VolleyHttpService.signup(this, jsonObject) { success ->
            if (success) {
                Toast.makeText(this, "회원가입 성공!", Toast.LENGTH_LONG).show()
//                startActivity<LoginActivity>(
//                        "email" to email
//                )
                val goToLogin = Intent(applicationContext, LoginActivity::class.java)
                goToLogin.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                goToLogin.putExtra("email", email)
                startActivity(goToLogin)
            } else {
                Toast.makeText(this, "회원가입 실패!", Toast.LENGTH_LONG).show()
            }

        }
    }
}
