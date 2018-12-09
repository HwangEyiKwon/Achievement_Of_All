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
import com.example.parkseunghyun.achievementofall.Configurations.RequestCodeCollection
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.ContentsHomeActivity
import com.example.parkseunghyun.achievementofall.R
import org.json.JSONObject

/**
    REFACTORED
 */

class ContentsProgressPager : Fragment() {

    private var contentProgressView: View? = null
    private var achievementRate: TextView ? = null
    private var contentNameView: TextView? = null

    private var jwtToken: String ?= null
    private var contentName: String? = null
    private var joinState: Int ?= null

    private var rewardButton: Button ?= null

    private var rewardMoneyView: TextView?= null
    private var returnMoneyView: TextView?= null

    private var rewardMoney: Int ?= null
    private var currentMoney: Int ?= null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        contentProgressView =  inflater!!.inflate(R.layout.fragment_contents_progress, container, false)

        achievementRate = contentProgressView?.findViewById(R.id.achievementRate)

        val activity = activity as ContentsHomeActivity
        jwtToken = activity.loadJWTToken()
        contentName = activity.content.toString()
        joinState = activity.joinState

        contentNameView = contentProgressView?.findViewById(R.id.id_contents_name_2)
        contentNameView?.text = contentName

        rewardMoneyView = contentProgressView?.findViewById(R.id.reward)
        returnMoneyView = contentProgressView?.findViewById(R.id.money)
        rewardButton = contentProgressView?.findViewById(R.id.button_to_reward)

        getAchievementRate()
        getCurrentMoney()

        rewardButton!!.setOnClickListener {

            rewardCheck ()

        }

        return contentProgressView

    }



    fun rewardCheck (){

        val jsonObjectForReward = JSONObject()
        jsonObjectForReward.put("token", jwtToken)
        jsonObjectForReward.put("contentName", contentName)

        VolleyHttpService.rewardCheck(this.context, jsonObjectForReward) { success ->

            if(success.getBoolean("success")){

                val goToReward = Intent(context, RewardActivity::class.java)
                goToReward.putExtra("token", jwtToken)
                goToReward.putExtra("contentName", contentName)
                goToReward.putExtra("penaltyMoney", rewardMoney)
                goToReward.putExtra("currentMoney", currentMoney)

                val contextToActivity = context as Activity
                contextToActivity.startActivityForResult(goToReward, RequestCodeCollection.REQUEST_RETURN_FROM_CONTENT_REWARD)

            } else {

                Toast.makeText(this.context,"이미 보상을 받으셨습니다.", Toast.LENGTH_SHORT).show()

            }

        }
    }

    private fun getCurrentMoney(){

        val jsonObjectToGetMoney = JSONObject()
        jsonObjectToGetMoney.put("token", jwtToken)
        jsonObjectToGetMoney.put("contentName", contentName)

        VolleyHttpService.getContentMoney(this.context, jsonObjectToGetMoney){ success ->

            if(joinState != 3) {

                rewardMoneyView!!.text = success.getInt("reward").toString()
                returnMoneyView!!.text = success.getInt("money").toString()
                rewardMoney = success.getInt("reward")
                currentMoney = success.getInt("money")

            } else {
                rewardMoneyView!!.text = "0"
                returnMoneyView!!.text = "0"
            }

        }
    }

    private fun getAchievementRate(){

        val jsonObject = JSONObject()
        jsonObject.put("token", jwtToken)
        jsonObject.put("contentName", contentName)

        VolleyHttpService.getAchievementRate(this.context, jsonObject){ success ->

            val rate = success.getInt("rate")

            val progress = contentProgressView?.findViewById(R.id.progress) as ProgressBar
            progress.progress = rate

            val progressText = contentProgressView?.findViewById(R.id.id_achievement_rate_support) as TextView
            progressText.visibility = View.GONE

            when(joinState) {

                0 -> { /**"참가중 (시작 전)"*/

                    rewardButton!!.isEnabled = false
                    rewardButton!!.setTextColor(resources.getColor(R.color.icongrey))

                    achievementRate!!.text = ""
                    progressText.text = "컨텐츠 시작 전입니다."
                    progressText.visibility = View.VISIBLE

                    progress.progress = 0

                }

                1 -> { /**"참가중 (진행중)"*/

                    rewardButton!!.isEnabled = false
                    rewardButton!!.setTextColor(resources.getColor(R.color.icongrey))

                    achievementRate!!.setText(rate.toString())
                    progressText.text = "% 진행 중입니다."
                    progressText.visibility = View.VISIBLE

                    progress.progress = rate

                }

                2 -> { /**"목표 달성 성공"*/

                    rewardButton!!.isEnabled = true
                    rewardButton!!.setTextColor(resources.getColor(R.color.colorPrimaryDark))

                    achievementRate!!.text = ""
                    progressText.text = "목표 달성에 성공하셨습니다."
                    progressText.visibility = View.VISIBLE

                    progress.progress = 100

                }

                3 -> { /**"미참가중*/
                    rewardButton!!.isEnabled = false
                    rewardButton!!.setTextColor(resources.getColor(R.color.icongrey))

                    achievementRate!!.text = ""
                    progressText.text = "아직 참가중이지 않습니다."
                    progressText.visibility = View.VISIBLE

                    progress.progress = 0

                }

                4 -> { /**"목표 달성 실패"*/
                    rewardButton!!.isEnabled = false
                    rewardButton!!.setTextColor(resources.getColor(R.color.icongrey))

                    achievementRate!!.setText(rate.toString())
                    progressText.text = "% 진행 중에 실패하셨습니다."
                    progressText.visibility = View.VISIBLE

                    progress.progress = rate

                }

            }

        }

    }

}