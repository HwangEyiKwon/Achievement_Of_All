package com.example.parkseunghyun.achievementofall.Fragments

import adapter.JoinedContentsAdapter
import adapter.ThumbnailAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.parkseunghyun.achievementofall.Activities.HomeActivity
import com.example.parkseunghyun.achievementofall.Activities.ProfileEditActivity
import com.example.parkseunghyun.achievementofall.Activities.ProfileViewActivity
import com.example.parkseunghyun.achievementofall.Configurations.GlideLoadingFlag
import com.example.parkseunghyun.achievementofall.Configurations.GlobalVariables
import com.example.parkseunghyun.achievementofall.Configurations.RequestCodeCollection
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R
import kotlinx.android.synthetic.main.fragment_home_account.*
import model.JoinedContentsModel
import model.ThumbnailModel
import org.json.JSONObject
import java.util.*

/**
    REFARCTORED
    TODO: Glide Placeholder
 */

class HomeAccountPager : Fragment() {

    private var globalVariables: GlobalVariables?= GlobalVariables()
    private var ipAddress: String = globalVariables!!.ipAddress

    private var joinedContents = mutableListOf<String>()

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

    private var nickName: TextView ?=null
    private var email: TextView ?=null
    private var phoneNumber: TextView ?=null
    private var editProfileButton: ImageView ?= null

    private var userProfile: ImageView ?=null
    private var ifContentNotJoined:TextView? = null
    private var ifNoVideo:TextView? = null


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

        userProfile!!.setOnClickListener {

            val goToProfileImageView = Intent(activity, ProfileViewActivity::class.java)
            goToProfileImageView.putExtra("email", email!!.text)

            startActivity(goToProfileImageView)

        }

        ifContentNotJoined!!.setOnClickListener {

            if_no_contents_joined!!.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scaling))

        }

        ifNoVideo!!.setOnClickListener {

            if_no_video!!.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scaling))

        }

    }

    private fun initViewComponents() {

        nickName = homeAccountView!!.findViewById(R.id.name)
        email = homeAccountView!!.findViewById(R.id.email)
        phoneNumber = homeAccountView!!.findViewById(R.id.phoneNumber)
        editProfileButton = homeAccountView!!.findViewById(R.id.edit)
        userProfile = homeAccountView!!.findViewById(R.id.post_profile_image)
        ifContentNotJoined = homeAccountView!!.findViewById(R.id.if_no_contents_joined)
        ifNoVideo = homeAccountView!!.findViewById(R.id.if_no_video)

        val activity = activity as HomeActivity
        jwtToken = activity.jwtToken.toString()

        setUserInfo(jwtToken!!)

    }

    private fun setUserInfo(token: String) {

        val jsonObjectForSetUserInfo = JSONObject()
        jsonObjectForSetUserInfo.put("token", token)

        VolleyHttpService.getUserInfo(activity!!, jsonObjectForSetUserInfo) { success ->

            email!!.text = success.getString("email")
            nickName!!.text = success.getString("name").replace("+", " ")
            phoneNumber!!.text = success.getString("phoneNumber")

            /** 이미지가 아직 내 Device 내에 있다면 그냥 거기서 불러옴. else - 서버에 요청해서 불러옴 */
            if( ! loadUriForSelfCaching().equals("0") ) {

                Glide
                        .with(this)
                        .load(loadUriForSelfCaching())
                        .apply(RequestOptions().fitCenter())
                        .apply(RequestOptions().centerCrop())
                        .into(userProfile)

            } else {

                Glide
                        .with(this)
                        .load("${ipAddress}/getUserImage/" + jwtToken)
                        .apply(RequestOptions().fitCenter())
                        .apply(RequestOptions().centerCrop())
                        .apply(RequestOptions().skipMemoryCache(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .into(userProfile)

            }

            GlideLoadingFlag.setProfileWithImageFlag(GlideLoadingFlag.FLAG_NOT_UPDATED)
            GlideLoadingFlag.setProfileWithOutImageFlag(GlideLoadingFlag.FLAG_NOT_UPDATED)

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

        if(joinedContents.size == 0) {

            if_no_contents_joined.visibility = View.VISIBLE
            if_no_contents_joined.text = "하단의 찾기 기능을 통해\n다양한 컨텐츠를 찾아서\n참여 해보세요!"

        } else {

            if_no_contents_joined.visibility = View.GONE

        }

        joinedContentsAdapter = JoinedContentsAdapter(activity, joinedContentsModelArrayList!!)
        joinedContentsView!!.adapter = joinedContentsAdapter

    }

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

        if(videoList.size == 0) {

            if_no_video.visibility = View.VISIBLE
            if_no_video.text = "컨텐츠에 참여해 인증영상을 올려보세요!\n모달친구들과 서로의 계획실천을 확인해보아요!"

        } else {

            if_no_video.visibility = View.GONE

        }

        thumbnailAdapter = ThumbnailAdapter(activity, thumbnailModelList!!)
        thumbnailView!!.adapter = thumbnailAdapter

    }


    private fun loadUriForSelfCaching(): String {

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val imageUri = sharedPref.getString("imageUri","0")

        return imageUri

    }

}
