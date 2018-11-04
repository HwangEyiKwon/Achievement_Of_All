package com.example.parkseunghyun.achievementofall

import adapter.ContentsPagerAdapter
import adapter.HomePagerAdapter
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.contents_pager_container.*
import org.json.JSONObject

class HomeActivity : AppCompatActivity() {

    private var viewPager: ViewPager? = null
//    private var viewPagerContents: ViewPager? = null

    private var adapter: HomePagerAdapter? =null
    private var adapter2: ContentsPagerAdapter? =null
    private var tabLayout: TabLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val afterLogin = intent.getStringExtra("email")

        val tbh = findViewById(R.id.id_toolbar_home) as View

        tbh.findViewById<ImageView>(R.id.logoutButton)
        
        tbh.setOnClickListener {
            println("zzzzzzzzzzzzzzzzzzzz")
        }

        val toolbar = findViewById(R.id.id_toolbar_home) as Toolbar
        setSupportActionBar(toolbar)

        var tabLayout = findViewById(R.id.id_home_tab) as TabLayout

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home_outline))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_search))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_icons_info))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        println("ffffffffffffffffff")
        createHomePager()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager?.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {

            }
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }
            override fun onPageSelected(p0: Int) {
                contents_circle_indicator.selectDot(p0)
            }
        })

    }

    fun createContentsPager(){
        viewPager = findViewById<ViewPager>(R.id.contents_pager_container)
        adapter2 = ContentsPagerAdapter(supportFragmentManager)
        viewPager?.adapter = adapter2
        contents_circle_indicator.createDotPanel(3, R.drawable.indicator_dot_off, R.drawable.indicator_dot_on, 0)
    }
    fun createHomePager(){
        viewPager = findViewById(R.id.home_pager_container)
        adapter = HomePagerAdapter(supportFragmentManager)
        viewPager?.adapter = adapter
        viewPager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
    }
    fun destroyContentsPager(){
//        viewPagerContents?.isSaveFromParentEnabled()
    }
    fun destroyHomePager(){
        viewPager!!.isSaveFromParentEnabled =false
//        viewPager?.isSaveFromParentEnabled()
//        viewPager?.adapter?.destroyItem(home_pager_container,0,0)
//        viewPager?.isSaveFromParentEnabled = false;
//        viewPager.ondetac
//        viewPager = null
//        adapter?.notifyDataSetChanged()
        adapter?.notifyDataSetChanged();
        viewPager?.setAdapter(adapter);
    }
    private fun logout(email: String){

        val jsonObject = JSONObject()
        jsonObject.put("email", email)

        VolleyHttpService.logout(this, jsonObject){ success ->
            if(success){
                Toast.makeText(this, "토큰  성공", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, "토큰  실패", Toast.LENGTH_LONG).show()
            }
        }
    }


}