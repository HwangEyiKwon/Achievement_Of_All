package com.example.parkseunghyun.achievementofall

import adapter.ContentsPagerAdapter
import adapter.HomePagerAdapter
import android.app.FragmentManager
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
    private var viewPagerContents: ViewPager? = null

    private var adapter: HomePagerAdapter? =null
    private var adapter2: ContentsPagerAdapter? =null

    private var tabLayout: TabLayout? = null


    public var userEmail: String ?= null

    override fun onStart() {
        super.onStart()
        createHomePager()
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val afterLogin = intent.getStringExtra("email")

        val tbh = findViewById<View>(R.id.home_layout).findViewById<View>(R.id.id_toolbar_home).findViewById<View>(R.id.toolbar_layout).findViewById<ImageView>(R.id.logoutButton)

        tbh.isClickable = true
        tbh.setOnClickListener {
            logout(afterLogin)
            Toast.makeText(this, "로그아웃", Toast.LENGTH_SHORT).show()
            startActivity<LoginActivity>()
        }

        getUserInfo(afterLogin);



        val toolbar = findViewById(R.id.id_toolbar_home) as Toolbar
        setSupportActionBar(toolbar)

        var tabLayout = findViewById(R.id.id_home_tab) as TabLayout

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home_outline))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_search))
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_icons_info))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        println("ffffffffffffffffff")


//        createContentsPager()

        viewPager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
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
        createHomePager()
        val userEmailText = supportFragmentManager.findFragmentById(R.id.tab_fragment2)

        println("ㅁㄹㅇㄴ "+userEmailText )
    }
    fun createHomePager(){
        println("123")
        viewPager = findViewById(R.id.home_pager_container)
        adapter = HomePagerAdapter(supportFragmentManager)
        viewPager?.adapter = adapter
        println(viewPager?.adapter)
        println("Home "+ supportFragmentManager.fragments)
    }

    fun createContentsPager(){
        println("123")
        viewPagerContents = findViewById(R.id.contents_pager_container)
        adapter2 = ContentsPagerAdapter(supportFragmentManager)
        viewPagerContents?.adapter = adapter2
        println("Contents "+supportFragmentManager.fragments)

//        contents_circle_indicator.createDotPanel(3, R.drawable.indicator_dot_off, R.drawable.indicator_dot_on, 0)
    }

    fun destroyAllFragment(){
//        val layout = findViewById(R.id.containers) as FrameLayout
//        layout.removeAllViews()
//        println("destroy")
//        viewPager?.adapter.destroyItem()

//
//        println(supportFragmentManager.fragments)
//        println(supportFragmentManager.getBackStackEntryCount())
//        while(fragmentManager.getBackStackEntryCount() > 0) { fragmentManager.popBackStackImmediate(); }
        val fm = supportFragmentManager
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

//
//        println("asdfasfasdfasdfasdfasdf"+fm.backStackEntryCount)
//        fm.beginTransaction().remove(supportFragmentManager.fragments[0]).commit()
//        fm.executePendingTransactions()
//        println("ㅅㅂ: "+fm.fragments)
//        fm.beginTransaction().remove(supportFragmentManager.fragments[0]).commit()
//        fm.executePendingTransactions()
//        println("ㅅㅂ2: "+fm.fragments)
//        fm.beginTransaction().remove(supportFragmentManager.fragments[0]).commit()
//        fm.executePendingTransactions()
//        println("ㅅㅂ3: "+fm.fragments)
//        fm.beginTransaction().remove(supportFragmentManager.fragments[0]).commit()
//        fm.executePendingTransactions()
        println("ㅅㅂ3: "+fm.fragments)

//        fm.beginTransaction().remove(supportFragmentManager.fragments[2]).commit()
//        fm.beginTransaction().remove(supportFragmentManager.findFragmentById(R.id.home_pager_container)).commit()
//        for (fragment in fm.fragments) {
//
//
//            fm.beginTransaction().remove(fragment).commit()
//
//            fm.popBackStack()
////            supportFragmentManager.executePendingTransactions()
//
////            if(supportFragmentManager.fragments!=null )
////                supportFragmentManager.popBackStack()
//
//            println("f: "+fragment)
//            println("fs: "+fm.fragments)
//
//
//        }
//        fm.beginTransaction().commit()


//        supportFragmentManager.beginTransaction().remove(supportFragmentManager.findFragmentById(R.id.contents_pager_container)).commit()
//        supportFragmentManager.beginTransaction().remove(supportFragmentManager.findFragmentById(R.id.home_pager_container)).commit()


//        viewPager?.getAdapter()?.notifyDataSetChanged();
//        viewPager?.setAdapter(adapter2)
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
    private fun getUserInfo(email: String){

        val jsonObject = JSONObject()
        jsonObject.put("email", email)

        VolleyHttpService.getUserInfo(this, jsonObject){ success ->
            println("받은것은?: "+success)
            userEmail = success.getString("email")
        }
    }
    private fun setUserInfo(){

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