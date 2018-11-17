package com.example.parkseunghyun.achievementofall

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.json.JSONObject

class ContentsSecond_pager : Fragment() {

    private var view_: View? = null

    private var contentDescription: TextView ? = null
    private var achievementRate: TextView ? = null

    // 사용자의 jwt-token
    private var jwtToken: String ?= null
    private var contentName: String? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        view_ =  inflater!!.inflate(R.layout.contents_fragment_2, container, false)

        contentDescription = view_?.findViewById(R.id.contentDescription)
        achievementRate = view_?.findViewById(R.id.achievementRate)

        println("getAcheid달성달성률")

        val activity = activity as ContentsHomeActivity
        jwtToken = activity.jwtToken.toString()
        contentName = activity.content.toString()

        getAchievementRate()

        return view_
    }
    private fun getAchievementRate(){

        val jsonObject = JSONObject()
        jsonObject.put("contentName", contentName)

        VolleyHttpService.getAcheivementRate(this.context, jsonObject){ success ->
            println("getAcheidfdsa "+success)
            if(success.getBoolean("success")){
                contentDescription!!.setText(success.getString("description"))
                achievementRate!!.setText(success.getString("rate"))
            }

        }

    }

}