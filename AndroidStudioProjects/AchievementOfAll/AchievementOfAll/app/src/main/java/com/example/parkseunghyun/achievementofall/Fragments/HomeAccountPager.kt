package com.example.parkseunghyun.achievementofall.Fragments

import adapter.JoinedContentsAdapter
import adapter.ThumbnailAdapter
import android.app.Activity
import android.content.Intent
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.parkseunghyun.achievementofall.Activities.HomeActivity
import com.example.parkseunghyun.achievementofall.Activities.ProfileEditActivity
import com.example.parkseunghyun.achievementofall.Configurations.GlobalVariables
import com.example.parkseunghyun.achievementofall.Configurations.RequestCodeCollection
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.Interfaces.RecyclerViewClickListener
import com.example.parkseunghyun.achievementofall.R
import model.JoinedContentsModel
import model.ThumbnailModel
import org.json.JSONObject
import java.util.*

/*
    REFARCTORED
    TODO: Glide Placeholder
 */


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

    private var homeAccountView: View ?= null

    private var jwtToken: String ?= null

    // 사용자 정보 (TextView)
    private var nickName: TextView ?=null
    private var email: TextView ?=null
    private var phoneNumber: TextView ?=null
    private var editProfileButton: ImageView ?= null

    // 사용자 프로필 사진 (ImageView)
    private var userProfile: ImageView ?=null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        homeAccountView = inflater!!.inflate(R.layout.fragment_home_account, container, false)

        initViewComponents()

        initButtonListener()

        return homeAccountView
    }

    private fun initButtonListener() {

        editProfileButton!!.setOnClickListener {

            val intentForEditPage = Intent(activity, ProfileEditActivity::class.java)
            intentForEditPage.putExtra("name", nickName!!.text)
            intentForEditPage.putExtra("phoneNumber", phoneNumber!!.text)

            val contextToActivity = activity as Activity
            contextToActivity.startActivityForResult(intentForEditPage, RequestCodeCollection.REQUEST_RETURN_FROM_PROFILE_EDIT)

        }

    }

    override fun recyclerViewListClicked(v: View, position: Int) {}

    private fun initViewComponents() {

        nickName = homeAccountView!!.findViewById(R.id.name)
        email = homeAccountView!!.findViewById(R.id.email)
        phoneNumber = homeAccountView!!.findViewById(R.id.phoneNumber)
        editProfileButton = homeAccountView!!.findViewById(R.id.edit)
        userProfile = homeAccountView!!.findViewById(R.id.post_profile_image)

        // 사용자 jwt-token을 통해 사용자 페이지 setting
        val activity = activity as HomeActivity
        jwtToken = activity.jwtToken.toString()

        setUserInfo(jwtToken!!)

    }

    // 사용자 페이지 정보 Setting 함수
    private fun setUserInfo(token: String) {

        val jsonObjectForSetUserInfo = JSONObject()
        jsonObjectForSetUserInfo.put("token", token)

        VolleyHttpService.getUserInfo(activity!!, jsonObjectForSetUserInfo) { success ->

            // 사용자 프로필 정보 갱신
            email!!.text = success.getString("email")
            nickName!!.text = success.getString("name")
            phoneNumber!!.text = success.getString("phoneNumber")

//            val glideRequestOptions = RequestOptions()
//            glideRequestOptions.placeholder(GlideLoadinFlag.profileBitmap)

            // 사용자 프로필 사진 갱신
            Glide
                    .with(this)
                    .load("${ipAddress}/getUserImage/" + jwtToken)
                    .apply(RequestOptions().skipMemoryCache(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(userProfile)

//            GlideLoadinFlag.profileBitmap =  userProfile!!.drawable
//            GlideLoadinFlag.setProfileFlag(GlideLoadinFlag.FLAG_NOT_UPDATED)


            // 사용자 참여 컨텐츠 정보 갱신
            var contentList: JSONObject

            for(indexOfContentList in 0.. (success.getJSONArray("contentList").length() - 1)) {

                contentList = success.getJSONArray("contentList")[indexOfContentList] as JSONObject
                val contentName =  contentList.getString("contentName")
                joinedContents.add(contentName.toString())

                for(indexOfVideoPath in 0..(contentList.getJSONArray("videoPath").length() - 1)){

                    videoList.add(contentList.getJSONArray("videoPath").getJSONObject(indexOfVideoPath))
                    videoContentList.add(contentName)

                }

            }

            generateJoinedContentsView()

            generateVideoCollection()

        }

    }

    // 사용자 참여 컨텐츠 View 생성 함수
    private fun generateJoinedContentsView() {

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        joinedContentsView = homeAccountView!!.findViewById(R.id.id_my_contents)
        joinedContentsView!!.layoutManager = layoutManager
        joinedContentsView!!.itemAnimator = DefaultItemAnimator()
        joinedContentsModelArrayList = ArrayList()

        for (indexOfJoinedContents in joinedContents.indices) {

            val joinedContentsModel = JoinedContentsModel()
            joinedContentsModel.name = joinedContents[indexOfJoinedContents]
            joinedContentsModelArrayList!!.add(joinedContentsModel)

        }

        joinedContentsAdapter = JoinedContentsAdapter(activity, joinedContentsModelArrayList!!, this)
        joinedContentsView!!.adapter = joinedContentsAdapter

    }

    // 사용자 비디오 목록 View 생성 함수
    private fun generateVideoCollection() {

        val layoutManager: RecyclerView.LayoutManager
        layoutManager = GridLayoutManager(activity, 3)
        thumbnailView = homeAccountView!!.findViewById(R.id.id_thumbnail)
        thumbnailView!!.layoutManager = layoutManager
        thumbnailView!!.itemAnimator = DefaultItemAnimator()
        thumbnailModelList = ArrayList()

        for (indexOfVideoList in videoList.indices) {

            val thumbnailModel = ThumbnailModel()

            thumbnailModel.userToken = jwtToken
            thumbnailModel.who = "me"
            thumbnailModel.videoPath = videoList[indexOfVideoList]
            thumbnailModel.contentName = videoContentList[indexOfVideoList]

            thumbnailModelList!!.add(thumbnailModel)

        }

        thumbnailAdapter = ThumbnailAdapter(activity, thumbnailModelList!!)
        thumbnailView!!.adapter = thumbnailAdapter

    }

}
