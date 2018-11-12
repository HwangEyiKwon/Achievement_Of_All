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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import model.JoinedContentsModel
import model.ThumbnailModel
import org.json.JSONObject
import java.util.*



class HomeAccountPager : Fragment(), RecyclerViewClickListener {

    private var joinedContents = mutableListOf<String>()

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

    private var jwtToken: String ?= null
    private var name: TextView ?=null
    private var email: TextView ?=null
    private var phoneNumber: TextView ?=null

    private var profile: ImageView ?=null

    override fun recyclerViewListClicked(v: View, position: Int) {
        Toast.makeText(homeAccountPagerContext, "position is $position", Toast.LENGTH_LONG)
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        homeAccountPagerContext = activity
        view_ = inflater!!.inflate(R.layout.fragment_home_account, container, false)

        name = view_!!.findViewById(R.id.name)
        email = view_!!.findViewById(R.id.email)
        phoneNumber = view_!!.findViewById(R.id.phoneNumber)

        profile = view_!!.findViewById(R.id.post_profile_image)

        val activity = activity as HomeActivity
        jwtToken = activity.jwtToken.toString()
        setUserInfo(jwtToken!!)

//        val requestOptions = RequestOptions()
//        requestOptions.isMemoryCacheable
//        Glide.with(context).setDefaultRequestOptions(requestOptions).load("http://192.168.3.211:3000/video/").into(profile)
//

        return view_
    }

    private fun setUserInfo(token: String){
        val jsonObject = JSONObject()
        jsonObject.put("token", token)

        VolleyHttpService.getUserInfo(homeAccountPagerContext!!, jsonObject){ success ->

            // 내 정보 갱신
            email!!.setText(success.getString("email"))
            name!!.setText(success.getString("name"))
            phoneNumber!!.setText(success.getString("phoneNumber"))
            Glide.with(this).load("http://192.168.3.211:3000/getUserImage/"+jwtToken).into(profile)


            var contentList: JSONObject ?= null

//            // 내 컨텐츠 정보 갱신
//            for(i in 0.. (success.getJSONArray("contentList").length()-1)){
//                contentList = success.getJSONArray("contentList")[i] as JSONObject
//                var contentName =  contentList.getString("contentName")
//                joinedContents?.add(contentName.toString())
//            }

            // Code for Joined Contents View
            generateJoinedContentsView()

            // Code for Video Thumbnail collection
            generateVideoCollection()
        }

    }
    private fun generateJoinedContentsView() {
        val layoutManager = LinearLayoutManager(homeAccountPagerContext, LinearLayoutManager.HORIZONTAL, false)
        joinedContentsView = view_!!.findViewById(R.id.id_my_contents)
        joinedContentsView!!.layoutManager = layoutManager
        joinedContentsView!!.itemAnimator = DefaultItemAnimator()
        joinedContentsModelArrayList = ArrayList()



        for (i in joinedContents.indices) {
            val joinedContentsModel = JoinedContentsModel()
            joinedContentsModel.name = joinedContents?.get(i)
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
