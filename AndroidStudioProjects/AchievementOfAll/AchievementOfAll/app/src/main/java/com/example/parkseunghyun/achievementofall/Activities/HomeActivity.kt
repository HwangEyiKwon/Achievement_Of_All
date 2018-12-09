package com.example.parkseunghyun.achievementofall.Activities

import adapter.HomePagerAdapter
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.example.parkseunghyun.achievementofall.Configurations.GlideLoadingFlag
import com.example.parkseunghyun.achievementofall.Configurations.RequestCodeCollection
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.ContentsHomeActivity
import com.example.parkseunghyun.achievementofall.R
import org.json.JSONObject

/**
    REFARCTORED
 */

class HomeActivity : AppCompatActivity() {

    private var homeTab: TabLayout? = null
    private var timeToConfirmExit: Long = 0

    private var homePagerAdapter: HomePagerAdapter? = null
    private var viewPagerForHomeTab:ViewPager? = null

    private var intentToCommuinate:Intent? = null

    var jwtToken: String?= null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        jwtToken = loadJWTToken()
        savedInstanceState?.putString("jwtToken", jwtToken)

        initButtonListener()

        genViewComponents(RequestCodeCollection.REQUEST_DEFAULT_CREATION)

        handleFCMRequest()

    }


    private fun initButtonListener() {

        val logoutButton = findViewById<View>(R.id.id_toolbar_home).findViewById<View>(R.id.toolbar_layout).findViewById<ImageView>(R.id.logoutButton)

        logoutButton.setOnClickListener {

            logoutRequest(jwtToken.toString())
            finish()

        }

    }

    private fun genViewComponents(request: Int) {

        when(request){

            RequestCodeCollection.REQUEST_DEFAULT_CREATION -> {

                viewPagerForHomeTab = findViewById(R.id.home_pager_container)
                homeTab = findViewById(R.id.id_home_tab)

                homeTab!!.addTab(homeTab!!.newTab().setIcon(R.drawable.ic_user_on))
                homeTab!!.addTab(homeTab!!.newTab().setIcon(R.drawable.ic_search_off))
                homeTab!!.addTab(homeTab!!.newTab().setIcon(R.drawable.ic_info_off))
                homeTab!!.getTabAt(0)!!.setIcon(R.drawable.ic_user_on)
                homeTab!!.getTabAt(1)!!.setIcon(R.drawable.ic_search_off)
                homeTab!!.getTabAt(2)!!.setIcon(R.drawable.ic_info_off)
                homeTab!!.tabGravity = TabLayout.GRAVITY_FILL

                viewPagerForHomeTab?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(homeTab))

                homeTab!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab) {

                        viewPagerForHomeTab?.currentItem = tab.position

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

                    override fun onTabUnselected(tab: TabLayout.Tab) {}
                    override fun onTabReselected(tab: TabLayout.Tab) {}

                })

                homePagerAdapter = HomePagerAdapter(supportFragmentManager)
                viewPagerForHomeTab?.adapter = homePagerAdapter
                viewPagerForHomeTab?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(homeTab))

            }

            RequestCodeCollection.REQUEST_RETURN_FROM_JOINED_CONTENT -> {

                if(GlideLoadingFlag.getThumbnailFlag() == GlideLoadingFlag.FLAG_UPDATED) {

                    homePagerAdapter = HomePagerAdapter(supportFragmentManager)
                    viewPagerForHomeTab?.adapter = homePagerAdapter
                    viewPagerForHomeTab!!.currentItem = homeTab!!.selectedTabPosition

                }

            }

            RequestCodeCollection.REQUEST_RETURN_FROM_SEARCH -> {

                if(GlideLoadingFlag.getJoinedContentFlag() == GlideLoadingFlag.FLAG_UPDATED) {

                    GlideLoadingFlag.setJoinedContentFlag(GlideLoadingFlag.FLAG_NOT_UPDATED)
                    homePagerAdapter = HomePagerAdapter(supportFragmentManager)
                    viewPagerForHomeTab?.adapter = homePagerAdapter
                    viewPagerForHomeTab!!.currentItem = homeTab!!.selectedTabPosition

                }

            }

            RequestCodeCollection.REQUEST_RETURN_FROM_PROFILE_EDIT -> {

                if(GlideLoadingFlag.getProfileFlag() == GlideLoadingFlag.FLAG_UPDATED) {

                    homePagerAdapter = HomePagerAdapter(supportFragmentManager)
                    viewPagerForHomeTab?.adapter = homePagerAdapter
                    viewPagerForHomeTab!!.currentItem = homeTab!!.selectedTabPosition

                }
            }

        }

    }


    private fun logoutRequest(token: String){
        val jsonObjectToLogout = JSONObject()
        jsonObjectToLogout.put("token", token)

        VolleyHttpService.logout(this, jsonObjectToLogout) { success ->
            if (success) {
                saveJWTToken("")
                Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_LONG).show()

            /** 이것은 로그아웃 후 어플 재실행 시 자동로그인 + 이전 캐싱 이미지가 로딩 되버리는 것을 방지. */
                val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
                val editorToClearLoginInfo = sharedPref.edit()
                editorToClearLoginInfo
                        .clear()
                        .apply()

                finish()

            } else {
                Toast.makeText(this, "로그아웃 실패", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveJWTToken(token: String){
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val editorToSaveJWTToken = sharedPref.edit()
        editorToSaveJWTToken
                .putString("token", token)
                .apply()
    }

    private fun loadJWTToken(): String{
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)

        return sharedPref.getString("token", "")
    }


    override fun onBackPressed() {

        if(System.currentTimeMillis() - timeToConfirmExit >= 2000){
            timeToConfirmExit = System.currentTimeMillis()
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 앱을 종료합니다.",Toast.LENGTH_SHORT).show();
        }
        else if(System.currentTimeMillis() - timeToConfirmExit < 2000){
            finishAffinity()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            RequestCodeCollection.REQUEST_RETURN_FROM_JOINED_CONTENT -> {

                genViewComponents(RequestCodeCollection.REQUEST_RETURN_FROM_JOINED_CONTENT)

            }

            RequestCodeCollection.REQUEST_RETURN_FROM_SEARCH -> {

                genViewComponents(RequestCodeCollection.REQUEST_RETURN_FROM_SEARCH)

            }

            RequestCodeCollection.REQUEST_RETURN_FROM_PROFILE_EDIT -> {

                genViewComponents(RequestCodeCollection.REQUEST_RETURN_FROM_PROFILE_EDIT)

            }

        }

    }
    private fun handleFCMRequest() {

        RequestCodeCollection.IS_FCM_FLAG = true

        intentToCommuinate = intent
        val contentNameFromFCM = intentToCommuinate?.getStringExtra("contentName")
        val fcmCategory = intentToCommuinate?.getStringExtra("fcm_category")

        /** 일반적인 로그인 상황 - intent에 별다른 정보가 저장안된다. */

        if (fcmCategory == null
                || intentToCommuinate?.getStringExtra("contentName") == null) {

            RequestCodeCollection.IS_FCM_FLAG = false

        }
        /** 아래는 전부 FCM통해 들어오는 상황 */
        else if (fcmCategory.equals("목표 달성 실패 알림")) {

            val goToContentsHome = Intent(this, ContentsHomeActivity::class.java)

            goToContentsHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            goToContentsHome.putExtra("fcm_category", "목표 달성 실패 알림")
            goToContentsHome.putExtra("contentName", contentNameFromFCM)

            startActivity(goToContentsHome)

        } else if (fcmCategory.equals("인증 시간이 얼마 남지 않았어요!")) {

            val goToContentsHome = Intent(this, ContentsHomeActivity::class.java)

            goToContentsHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            goToContentsHome.putExtra("fcm_category", "인증 시간이 얼마 남지 않았어요!")
            goToContentsHome.putExtra("contentName", contentNameFromFCM)

            startActivity(goToContentsHome)

        } else if (fcmCategory.equals("목표 달성 성공 알림")) {

            val goToContentsHome = Intent(this, ContentsHomeActivity::class.java)

            goToContentsHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            goToContentsHome.putExtra("fcm_category", "목표 달성 성공 알림")
            goToContentsHome.putExtra("contentName", contentNameFromFCM)

            startActivity(goToContentsHome)

        } else if (fcmCategory.equals("과반수의 반대로 인증에 실패하셨습니다")) {

            val goToContentsHome = Intent(this, ContentsHomeActivity::class.java)

            goToContentsHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            goToContentsHome.putExtra("fcm_category", "과반수의 반대로 인증에 실패하셨습니다")
            goToContentsHome.putExtra("contentName", contentNameFromFCM)
            goToContentsHome.putExtra("rejectUserArray", intentToCommuinate?.getStringExtra("rejectUserArray"))
            goToContentsHome.putExtra("rejectReasonArray", intentToCommuinate?.getStringExtra("rejectReasonArray"))

            startActivity(goToContentsHome)

        } else if (fcmCategory.equals("마지막 인증까지 성공하셨습니다! 컨텐츠 종료일에 보상을 받으실 수 있습니다.")) {

            val goToContentsHome = Intent(this, ContentsHomeActivity::class.java)

            goToContentsHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            goToContentsHome.putExtra("fcm_category", "마지막 인증까지 성공하셨습니다! 컨텐츠 종료일에 보상을 받으실 수 있습니다.")
            goToContentsHome.putExtra("contentName", contentNameFromFCM)

            startActivity(goToContentsHome)

        } else if (fcmCategory.equals("컨텐츠에 새로운 인증영상이 올라왔습니다!")) {

            val goToContentsHome = Intent(this, ContentsHomeActivity::class.java)

            goToContentsHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            goToContentsHome.putExtra("fcm_category", "컨텐츠에 새로운 인증영상이 올라왔습니다!")
            goToContentsHome.putExtra("contentName", contentNameFromFCM)

            startActivity(goToContentsHome)

        }
    }

}

/**

홈화면 정리

1. 일반적인 로그인상황 (O) REQUEST_DEFAULT_CREATION

2. FCM으로 들어가는 상황 (O) REQUEST_DEFAULT_CREATION

3. 프로필 이미지 수정 후 or  프로필 이름 등 텍스트 파트 수정 후 업데이트 필요한 상황

4. 컨텐츠에 새로 가입후 되돌아와서 업데이트 필요한 상황
SearchPager에서 ContentHome에 갓다가 돌아오는 경우

5. 참여 컨텐츠에 들어가서 인증 후 돌아오면 썸네일 업뎃이 필요

 */

