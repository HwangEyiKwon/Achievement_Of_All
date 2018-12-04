package com.example.parkseunghyun.achievementofall.Activities

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R
import org.json.JSONObject

class PenaltyActivity : AppCompatActivity() {

    var jwtToken: String ?= null
    var contentName: String ?= null

    var currentMoney: TextView ?= null
    var penaltyMoney: TextView ?= null
    var finalMoney: TextView ?= null

    var penaltyConfirmButton: Button?= null

    var cm: Int ?= null
    var rm: Int ?= null
    var fm: Int ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_penalty)

        penaltyConfirmButton = findViewById(R.id.penalty_confirm_button)

        contentName = intent.getStringExtra("contentName")

        println("")
        getCurrentMoney()


        currentMoney = findViewById(R.id.current_money)
        penaltyMoney = findViewById(R.id.final_penalty)
        finalMoney = findViewById(R.id.final_money)

        penaltyConfirmButton!!.setOnClickListener {
            finish()
        }



    }

    private fun getCurrentMoney(){

        val jsonObject = JSONObject()
        jsonObject.put("token", loadToken())
        jsonObject.put("contentName", contentName)


        VolleyHttpService.getContentMoney(this, jsonObject){ success ->

            rm = success.getInt("penalty")
            cm = success.getInt("penalty")
            fm = success.getInt("money")

            currentMoney!!.text = cm.toString()
            penaltyMoney!!.text = rm.toString()
            finalMoney!!.text = fm.toString()

        }
    }

    private fun loadToken(): String{
        var auto = PreferenceManager.getDefaultSharedPreferences(this)

        return auto.getString("token", "")
    }

}