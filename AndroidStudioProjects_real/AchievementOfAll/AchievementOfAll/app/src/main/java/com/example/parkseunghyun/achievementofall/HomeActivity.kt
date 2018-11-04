package com.example.parkseunghyun.achievementofall

import adapter.ContentsPagerAdapter
import adapter.HomePagerAdapter
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.contents_pager_container.*
import org.jetbrains.anko.startActivity
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

        val tbh = findViewById<View>(R.id.home_layout).findViewById<View>(R.id.id_toolbar_home).findViewById<View>(R.id.toolbar_layout).findViewById<ImageView>(R.id.logoutButton)
          println("tbh")
        println(tbh)
//        tbh = tbh.findViewById<View>(R.id.toolbar_layout).findViewById<ImageView>(R.id.toolbar_frag).findViewById<ImageView>(R.id.logoutButton)
//        println("tbh2")
//        println(tbh)
        tbh.isClickable = true

        tbh.setOnClickListener {
            logout(afterLogin)
            Toast.makeText(this, "로그아웃", Toast.LENGTH_SHORT).show()
            startActivity<LoginActivity>()
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

//    override fun onStart() {
//        super.onStart()
//        initializePlayer()
//    }
    fun initializePlayer(){
        // Create a default TrackSelector
        val bandwidthMeter =  DefaultBandwidthMeter();
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter);
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory);

        //Initialize the player
        val player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        //Initialize simpleExoPlayerView
        val simpleExoPlayerView = findViewById(R.id.simpleExoPlayerView) as SimpleExoPlayerView

           simpleExoPlayerView.setPlayer(player)

                // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "CloudinaryExoplayer"));

        // Produces Extractor instances for parsing the media data.
        val extractorsFactory = DefaultExtractorsFactory();

        // This is the MediaSource representing the media to be played.
        val videoUri = Uri.parse("http://192.168.3.211:3000/video");
        val videoSource =  ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);

        // Prepare the player with the source.
        player.prepare(videoSource);

    }


}