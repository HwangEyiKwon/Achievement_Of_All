package com.example.parkseunghyun.achievementofall

import adapter.JoinedContentsAdapter
import adapter.ThumbnailAdapter
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import model.JoinedContentsModel
import model.ThumbnailModel
import org.json.JSONObject
import java.util.*

class HomeAccountPager : Fragment(), RecyclerViewClickListener {
    private val PROFILE = arrayOf(R.drawable.ns, R.drawable.diet, R.drawable.book)
    private val NAME = arrayOf("Smoking", "Diet", "Study")

    private val ACCOUNTPIC = arrayOf(R.drawable.selena12, R.drawable.nature1, R.drawable.nature2, R.drawable.nature3, R.drawable.selena1, R.drawable.selena2, R.drawable.selena3, R.drawable.nature4, R.drawable.nature5, R.drawable.nature6, R.drawable.selena4, R.drawable.selena5, R.drawable.selena6, R.drawable.selena7, R.drawable.selena8, R.drawable.selena9, R.drawable.selena10, R.drawable.selena11)

    private var joinedContentsView: RecyclerView? = null
    private var thumbnailView: RecyclerView? = null

    private var thumbnailAdapter: ThumbnailAdapter? = null
    private var thumbnailModelList: ArrayList<ThumbnailModel>? = null

    private var joinedContentsModelArrayList: ArrayList<JoinedContentsModel>? = null
    private var joinedContentsAdapter: JoinedContentsAdapter? = null

    private var view_: View ?= null
    private var homeAccountPagerContext: Context? = null

    //    static final Class<?>[] ACTIVITIES = { X_Home_no.class }; // 각각의 LIST_MENU의 원소에 대응되는 액티비티의 각 클래스 이름을 써줍니다.

    private var name: TextView ?=null
    private var email: TextView ?=null
    private var phoneNumber: TextView ?=null


    override fun recyclerViewListClicked(v: View, position: Int) {
        Toast.makeText(homeAccountPagerContext, "position is $position", Toast.LENGTH_LONG)
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        homeAccountPagerContext = activity
        view_ = inflater!!.inflate(R.layout.fragment_home_account, container, false)

        name = view_!!.findViewById<TextView>(R.id.name)
        email = view_!!.findViewById<TextView>(R.id.email)
        phoneNumber = view_!!.findViewById<TextView>(R.id.phoneNumber)

        val activity = activity as HomeActivity
        setUserInfo(activity.jwtToken.toString())

        // Code for Joined Contents View
        generateJoinedContentsView()

        // Code for Video Thumbnail collection
        generateVideoCollection()

        return view_
    }

    private fun setUserInfo(token: String){
        val jsonObject = JSONObject()
        jsonObject.put("token", token)
        VolleyHttpService.getUserInfo(homeAccountPagerContext!!, jsonObject){ success ->
            email!!.setText(success.getString("email"))
            name!!.setText(success.getString("name"))
            phoneNumber!!.setText(success.getString("phoneNumber"))
        }

    }
    private fun generateJoinedContentsView() {
        val layoutManager = LinearLayoutManager(homeAccountPagerContext, LinearLayoutManager.HORIZONTAL, false)
        joinedContentsView = view_!!.findViewById(R.id.id_my_contents)
        joinedContentsView!!.layoutManager = layoutManager
        joinedContentsView!!.itemAnimator = DefaultItemAnimator()
        joinedContentsModelArrayList = ArrayList()



        for (i in PROFILE.indices) {
            val joinedContentsModel = JoinedContentsModel()
            joinedContentsModel.profile = PROFILE[i]
            joinedContentsModel.name = NAME[i]
            joinedContentsModelArrayList!!.add(joinedContentsModel)
        }


        joinedContentsAdapter = JoinedContentsAdapter(homeAccountPagerContext!!, joinedContentsModelArrayList!!, this)
        joinedContentsView!!.adapter = joinedContentsAdapter

    }

    private fun generateVideoCollection() {

        val layoutManager: RecyclerView.LayoutManager
        thumbnailView = view_!!.findViewById(R.id.id_thumbnail)
        layoutManager = GridLayoutManager(homeAccountPagerContext, 3)
        thumbnailView!!.layoutManager = layoutManager
        thumbnailView!!.itemAnimator = DefaultItemAnimator()

        thumbnailModelList = ArrayList()

        for (i in ACCOUNTPIC.indices) {
            val thumbnailModel = ThumbnailModel()

            thumbnailModel.accountpic = ACCOUNTPIC[i]

            thumbnailModelList!!.add(thumbnailModel)
        }

        thumbnailAdapter = ThumbnailAdapter(homeAccountPagerContext!!, thumbnailModelList!!)
        thumbnailView!!.adapter = thumbnailAdapter

    }


}
