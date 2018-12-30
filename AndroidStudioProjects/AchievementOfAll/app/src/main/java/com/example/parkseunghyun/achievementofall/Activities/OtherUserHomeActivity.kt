package com.example.parkseunghyun.achievementofall

import adapter.JoinedContentsAdapter
import adapter.ThumbnailAdapter
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.parkseunghyun.achievementofall.Activities.ProfileViewActivity
import com.example.parkseunghyun.achievementofall.Configurations.GlobalVariables
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import de.hdodenhof.circleimageview.CircleImageView
import model.JoinedContentsModel
import model.ThumbnailModel
import org.json.JSONObject
import java.util.*

/**
    REFACTORED
 */

// OtherUserHomeActivity
// 타 사용자 홈 화면
class OtherUserHomeActivity : AppCompatActivity() {

    private var globalVariables: GlobalVariables?= GlobalVariables()
    private var ipAddress: String = globalVariables!!.ipAddress

    private var otherUserNameView:TextView? = null
    private var otherUserName:String? = null
    private var otherUserEmail: String? = null
    private var otherUserProfile: CircleImageView ?= null

    private var joinedContentsView: RecyclerView? = null
    private var joinedContentsModelArrayList: ArrayList<JoinedContentsModel>? = null
    private var joinedContents = mutableListOf<String>()
    private var joinedContentsAdapter: JoinedContentsAdapter? = null

    private var videoList = mutableListOf<JSONObject>()
    private var videoContentList = mutableListOf<String>()
    private var thumbnailView: RecyclerView? = null
    private var thumbnailAdapter: ThumbnailAdapter? = null
    private var thumbnailModelList: ArrayList<ThumbnailModel>? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_user_home)

        // 초기화
        initViewComponents()

    }

    // initViewComponent
    // 메인 페이지의 view에 있는 각 요소들을 초기화합니다.
    private fun initViewComponents() {

        otherUserNameView = findViewById(R.id.other_user_name)
        otherUserProfile = findViewById(R.id.other_user_profile_image)

        // intent를 통해 타 사용자 정보를 받아옵니다.
        if (intent.getStringExtra("email") != null) {

            otherUserEmail = intent.getStringExtra("email")
            otherUserName = intent.getStringExtra("userName")
            otherUserNameView!!.text = otherUserName

        }

        // 받아온 정보를 통해 타 사용자 정보를 보여줍니다.
        setOtherUserInfo(otherUserEmail!!)

        // 타 사용자의 프로필 이미지를 클릭할 경
        otherUserProfile!!.setOnClickListener {

            val goToProfileImageView = Intent(this, ProfileViewActivity::class.java)
            goToProfileImageView.putExtra("email", otherUserEmail)

            startActivity(goToProfileImageView)
        }

    }


    // setOtherUserInfo
    // 타 사용자의 정보를 설정합니다.
    private fun setOtherUserInfo(email: String) {

        val jsonObjectForOtherUserInfo = JSONObject()
        jsonObjectForOtherUserInfo.put("email", email)

        VolleyHttpService.getOtherUserInfo(this, jsonObjectForOtherUserInfo) { success ->

            Glide
                    .with(this)
                    .load("${ipAddress}/getOtherUserImage/"+otherUserEmail)
                    .apply(RequestOptions().skipMemoryCache(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .thumbnail(0.1f)
                    .into(otherUserProfile)


            var contentList: JSONObject
            var contentName: String

            // 타 사용자가 참가 중인 컨텐츠 리스트
            for(indexOfContentList in 0.. (success.getJSONArray("contentList").length() - 1)) {

                contentList = success.getJSONArray("contentList")[indexOfContentList] as JSONObject
                contentName = contentList.getString("contentName")

                joinedContents.add(contentName)

                // 비디오 리스트
                for(indexOfVideoList in 0..(contentList.getJSONArray("videoPath").length() - 1)){

                    videoList.add(contentList.getJSONArray("videoPath").getJSONObject(indexOfVideoList))
                    videoContentList.add(contentName)

                }

            }

            // 스토리 및 비디오 생성
            generateJoinedContentsView()
            generateVideoCollection()
        }

    }

    // generateJoinedContentsView
    // 타 사용자가 참가 중인 컨텐츠 리스트를 통해 상단에 스토리를 생성합니다.
    private fun generateJoinedContentsView() {

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        joinedContentsView = findViewById(R.id.id_other_user_contents)
        joinedContentsView!!.layoutManager = layoutManager
        joinedContentsView!!.itemAnimator = DefaultItemAnimator()
        joinedContentsModelArrayList = ArrayList()

        for (indexOfJoinedContents in joinedContents.indices) {

            val joinedContentsModel = JoinedContentsModel()
            joinedContentsModel.name = joinedContents.get(indexOfJoinedContents)
            joinedContentsModelArrayList!!.add(joinedContentsModel)

        }

        joinedContentsAdapter = JoinedContentsAdapter(this, joinedContentsModelArrayList!!)
        joinedContentsView!!.adapter = joinedContentsAdapter

    }

    // generateVideoCollection
    // 타 사용자의 비디오 리스트를 통해 비디오를 생성합니다.
    private fun generateVideoCollection() {

        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 3)

        thumbnailView = findViewById(R.id.id_other_thumbnail)
        thumbnailView!!.layoutManager = layoutManager
        thumbnailView!!.itemAnimator = DefaultItemAnimator()
        thumbnailModelList = ArrayList()

        for (indexOfVideoList in videoList.indices) {

            val thumbnailModel = ThumbnailModel()
            thumbnailModel.userEmail = otherUserEmail
            thumbnailModel.who = "other"
            thumbnailModel.videoPath = videoList.get(indexOfVideoList)
            thumbnailModel.contentName = videoContentList.get(indexOfVideoList)
            thumbnailModelList!!.add(thumbnailModel)

        }

        thumbnailAdapter = ThumbnailAdapter(this, thumbnailModelList!!)
        thumbnailView!!.adapter = thumbnailAdapter

    }

}