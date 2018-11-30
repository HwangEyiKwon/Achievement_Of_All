package com.example.parkseunghyun.achievementofall.Fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.parkseunghyun.achievementofall.Activities.RewardActivity
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.ContentsHomeActivity
import com.example.parkseunghyun.achievementofall.R
import org.json.JSONObject



class ContentsProgressPager : Fragment() {

    private var view_: View? = null
    private var achievementRate: TextView ? = null
    private var contentsName: TextView? = null

    // 사용자의 jwt-token
    private var jwtToken: String ?= null
    private var contentName: String? = null
    private var joinState: Int ?= null

    private var rewardButton: Button ?= null

    private var reward: TextView?= null
    private var money: TextView?= null

    private var rewardMoney: Int ?= null
    private var currentMoney: Int ?= null

    private val REWARD_CODE = 222

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        view_ =  inflater!!.inflate(R.layout.fragment_contents_progress, container, false)

        achievementRate = view_?.findViewById(R.id.achievementRate)

        println("getAcheid달성달성률")

        val activity = activity as ContentsHomeActivity
        jwtToken = activity.jwtToken.toString()
        contentName = activity.content.toString()
        joinState = activity.joinState

        println("TEST2 " + contentName)

        contentsName = view_?.findViewById(R.id.id_contents_name_2)
        contentsName?.setText(contentName)

        reward= view_?.findViewById(R.id.reward)
        money= view_?.findViewById(R.id.money)

        rewardButton = view_?.findViewById(R.id.button_to_reward)


        when(joinState){
            2 -> {
                rewardButton!!.isEnabled = true
            }
            else->{
                rewardButton!!.isEnabled = false
                rewardButton!!.setTextColor(resources.getColor(R.color.icongrey))
            }
        }

        getAchievementRate()
        getCurrentMoney()

        rewardButton!!.setOnClickListener {
            rewardCheck ()
        }

        return view_
    }
    fun rewardCheck (){

        val jsonObject = JSONObject()
        jsonObject.put("token", jwtToken)
        jsonObject.put("contentName", contentName)

        VolleyHttpService.rewardCheck(this.context, jsonObject) { success ->

            println(success)
            if(success.getBoolean("success")){

                if(joinState == 3){

                }else{
                    val goToReward = Intent(context, RewardActivity::class.java)
                    goToReward.putExtra("token", jwtToken)
                    goToReward.putExtra("contentName", contentName)
                    goToReward.putExtra("rewardMoney", rewardMoney)
                    goToReward.putExtra("currentMoney", currentMoney)

                    val contextToActivity = context as Activity
                    contextToActivity.startActivityForResult(goToReward, REWARD_CODE)
                }

            }else{
                Toast.makeText(this.context,"이미 보상을 받으셨습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private fun getCurrentMoney(){

        val jsonObject = JSONObject()
        jsonObject.put("token", jwtToken)
        jsonObject.put("contentName", contentName)


        VolleyHttpService.getContentMoney(this.context, jsonObject){ success ->

            println(success)

            if(joinState == 3){

            }else{
                reward!!.text = success.getInt("reward").toString()
                money!!.text = success.getInt("money").toString()
                rewardMoney = success.getInt("reward")
                currentMoney = success.getInt("money")
            }

        }

    }
    private fun getAchievementRate(){

        val jsonObject = JSONObject()
        jsonObject.put("token", jwtToken)
        jsonObject.put("contentName", contentName)

        VolleyHttpService.getAchievementRate(this.context, jsonObject){ success ->
            println("getAcheidfdsa "+success)

            var rate = success.getInt("rate")

            val progress = view_?.findViewById(R.id.progress) as ProgressBar
            progress.progress = rate
            progress.visibility = View.GONE

            val progressText = view_?.findViewById(R.id.id_achievement_rate_support) as TextView
            progressText.visibility = View.GONE

            println("TESTTEST: " + rate)

            if(joinState == 0){
                achievementRate!!.setText("컨텐츠 시작 전입니다...........")
                progress.visibility = View.VISIBLE
                progress.progress = 0
            }
            else if(joinState == 2){
                achievementRate!!.setText("목표에 달성하셨습니다!! 축하드립니다.!!")
                progress.visibility = View.VISIBLE
                progress.progress = 100

            }else if(rate == -1){
                achievementRate!!.setText("에러")
            }else{
                achievementRate!!.setText(rate.toString())
                progress.visibility = View.VISIBLE
                progress.progress = rate
                progressText.visibility = View.VISIBLE
            }

        }

    }

}