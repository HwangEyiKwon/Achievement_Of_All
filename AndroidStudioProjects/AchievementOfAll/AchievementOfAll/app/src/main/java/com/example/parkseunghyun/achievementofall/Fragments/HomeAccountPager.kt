package com.example.parkseunghyun.achievementofall.Fragments

import adapter.JoinedContentsAdapter
import adapter.ThumbnailAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.example.parkseunghyun.achievementofall.Activities.HomeActivity
import com.example.parkseunghyun.achievementofall.Activities.ProfileEditActivity
import com.example.parkseunghyun.achievementofall.Configurations.GlobalVariables
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.Interfaces.RecyclerViewClickListener
import com.example.parkseunghyun.achievementofall.R
import model.JoinedContentsModel
import model.ThumbnailModel
import org.json.JSONObject
import java.util.*



class HomeAccountPager : Fragment(), RecyclerViewClickListener {

    // 서버 ip 주소
    private var globalVariables: GlobalVariables?= GlobalVariables()
    private var ipAddress: String = globalVariables!!.ipAddress

    // 사용자의 참여 컨텐츠
    private var joinedContents = mutableListOf<String>()

    // 사용자의 비디오 목록
    private var videoList = mutableListOf<JSONObject>()
    private var videoContentList = mutableListOf<String>()


    private var joinedContentsView: RecyclerView? = null
    private var thumbnailView: RecyclerView? = null

    private var thumbnailAdapter: ThumbnailAdapter? = null
    private var thumbnailModelList: ArrayList<ThumbnailModel>? = null

    private var joinedContentsModelArrayList: ArrayList<JoinedContentsModel>? = null
    private var joinedContentsAdapter: JoinedContentsAdapter? = null

    private var view_: View ?= null
    private var homeAccountPagerContext: Context? = null

    val REQUEST_FROM_JCA = 10101
    val REQUEST_FROM_SEARCH = 1010
    val REQUEST_FROM_EDIT = 333


    //    static final Class<?>[] ACTIVITIES = { X_Home_no.class }; // 각각의 LIST_MENU의 원소에 대응되는 액티비티의 각 클래스 이름을 써줍니다.

    // 사용자의 jwt-token
    private var jwtToken: String ?= null

    // 사용자 정보 (TextView)
    private var name: TextView ?=null
    private var email: TextView ?=null
    private var phoneNumber: TextView ?=null
    private var editButton: ImageView ?= null

    // 사용자 프로필 사진 (ImageView)
    private var profile: ImageView ?=null

    override fun recyclerViewListClicked(v: View, position: Int) {
        Toast.makeText(homeAccountPagerContext, "position is $position", Toast.LENGTH_LONG)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        homeAccountPagerContext = activity
        view_ = inflater!!.inflate(R.layout.fragment_home_account, container, false)

        // xml로부터 TextView 설정
        name = view_!!.findViewById(R.id.name)
        email = view_!!.findViewById(R.id.email)
        phoneNumber = view_!!.findViewById(R.id.phoneNumber)

        editButton = view_!!.findViewById(R.id.edit)

        // xml로부터 ImageView 설정
        profile = view_!!.findViewById(R.id.post_profile_image)

        // 사용자 jwt-token을 통해 사용자 페이지 setting
        val activity = activity as HomeActivity
        jwtToken = activity.jwtToken.toString()
        setUserInfo(jwtToken!!)

        editButton!!.setOnClickListener {
            println("editedit")
            Glide.get(context).clearMemory()
            val goToEditPage = Intent(homeAccountPagerContext, ProfileEditActivity::class.java)

            goToEditPage.putExtra("name", name!!.text)
            goToEditPage.putExtra("phoneNumber",phoneNumber!!.text)

            val contextToActivity = homeAccountPagerContext as Activity
            contextToActivity.startActivityForResult(goToEditPage, REQUEST_FROM_EDIT)

        }

        return view_
    }

    // 사용자 페이지 정보 Setting 함수
    private fun setUserInfo(token: String) {

        val jsonObject = JSONObject()
        jsonObject.put("token", token)

        VolleyHttpService.getUserInfo(homeAccountPagerContext!!, jsonObject) { success ->

            // 사용자 정보 갱신
            email!!.setText(success.getString("email"))
            name!!.setText(success.getString("name"))
            phoneNumber!!.setText(success.getString("phoneNumber"))

            val requestOptions = RequestOptions()
//            requestOptions.placeholder(R.drawable.)

            // 사용자 프로필 사진 갱신
            Glide
                    .with(this)
                    .setDefaultRequestOptions(requestOptions)
                    .load("${ipAddress}/getUserImage/" + jwtToken)
                    .apply(RequestOptions().skipMemoryCache(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(profile)





            println("너 갱신하니?")

            // 사용자 참여 컨텐츠 정보 갱신
            var contentList: JSONObject
            for(i in 0.. (success.getJSONArray("contentList").length()-1)){
                contentList = success.getJSONArray("contentList")[i] as JSONObject
                var contentName =  contentList.getString("contentName")
                joinedContents?.add(contentName.toString())

                for(i in 0..(contentList.getJSONArray("videoPath").length()-1)){
                    videoList?.add(contentList.getJSONArray("videoPath").getJSONObject(i))
                    videoContentList?.add(contentName)
                }
            }

            // 사용자 참여 컨텐츠 View 생성
            generateJoinedContentsView()

            // 사용자 비디오 목록 View 생성
            generateVideoCollection()
        }

    }
    // 사용자 참여 컨텐츠 View 생성 함수
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

    // 사용자 비디오 목록 View 생성 함수
    private fun generateVideoCollection() {

        val layoutManager: RecyclerView.LayoutManager
        thumbnailView = view_!!.findViewById(R.id.id_thumbnail)
        layoutManager = GridLayoutManager(homeAccountPagerContext, 3)
        thumbnailView!!.layoutManager = layoutManager
        thumbnailView!!.itemAnimator = DefaultItemAnimator()

        thumbnailModelList = ArrayList()

        for (i in videoList.indices) {
            val thumbnailModel = ThumbnailModel()

            thumbnailModel.userToken = jwtToken
            thumbnailModel.who = "me"
            thumbnailModel.videoPath = videoList?.get(i)
            thumbnailModel.contentName = videoContentList?.get(i)

            thumbnailModelList!!.add(thumbnailModel)
        }

        thumbnailAdapter = ThumbnailAdapter(homeAccountPagerContext!!, thumbnailModelList!!)
        thumbnailView!!.adapter = thumbnailAdapter

    }
}
