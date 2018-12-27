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

// AppStartActivity
// 메인 화면
class AppStartActivity : AppCompatActivity() {

    private var signUpButton: TextView? = null
    private var loginButton: TextView? = null

    private var appDescViewPager: ViewPager? = null
    private var adapterForDescView: AppStartPagerAdapter? = null

    private var timeToCheckExitConfirm: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_start)

        // 초기화
        initViewComponents()
        initButtonListener()

        // 자동 로그인
        val jsonObjectForLogin = loadDataForAutoLogin()
        val userEmail = jsonObjectForLogin.getString("email")
        val userPW = jsonObjectForLogin.getString("password")
        loginRequest( userEmail, userPW )

    }

    // initButtonListener
    // 버튼이 눌렸을 떄 작동합니다.
    private fun initButtonListener() {

        // 회원가입 버튼이 눌렸을 경우
        signUpButton!!.setOnClickListener {

            val goToSignupActivity = Intent(applicationContext, SignupActivity::class.java)
            goToSignupActivity.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(goToSignupActivity)

        }
        // 로그인 버튼이 눌렸을 경우
        loginButton!!.setOnClickListener {

            val goToLoginActivity = Intent(applicationContext, LoginActivity::class.java)
            goToLoginActivity.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(goToLoginActivity)

        }
    }

    // initViewComponent
    // 메인 페이지의 view에 있는 각 요소들을 초기화합니다.
    private fun initViewComponents() {

        // 로그인 버튼과 회원가입 버튼을 초기화합니다.
        signUpButton = findViewById(R.id.sign_up_button)
        loginButton = findViewById(R.id.login_button)

        // 앱 설명 페이저에 어댑터를 장착합니다.
        appDescViewPager = findViewById(R.id.app_desc_viewpager)
        adapterForDescView = AppStartPagerAdapter(supportFragmentManager)
        appDescViewPager!!.adapter = adapterForDescView

        // 앱 설명 페이저 하단에 점 3개
        app_desc_indicator.createDotPanel(3, R.drawable.desc_num_indicator_off, R.drawable.desc_num_indicator_on, 0)

        appDescViewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
            override fun onPageSelected(p0: Int) {

                app_desc_indicator.selectDot(p0)

            }
        })

    }

    // onBackPressed (override)
    // 뒤로가기 버튼을 짧은 시간 내 두번 누르면 앱이 종료됩니다.
    override fun onBackPressed() {

        if(System.currentTimeMillis() - timeToCheckExitConfirm >= 2000){

            timeToCheckExitConfirm = System.currentTimeMillis()
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show();

        }
        else if(System.currentTimeMillis() - timeToCheckExitConfirm<2000){
            finish() // 앱 종료
        }

    }

    // loadDataForAutoLogin
    // 자동 로그인을 위해 SharedPreference에서 데이터를 가져옵니다.
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

    // loginRequest
    // loadDataForAutoLogin에서 받아온 데이터로 로그인을 진행합니다.
    private fun loginRequest(email: String, password: String){

        val jsonObjectForLogin = JSONObject()
        jsonObjectForLogin.put("email", email)
        jsonObjectForLogin.put("password",password)

        VolleyHttpService.login(this, jsonObjectForLogin) { success ->

            if (success.get("success") == true) { // 로그인에 성공할 경우

                // 새로운 FCM 토큰을 발급받습니다.
                val fcmService = FirebaseInstanceIDService()
                fcmService.onTokenRefresh()

                val jsonObjectForFCM = fcmService.jsonObjectForRefreshFCM as JSONObject
                jsonObjectForFCM.put("email", email)
                sendFCMToken(jsonObjectForFCM)

                saveDataForAutoLogin(email, password)

                // 새로운 JWT 토큰을 발급받습니다.
                val jwtToken = success.getJSONObject("headers").get("token")
                saveJwtToken(jwtToken.toString())
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()

                // 로그인에 성공하여 HomeActivity로 이동합니다.
                startActivity<HomeActivity>()

            } else { // 로그인에 실패할 경우

                // 로그인에 실패했을 경우 SharedPreference에 데이터를 0으로 초기화합니다.
                saveDataForAutoLogin("0", "0")
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()

            }
        }

    }

    // saveDataForAutoLogin
    // 받아온 데이터를 SharedPreference에 저장합니다.
    private fun saveDataForAutoLogin(email: String, password: String){

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val editorForSaveLoginData = sharedPref.edit()

        editorForSaveLoginData
                .putString("email", email)
                .putString("password",password)
                .apply()

    }

    // saveJwtToken
    // JWT 토큰을 SharedPreference에 저장합니다.
    private fun saveJwtToken(token: String){

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val editorForSaveJwtToken = sharedPref.edit()
        editorForSaveJwtToken
                .putString("token", token)
                .apply()

    }

    // sendFCMToken
    // FCM Token을 전송합니다.
    private fun sendFCMToken(jsonObject: JSONObject){

        VolleyHttpService.sendToken(this, jsonObject) { success ->

            if (success) {

            } else {
                Toast.makeText(this, "FCM 토큰 전송 실패", Toast.LENGTH_SHORT).show()
            }

        }

    }

}
