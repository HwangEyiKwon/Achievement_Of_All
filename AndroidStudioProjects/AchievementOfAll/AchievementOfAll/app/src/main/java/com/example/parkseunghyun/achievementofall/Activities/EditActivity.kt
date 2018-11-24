package com.example.parkseunghyun.achievementofall.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R
import kotlinx.android.synthetic.main.activity_edit.*
import org.json.JSONObject


class EditActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        bt_edit.setOnClickListener{
            println("앙기모")

            if(edit_password.text.toString() == edit_passwordCheck.text.toString()){

                edit()
                finish()

            }else{
                Toast.makeText(this,"비밀번호 체크가 틀립니다.", Toast.LENGTH_SHORT).show();
            }
        }


    }
    private fun edit(){

        val password = edit_password.text.toString()
        val name = edit_nickname.text.toString()
        val phoneNumber = edit_phone_number.text.toString()

        val jsonObject = JSONObject()

        jsonObject.put("password",password)
        jsonObject.put("name", name)
        jsonObject.put("phoneNumber", phoneNumber)

       VolleyHttpService.edit(this, jsonObject){success ->

           println(success)
       }
    }
}