package com.example.parkseunghyun.achievementofall

//import android.support.v7.widget.RecyclerView

//import adapter.StoriesAdapter
//import model.StoriesModel
import adapter.StoriesAdapter
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.contents_pager_container.*
import model.StoriesModel


class ContentsHomeActivity : AppCompatActivity(), RecyclerViewClickListener {

    private val PROFILE = arrayOf(R.drawable.rdj, R.drawable.rocky, R.drawable.rock, R.drawable.will, R.drawable.hitler, R.drawable.mj, R.drawable.miketyson, R.drawable.jt, R.drawable.johnnydepp, R.drawable.jfk, R.drawable.barackobama)
    private val NAME = arrayOf("Sam", "Rick", "Richard", "Tony", "Bruce", "Steve", "Chandler", "Star", "Stark", "Joey", "Ross")
    private var storiesModelArrayList: ArrayList<StoriesModel>? = null
    private var recyclerView: RecyclerView? = null
    private var storiesAdapter: StoriesAdapter? = null
    private var text_joinedORnot: TextView? = null
    private var remainingTime: TextView? = null


    override fun recyclerViewListClicked(v: View, position: Int) {
        Toast.makeText(getApplicationContext(), "position is $position", Toast.LENGTH_LONG)
    }

    private var contentName: TextView ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contents_home)

        text_joinedORnot = findViewById(R.id.id_joined_OR_not)
        text_joinedORnot?.setText("참가중 or 미참가중")

        remainingTime = findViewById(R.id.ydh_remaining_time)
        remainingTime?.setText("남은 인증시간")

        contentName = findViewById(R.id.contentName)

        if(intent.getStringExtra("contentName")!=null){
            val name = intent.getStringExtra("contentName")
            contentName!!.setText(name)
        }

        recyclerView = findViewById(R.id.recystories)
        val layoutManager = LinearLayoutManager(this@ContentsHomeActivity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        storiesModelArrayList = ArrayList()

        for (i in PROFILE.indices) {
            val storiesModel = StoriesModel()
            storiesModel.profile = PROFILE[i]
            storiesModel.name = NAME[i]
            storiesModelArrayList!!.add(storiesModel)
        }

        storiesAdapter = StoriesAdapter(this@ContentsHomeActivity, storiesModelArrayList!!, this)
        recyclerView!!.adapter = storiesAdapter

        val viewPager = findViewById<ViewPager>(R.id.contents_pager_container)
        val pAdapter = adapter.ContentsPagerAdapter(supportFragmentManager)
        viewPager.adapter = pAdapter

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


}
