package com.example.parkseunghyun.achievementofall.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R
import kotlinx.android.synthetic.main.activity_signup.*
import org.json.JSONObject
import java.util.regex.Pattern


class SignupActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)


        text_email2.setOnFocusChangeListener(object : View.OnFocusChangeListener {

            override fun onFocusChange(v: View, hasFocus: Boolean) {
                if (hasFocus) {
                    val p = Pattern.compile("^[a-zA-X0-9]@[a-zA-Z0-9].[a-zA-Z0-9]")
                    val m = p.matcher(text_email2.getText().toString())

                    if (!m.matches()) {
                        Toast.makeText(this@SignupActivity, "Email형식으로 입력하세요", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })


        bt_signup.setOnClickListener{
            signup()
        }
//        goLogin.setOnClickListener {
//            startActivity<LoginActivity>()
//        }

    }
    private fun signup(){

        val email = text_email2.text.toString()
        val password = text_password2.text.toString()
        val name = user_nickname.text.toString()
        val phoneNumber = user_phone_number.text.toString()
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
