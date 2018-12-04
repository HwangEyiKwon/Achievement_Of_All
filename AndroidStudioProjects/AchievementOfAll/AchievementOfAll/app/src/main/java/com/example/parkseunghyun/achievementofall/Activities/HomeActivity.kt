package com.example.parkseunghyun.achievementofall.Activities


import adapter.HomePagerAdapter
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.ContentsHomeActivity
import com.example.parkseunghyun.achievementofall.R
import org.json.JSONObject


class HomeActivity : AppCompatActivity() {

    private var homeTab: TabLayout? = null
    private var time: Long = 0
    val REQUEST_FROM_JCA = 10101
    val REQUEST_FROM_SEARCH = 1010
    var homePagerAdapter: HomePagerAdapter? = null
    var viewPager:ViewPager? = null

    // jwt-token
    var jwtToken: String?= null

    val REQUEST_FIRST_CREATE = 808
    val REQUEST_UPDATE = 111
    val REQUEST_FROM_EDIT = 333




    override fun onRestart() {
        super.onRestart()
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
    }




    // TODO - GEONHEE's UI 이 함수를 넣음으로써 로그아웃 방지함. 위의 주석은 지워도 될거같다.
    override fun onBackPressed() {
//        super.onBackPressed()
//        얘를 주석처리해야 뒤로가기가 안눌린다리
        if(System.currentTimeMillis() - time >= 2000){
            time = System.currentTimeMillis()
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 앱을 종료합니다.",Toast.LENGTH_SHORT).show();
        }
        else if(System.currentTimeMillis() - time < 2000){
            finishAffinity()
        }
    }

    override fun onResume(){
        super.onResume();
        println("RESUMERESUMERESUMEHome")
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_home_layout)

        // jwt-token 받기
        println("홈페이지에서 토큰 받기(preference에서): "+ loadToken())
        jwtToken = loadToken()
        savedInstanceState?.putString("jwtToken", jwtToken)

        // 로그아웃 버튼
        val logoutBt = findViewById<View>(R.id.id_toolbar_home).findViewById<View>(R.id.toolbar_layout).findViewById<ImageView>(R.id.logoutButton)
        logoutBt.setOnClickListener {
            logout(jwtToken.toString())
            finish()
        }

        // Code for TabLayout
        generateTabLayout(REQUEST_FIRST_CREATE)

        var ownIntent:Intent = intent
        var tmpContentName = ownIntent.getStringExtra("contentName")

        if(ownIntent.getStringExtra("fcm_category") == null || ownIntent.getStringExtra("contentName") == null){
            // 아무것도 안해도 됨
        }
        else if(ownIntent.getStringExtra("fcm_category").equals("목표 달성 실패 알림")){
            // TODO 여기에 ContentHomeActivity 들어가야됨.
            val intent = Intent(this, ContentsHomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("fcm_category", "목표 달성 실패 알림")
            intent.putExtra("contentName", tmpContentName)
            startActivity(intent)
        }
        else if(ownIntent.getStringExtra("fcm_category").equals("인증 시간이 얼마 남지 않았어요!")){
            val intent = Intent(this, ContentsHomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("fcm_category", "인증 시간이 얼마 남지 않았어요!")
            intent.putExtra("contentName", tmpContentName)
            startActivity(intent)

        }
        else if(ownIntent.getStringExtra("fcm_category").equals("목표 달성 성공 알림")){
            val intent = Intent(this, ContentsHomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("fcm_category", "목표 달성 성공 알림")
            intent.putExtra("contentName", tmpContentName)
            startActivity(intent)

        }
        else if(ownIntent.getStringExtra("fcm_category").equals("과반수의 반대로 인증에 실패하셨습니다")){
            val intent = Intent(this, ContentsHomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("fcm_category", "과반수의 반대로 인증에 실패하셨습니다")
            intent.putExtra("contentName", tmpContentName)
            intent.putExtra("rejectUserArray", ownIntent.getStringExtra("rejectUserArray"))
            intent.putExtra("rejectReasonArray", ownIntent.getStringExtra("rejectReasonArray"))
            startActivity(intent)
        }

    }

    private fun generateTabLayout(request: Int) {


        if(request == REQUEST_FIRST_CREATE) {

            viewPager = findViewById(R.id.home_pager_container)
            homeTab = findViewById(R.id.id_home_tab)


//            homeTab = findViewById(R.id.id_home_tab)
            homeTab!!.addTab(homeTab!!.newTab().setIcon(R.drawable.ic_user_on))
            homeTab!!.addTab(homeTab!!.newTab().setIcon(R.drawable.ic_search_off))
            homeTab!!.addTab(homeTab!!.newTab().setIcon(R.drawable.ic_info_off))
            homeTab!!.tabGravity = TabLayout.GRAVITY_FILL
//            viewPager = findViewById<ViewPager>(R.id.home_pager_container)


            viewPager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(homeTab))

            homeTab!!.getTabAt(0)!!.setIcon(R.drawable.ic_user_on)
            homeTab!!.getTabAt(1)!!.setIcon(R.drawable.ic_search_off)
            homeTab!!.getTabAt(2)!!.setIcon(R.drawable.ic_info_off)


            homeTab!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPager?.currentItem = tab.position
                    Log.d(this.javaClass.name, "POSITION LOGGING   $tab.position")

                    when(tab.position) {
                        0-> {

                            homeTab!!.getTabAt(0)!!.setIcon(R.drawable.ic_user_on)
                            homeTab!!.getTabAt(1)!!.setIcon(R.drawable.ic_search_off)
                            homeTab!!.getTabAt(2)!!.setIcon(R.drawable.ic_info_off)
                        }

                        1-> {
                            homeTab!!.getTabAt(0)!!.setIcon(R.drawable.ic_user_off)
                            homeTab!!.getTabAt(1)!!.setIcon(R.drawable.ic_search_on)
                            homeTab!!.getTabAt(2)!!.setIcon(R.drawable.ic_info_off)

                        }
                        2-> {

                            homeTab!!.getTabAt(0)!!.setIcon(R.drawable.ic_user_off)
                            homeTab!!.getTabAt(1)!!.setIcon(R.drawable.ic_search_off)
                            homeTab!!.getTabAt(2)!!.setIcon(R.drawable.ic_info_on)

                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {


                }
                override fun onTabReselected(tab: TabLayout.Tab) {
                }
            })

            homePagerAdapter = HomePagerAdapter(supportFragmentManager)
            viewPager?.adapter = homePagerAdapter
            viewPager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(homeTab))

        }

        if(request == REQUEST_UPDATE){

            homePagerAdapter = HomePagerAdapter(supportFragmentManager)
            viewPager?.adapter = homePagerAdapter
            viewPager!!.currentItem = homeTab!!.selectedTabPosition

        }

    }



    private fun logout(token: String){
        val jsonObject = JSONObject()
        jsonObject.put("token", token)

        VolleyHttpService.logout(this, jsonObject) { success ->
            if (success) {
                println("로그아웃 전 토큰" + loadToken())
                saveToken("")
                Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_LONG).show()
                println("로그아웃 후 토큰" + loadToken())

            /*TODO(완료) 이것은 로그아웃 후 어플 재실행 시 자동로그인이 되버리는 것을 방지. */
                var auto2 = PreferenceManager.getDefaultSharedPreferences(this)
                val editor = auto2.edit()
                editor.clear()
                editor.apply()
            /* 여기까지용 */

                finish()

            } else {
                Toast.makeText(this, "로그아웃 실패", Toast.LENGTH_LONG).show()
            }
        }
    }

    // SharedPreferences (jwt-token)
    private fun saveToken(token: String){
        var auto = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = auto.edit()
        editor.putString("token", token)
                .apply()
    }

    private fun loadToken(): String{
        var auto = PreferenceManager.getDefaultSharedPreferences(this)

        return auto.getString("token", "")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("TEST---- HomeActivity로 오나?" + resultCode)



        when (requestCode) {
            REQUEST_FROM_JCA -> {
                println("TEST------ HomeActivity from jca")
                generateTabLayout(REQUEST_UPDATE)

            }
            REQUEST_FROM_SEARCH -> {
                println("TEST------ HomeActivity from search")
                generateTabLayout(REQUEST_UPDATE)
            }
            REQUEST_FROM_EDIT -> {
                println("TEST----- EDIT??")
                generateTabLayout(REQUEST_UPDATE)
            }


        }

    }


}

