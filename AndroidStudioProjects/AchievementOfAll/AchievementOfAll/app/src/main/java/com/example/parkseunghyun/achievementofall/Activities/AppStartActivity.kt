package com.example.parkseunghyun.achievementofall.Activities

import adapter.CircleIndicatorAdapter
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import com.example.parkseunghyun.achievementofall.Configurations.MyFirebaseInstanceIDService
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R
import me.relex.circleindicator.CircleIndicator
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class AppStartActivity : AppCompatActivity() {

    private var descViewPager: ViewPager? = null
    private var descCircleIndicator: CircleIndicatorAdapter? = null
    private val indicator: CircleIndicator? = null
    private var mainDescription: TextView? = null
    private var subDescription: TextView? = null
    private var moreDescription: TextView? = null
    private var signUpButton: TextView? = null
    private var loginButton: TextView? = null
    private var time: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_start)


        var jsonObject = loadData()
        var email = jsonObject.getString("email")
        var password = jsonObject.getString("password")

        login(email, password)

        mainDescription = findViewById(R.id.main_description)
        subDescription = findViewById(R.id.sub_description)
        moreDescription  = findViewById(R.id.more_description)
        signUpButton = findViewById(R.id.sign_up_button)
        loginButton = findViewById(R.id.login_button)

        descViewPager = findViewById<ViewPager>(R.id.v1)
        val indicator = findViewById<ViewPager>(R.id.indicator) as CircleIndicator
        descCircleIndicator = CircleIndicatorAdapter(supportFragmentManager)
        descViewPager!!.adapter = descCircleIndicator
        indicator.setViewPager(descViewPager)
        descCircleIndicator!!.registerDataSetObserver(indicator.dataSetObserver)


        signUpButton!!.setOnClickListener {
            val goToSignUp = Intent(applicationContext, SignupActivity::class.java)
            goToSignUp.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(goToSignUp)
        }
        loginButton!!.setOnClickListener {
            val goToLogin = Intent(applicationContext, LoginActivity::class.java)
            goToLogin.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(goToLogin)

        }

        descViewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
            override fun onPageSelected(p0: Int) {
                when (p0){
                    0 -> {
                        mainDescription!!.setText("목표를 달성할\n의지가 부족하신가요 ?")
                        subDescription!!.setText("의지가 부족한 당신! \n모두의 달성과 함께라면 \n실천의지가 팍팍!")
                        moreDescription!!.setText("의지를 북돋아주는\n보상 및 패널티 시스템!\n목표도 달성! 보상도 GET!!")
                    }
                    1 -> {
                        mainDescription!!.setText("목표달성을 함께할\n친구를 찾으시나요 ?")
                        subDescription!!.setText("단조로운 목표달성, \n모두의 달성과 함께라면 \n즐겁습니다!")
                        moreDescription!!.setText("모달 친구들과 서로\n목표 실천 과정을\n확인해주세요!")
                    }
                    2 -> {
                        mainDescription!!.setText("그렇다면,\n모두의 달성과 함께 하세요 !")
                        subDescription!!.setText("그 어떤 목표라도 \n모두의 달성과 함께라면 \n성취할 수 있습니다!")
                        moreDescription!!.setText("최고의 목표달성 플랫폼!\n지금 바로 시작하세요!\n다양한 컨텐츠에 참여 해보세요!")
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        if(System.currentTimeMillis() - time >= 2000){
            time = System.currentTimeMillis()
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show();
        }
        else if(System.currentTimeMillis() - time<2000){
            finish()
        }
    }

    private fun loadData(): JSONObject {
        var auto = PreferenceManager.getDefaultSharedPreferences(this)

        val jsonObject = JSONObject()
        var email = auto.getString("email","0")
        var password = auto.getString("password","0")
        var isChecked = auto.getBoolean("isChecked",false)

        println("TEST_TEST   " + email +  password + isChecked)

        jsonObject.put("email", email)
        jsonObject.put("password",password)
        jsonObject.put("isChecked", isChecked)

        return jsonObject
    }

    private fun sendToken(jsonObject: JSONObject){

        VolleyHttpService.sendToken(this, jsonObject) { success ->
            if (success) {
                Toast.makeText(this, "FCM 토큰 성공", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "FCM 토큰 실패", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun login(email: String, password: String){

        val jsonObject = JSONObject()
        jsonObject.put("email", email)
        jsonObject.put("password",password)

        VolleyHttpService.login(this, jsonObject) { success ->
            if (success.get("success") == true) { // 로그인 성공

                val fcmService = MyFirebaseInstanceIDService()
                fcmService.onTokenRefresh()
                val jsonObject = fcmService.jsonObject as JSONObject

                jsonObject.put("email", email)

                sendToken(jsonObject)

                // 자동 로그인을 위한 정보 저장
                saveData(email, password)

                Toast.makeText(this, "로그인 성공", Toast.LENGTH_LONG).show()

                var jwt_token = success.getJSONObject("headers").get("token")

                // 토큰 저장
                saveToken(jwt_token.toString())

                var homeActivityIntent: Intent = Intent()
                startActivity<HomeActivity>()
//                finish()


            } else { // 로그인 실패
                saveData("0", "0")
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveData(email: String, password: String){
        var auto = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = auto.edit()

        editor.putString("email", email)
                .putString("password",password)
                .apply()
    }

    private fun saveToken(token: String){
        var auto = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = auto.edit()
        editor.putString("token", token)
                .apply()
    }

}
