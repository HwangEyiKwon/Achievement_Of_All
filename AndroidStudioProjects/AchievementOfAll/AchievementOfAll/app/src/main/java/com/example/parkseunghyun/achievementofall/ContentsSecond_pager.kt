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
    private var achievementRate: TextView ? = null

    // 사용자의 jwt-token
    private var jwtToken: String ?= null
    private var contentName: String? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        view_ =  inflater!!.inflate(R.layout.contents_fragment_2, container, false)

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
        jsonObject.put("token", jwtToken)
        jsonObject.put("contentName", contentName)

        VolleyHttpService.getAchievementRate(this.context, jsonObject){ success ->
            println("getAcheidfdsa "+success)

                var rate = success.getInt("rate")

                if(rate == 0){
                    achievementRate!!.setText("시작 전인 컨텐츠 방입니다..")
                }else if(rate == -1){
                    achievementRate!!.setText("참가 중인 컨텐츠 방 정보가 없습니다.")
                }else{
                    achievementRate!!.setText(rate.toString())
                }



        }

    }

}