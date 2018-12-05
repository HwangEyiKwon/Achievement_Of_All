package com.example.parkseunghyun.achievementofall.Activities

import adapter.AppStartPagerAdapter
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
import kotlinx.android.synthetic.main.contents_pager_container.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject

/*
    REFACTORED.
 */

class AppStartActivity : AppCompatActivity() {


    private var signUpButton: TextView? = null
    private var loginButton: TextView? = null

    private var appDescViewPager: ViewPager? = null
    private var adapterForDescView: AppStartPagerAdapter? = null

    private var timeToCheckExitConfirm: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_start)

        initViewComponents()
        initButtonListener()

        val jsonObjectForLogin = loadData()
        val userEmail = jsonObjectForLogin.getString("email")
        val userPW = jsonObjectForLogin.getString("password")

        loginRequest( userEmail, userPW )

    }

    private fun initButtonListener() {
        signUpButton!!.setOnClickListener {
            val goToSignupActivity = Intent(applicationContext, SignupActivity::class.java)
            goToSignupActivity.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(goToSignupActivity)
        }

        loginButton!!.setOnClickListener {
            val goToLoginActivity = Intent(applicationContext, LoginActivity::class.java)
            goToLoginActivity.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(goToLoginActivity)
        }
    }

    private fun initViewComponents() {

        signUpButton = findViewById(R.id.sign_up_button)
        loginButton = findViewById(R.id.login_button)

        appDescViewPager = findViewById(R.id.app_desc_viewpager)
        adapterForDescView = AppStartPagerAdapter(supportFragmentManager)
        appDescViewPager!!.adapter = adapterForDescView

        app_desc_indicator.createDotPanel(3, R.drawable.desc_num_indicator_off, R.drawable.desc_num_indicator_on, 0)


        appDescViewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
            override fun onPageSelected(p0: Int) {
                app_desc_indicator.selectDot(p0)
            }
        })

    }

    override fun onBackPressed() {

        if(System.currentTimeMillis() - timeToCheckExitConfirm >= 2000){
            timeToCheckExitConfirm = System.currentTimeMillis()
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show();
        }
        else if(System.currentTimeMillis() - timeToCheckExitConfirm<2000){
            finish()
        }

    }

    private fun loadData(): JSONObject {

        var auto = PreferenceManager.getDefaultSharedPreferences(this)

        val jsonObject = JSONObject()
        var email = auto.getString("email","0")
        var password = auto.getString("password","0")
        var isChecked = auto.getBoolean("isChecked",false)

        jsonObject.put("email", email)
        jsonObject.put("password",password)
        jsonObject.put("isChecked", isChecked)

        return jsonObject

    }

    private fun loginRequest(email: String, password: String){

        val jsonObject = JSONObject()
        jsonObject.put("email", email)
        jsonObject.put("password",password)

        VolleyHttpService.login(this, jsonObject) { success ->
            if (success.get("success") == true) { // 로그인 성공

                val fcmService = MyFirebaseInstanceIDService()
                fcmService.onTokenRefresh()

                val jsonObjectForFCM = fcmService.jsonObject as JSONObject
                jsonObjectForFCM.put("email", email)
                sendFCMToken(jsonObjectForFCM)

                // 자동 로그인을 위한 정보 저장
                saveDataForAutoLogin(email, password)

                val jwtToken = success.getJSONObject("headers").get("token")
                saveJwtToken(jwtToken.toString())
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()

                startActivity<HomeActivity>()

            } else { // 로그인 실패

                saveDataForAutoLogin("0", "0")
//                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()

            }
        }

    }

    private fun saveDataForAutoLogin(email: String, password: String){

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val editorForSaveLoginData = sharedPref.edit()

        editorForSaveLoginData
                .putString("email", email)
                .putString("password",password)
                .apply()

    }

    private fun saveJwtToken(token: String){

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val editorForSaveJwtToken = sharedPref.edit()
        editorForSaveJwtToken
                .putString("token", token)
                .apply()

    }

    private fun sendFCMToken(jsonObject: JSONObject){

        VolleyHttpService.sendToken(this, jsonObject) { success ->
            if (success) {
//                Toast.makeText(this, "FCM 토큰 성공", Toast.LENGTH_LONG).show()
            } else {
//                Toast.makeText(this, "FCM 토큰 실패", Toast.LENGTH_LONG).show()
            }
        }

    }

}
