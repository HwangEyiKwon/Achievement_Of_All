package com.example.parkseunghyun.achievementofall


import adapter.HomePagerAdapter
import android.content.ComponentName
import android.content.Intent
import android.media.MediaRecorder
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import org.jetbrains.anko.startActivity
import org.json.JSONObject


class HomeActivity : AppCompatActivity() {

    private var homeTab: TabLayout? = null

    // jwt-token
    var jwtToken: String?= null

    // 카메라 연동
    private var cam: android.hardware.Camera ?= null;
    private var MediaRecorder: MediaRecorder?= null;
    private var sv: SurfaceView?= null;
    private var sh: SurfaceHolder?= null;

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

//        println("PHOTOOOO???1")
//        VolleyHttpService.getUserImage(this){ success ->
//            println("PHOTOOOO???dddd")
//            println(success)
//        }
//    println("PHOTOOOO???2")



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


        var viewPager = findViewById<ViewPager>(R.id.home_pager_container)
        var homePagerAdapter = HomePagerAdapter(supportFragmentManager)
        viewPager?.adapter = homePagerAdapter
        viewPager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(homeTab))

        homeTab!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager?.currentItem = tab.position

//                when(viewPager.currentItem) {
//
//                    0-> {
//                        homeTab!!.findViewById<>()
//                    }
//                    1-> {
//                    }
//                    2-> {
//                    }
//                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

    }


    private fun generateTabLayout() {

        homeTab = findViewById(R.id.id_home_tab)
        homeTab!!.addTab(homeTab!!.newTab().setIcon(R.drawable.ic_home_outline))
        homeTab!!.addTab(homeTab!!.newTab().setIcon(R.drawable.ic_search))
        homeTab!!.addTab(homeTab!!.newTab().setIcon(R.drawable.ic_icons_info))
        homeTab!!.tabGravity = TabLayout.GRAVITY_FILL
        println(homeTab)
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




    fun callCamera() {

        var i = Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            var pm = getPackageManager();

            var mInfo = pm.resolveActivity(i, 0);

            var intent =  Intent();
            intent.setComponent(ComponentName(mInfo.activityInfo.packageName, mInfo.activityInfo.name));
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            startActivity(intent);
        } catch (e: Exception){ Log.i("TAG", "Unable to launch camera: " + e); }
    }
//
//    fun setting(){
//        cam = android.hardware.Camera.open();
//        cam?.setDisplayOrientation(90);
//        sv = findViewById(R.id.surfaceView);
//        sh = sv?.getHolder();
//        sh?.addCallback(this);
//        sh?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//    <SurfaceView
//    android:layout_width="wrap_content"
//    android:layout_height="wrap_content"
//    android:id = "@+id/surfaceView"/>
//    }

}

