package com.example.parkseunghyun.achievementofall.Activities

import adapter.AppStartPagerAdapter
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import com.example.parkseunghyun.achievementofall.Configurations.FirebaseInstanceIDService
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R
import kotlinx.android.synthetic.main.container_contents_pager.*
import org.jetbrains.anko.startActivity
import org.json.JSONObject

/**
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

        val jsonObjectForLogin = loadDataForAutoLogin()
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

    private fun loadDataForAutoLogin(): JSONObject {

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val jsonObjectForAutoLogin = JSONObject()
        val email = sharedPref.getString("email","0")
        val password = sharedPref.getString("password","0")
        val isChecked = sharedPref.getBoolean("isChecked",false)

        jsonObjectForAutoLogin.put("email", email)
        jsonObjectForAutoLogin.put("password",password)
        jsonObjectForAutoLogin.put("isChecked", isChecked)

        return jsonObjectForAutoLogin

    }

    private fun loginRequest(email: String, password: String){

        val jsonObjectForLogin = JSONObject()
        jsonObjectForLogin.put("email", email)
        jsonObjectForLogin.put("password",password)

        VolleyHttpService.login(this, jsonObjectForLogin) { success ->

            if (success.get("success") == true) {

                val fcmService = FirebaseInstanceIDService()
                fcmService.onTokenRefresh()

                val jsonObjectForFCM = fcmService.jsonObjectForRefreshFCM as JSONObject
                jsonObjectForFCM.put("email", email)
                sendFCMToken(jsonObjectForFCM)

                saveDataForAutoLogin(email, password)

                val jwtToken = success.getJSONObject("headers").get("token")
                saveJwtToken(jwtToken.toString())
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()

                startActivity<HomeActivity>()

            } else {

                saveDataForAutoLogin("0", "0")
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()

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

            } else {

            }

        }

    }

}
