package com.example.parkseunghyun.achievementofall

import adapter.ContentsPagerAdapter
import adapter.HomePagerAdapter
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import kotlinx.android.synthetic.main.contents_pager_container.*

class HomeActivity : AppCompatActivity() {

    private var viewPager: ViewPager? = null
    private var viewPagerContents: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        var id = 0

        if(id == 1){
            viewPagerContents = findViewById<ViewPager>(R.id.contents_pager_container)
            val adapter2 = ContentsPagerAdapter(supportFragmentManager)
            viewPagerContents?.adapter = adapter2
        }


        val toolbar = findViewById(R.id.id_toolbar_home) as Toolbar
        setSupportActionBar(toolbar)


        val tabLayout = findViewById(R.id.id_home_tab) as TabLayout
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home_outline))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_search))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_icons_info))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL



        viewPager = findViewById(R.id.home_pager_container)
        val adapter = HomePagerAdapter(supportFragmentManager)
        viewPager?.adapter = adapter
        viewPager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))



//        viewPagerContents = findViewById<ViewPager>(R.id.contents_pager_container)
//        val adapter2 = ContentsPagerAdapter(supportFragmentManager)
//        viewPagerContents?.adapter = adapter2



        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager?.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        viewPagerContents?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {

            }
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }
            override fun onPageSelected(p0: Int) {
                contents_circle_indicator.selectDot(p0)
            }
        })

//        contents_circle_indicator.createDotPanel(3, R.drawable.indicator_dot_off, R.drawable.indicator_dot_on, 0)


    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val id = item.itemId
//        return if (id == R.id.action_settings) {
//            true
//        } else super.onOptionsItemSelected(item)
//
//    }
}