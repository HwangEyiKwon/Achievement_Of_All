package com.example.parkseunghyun.achievementofall


import adapter.HomePagerAdapter
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import org.jetbrains.anko.startActivity
import org.json.JSONObject


class HomeActivity : AppCompatActivity() {

    private var homeTab: TabLayout? = null

    // jwt-token
    var jwtToken: String?= null


//    override fun onRestart() {
//        super.onRestart()
//        println("restart")
//        println("홈페이지에서 토큰 받기(preference에서): "+ loadToken())
//        jwtToken = loadToken()
//
//
//    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_layout)

        // jwt-token 받기
        println("홈페이지에서 토큰 받기(preference에서): "+ loadToken())
        jwtToken = loadToken()
        savedInstanceState?.putString("jwtToken", jwtToken)


        // 로그아웃 버튼
        val logoutBt = findViewById<View>(R.id.id_toolbar_home).findViewById<View>(R.id.toolbar_layout).findViewById<ImageView>(R.id.logoutButton)
        logoutBt.setOnClickListener {
            logout(jwtToken.toString())
        }

        // Code for TabLayout
        generateTabLayout()

    }


    private fun generateTabLayout() {

        homeTab = findViewById(R.id.id_home_tab)
        homeTab!!.addTab(homeTab!!.newTab().setIcon(R.drawable.ic_icons_person_black))
        homeTab!!.addTab(homeTab!!.newTab().setIcon(R.drawable.ic_icons_search))
        homeTab!!.addTab(homeTab!!.newTab().setIcon(R.drawable.ic_icons_info))
        homeTab!!.tabGravity = TabLayout.GRAVITY_FILL
        println(homeTab)

        var viewPager = findViewById<ViewPager>(R.id.home_pager_container)
        var homePagerAdapter = HomePagerAdapter(supportFragmentManager)
        viewPager?.adapter = homePagerAdapter
        viewPager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(homeTab))

        homeTab!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager?.currentItem = tab.position

                when(tab.position) {
                    0-> {
                        homeTab!!.getTabAt(0)!!.setIcon(R.drawable.ic_icons_person_black)
                        homeTab!!.getTabAt(1)!!.setIcon(R.drawable.ic_icons_search)
                        homeTab!!.getTabAt(2)!!.setIcon(R.drawable.ic_icons_info)
                    }
                    1-> {
                        homeTab!!.getTabAt(0)!!.setIcon(R.drawable.ic_icons_person)
                        homeTab!!.getTabAt(1)!!.setIcon(R.drawable.ic_icons_search_black)
                        homeTab!!.getTabAt(2)!!.setIcon(R.drawable.ic_icons_info)
                    }
                    2-> {
                        homeTab!!.getTabAt(0)!!.setIcon(R.drawable.ic_icons_person)
                        homeTab!!.getTabAt(1)!!.setIcon(R.drawable.ic_icons_search)
                        homeTab!!.getTabAt(2)!!.setIcon(R.drawable.ic_icons_info_black)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    private fun logout(token: String){
        val jsonObject = JSONObject()
        jsonObject.put("token", token)

        VolleyHttpService.logout(this, jsonObject){ success ->
            if(success){
                println("로그아웃 전 토큰"+loadToken())
                saveToken("")
                Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_LONG).show()
                println("로그아웃 후 토큰"+loadToken())
                startActivity<LoginActivity>()

            }else{
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


}

