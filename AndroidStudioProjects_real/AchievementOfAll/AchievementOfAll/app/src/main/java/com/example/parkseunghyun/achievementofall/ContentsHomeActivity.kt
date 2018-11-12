package com.example.parkseunghyun.achievementofall

//import android.support.v7.widget.RecyclerView

//import adapter.StoriesAdapter
//import model.StoriesModel
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.contents_pager_container.*


class ContentsHomeActivity : AppCompatActivity(), View.OnClickListener {
//    private val PROFILE = arrayOf(R.drawable.rdj, R.drawable.rocky, R.drawable.rock, R.drawable.will, R.drawable.hitler, R.drawable.mj, R.drawable.miketyson, R.drawable.jt, R.drawable.johnnydepp, R.drawable.jfk, R.drawable.barackobama)
//    private val NAME = arrayOf("Sam", "Rick", "Richard", "Tony", "Bruce", "Steve", "Chandler", "Star", "Stark", "Joey", "Ross")

//    private var storiesModelArrayList: ArrayList<StoriesModel>? = null
//    private var recyclerView: RecyclerView? = null
//    private var storiesAdapter: StoriesAdapter? = null


//    internal var homelinear: LinearLayout? = null
//    internal var searchlinear: LinearLayout? = null
//    internal var profilelinear: LinearLayout? = null
//
//    internal var home: ImageView? = null
//    internal var search: ImageView? = null
//    internal var profile: ImageView? = null

    private var contentName: TextView ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contents_home)

        contentName = findViewById(R.id.contentName)

        if(intent.getStringExtra("contentName")!=null){
            val name = intent.getStringExtra("contentName")
            contentName!!.setText(name)
        }
//        homelinear = findViewById(R.id.homelinear)
//        searchlinear = findViewById(R.id.searchlinear)
//        profilelinear = findViewById(R.id.profilelinear)
//
//        home = findViewById(R.id.hometool)
//        search = findViewById(R.id.searchtool)
//        profile = findViewById(R.id.profiletool)

        /////Code for Circular Stories
//        recyclerView = findViewById(R.id.recystories)
//        val layoutManager = LinearLayoutManager(this@ContentsHomeActivity, LinearLayoutManager.HORIZONTAL, false)
//        recyclerView!!.layoutManager = layoutManager
//        recyclerView!!.itemAnimator = DefaultItemAnimator()
//        storiesModelArrayList = ArrayList()

//        for (i in PROFILE.indices) {
//            val storiesModel = StoriesModel()
//            storiesModel.profile = PROFILE[i]
//            storiesModel.name = NAME[i]
//            storiesModelArrayList!!.add(storiesModel)
//        }

//        storiesAdapter = StoriesAdapter(this@ContentsHomeActivity, storiesModelArrayList!!)
//        recyclerView!!.adapter = storiesAdapter


        val viewPager = findViewById<ViewPager>(R.id.contents_pager_container)
        val pAdapter = adapter.ContentsPagerAdapter(supportFragmentManager)
        viewPager.adapter = pAdapter


//        homelinear?.setOnClickListener(this)
//        searchlinear?.setOnClickListener(this)
//        profilelinear?.setOnClickListener(this)


        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {

            }
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }
            override fun onPageSelected(p0: Int) {
                contents_circle_indicator.selectDot(p0)
            }
        })

        contents_circle_indicator.createDotPanel(3, R.drawable.indicator_dot_off, R.drawable.indicator_dot_on, 0)

    }


    override fun onClick(v: View) {

//        when (v.id) {
//
//            R.id.homelinear -> {
//
//                home?.setImageResource(R.drawable.ic_home_black)
//                search?.setImageResource(R.drawable.ic_search)
//                profile?.setImageResource(R.drawable.ic_icons_info)
//                Toast.makeText(applicationContext, "Toast is delicious.", Toast.LENGTH_LONG).show()
//
//            }
//
//            R.id.searchlinear -> {
//
//                home?.setImageResource(R.drawable.ic_home_outline)
//                search?.setImageResource(R.drawable.ic_search_black)
//                profile?.setImageResource(R.drawable.ic_icons_info)
//            }
//
//
//            R.id.profilelinear -> {
//
//                home?.setImageResource(R.drawable.ic_home_outline)
//                search?.setImageResource(R.drawable.ic_search)
//                profile?.setImageResource(R.drawable.ic_icons_info_black)
//            }
//        }

    }
}
