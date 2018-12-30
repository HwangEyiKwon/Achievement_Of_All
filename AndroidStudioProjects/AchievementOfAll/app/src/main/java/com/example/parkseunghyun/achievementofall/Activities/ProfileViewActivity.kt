package com.example.parkseunghyun.achievementofall.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.parkseunghyun.achievementofall.Configurations.GlobalVariables
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R
import org.json.JSONObject

// ProfileViewActivity
// 프로필 이미지 화면
// 이미지를 누를 경우 프로필 이미지를 확대해서 보여줍니다.
class ProfileViewActivity : AppCompatActivity() {

    private var globalVariables: GlobalVariables?= GlobalVariables()
    private var ipAddress: String = globalVariables!!.ipAddress

    var goOutButton: Button?= null
    var userProfileBig: ImageView?= null
    var userEmail:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_image_view)

        userProfileBig = findViewById(R.id.profile_image_big)
        goOutButton = findViewById(R.id.button_finish)
        userEmail = intent.getStringExtra("email")

        val jsonObjectForOtherUserInfo = JSONObject()
        jsonObjectForOtherUserInfo.put("email", userEmail)

        VolleyHttpService.getOtherUserInfo(this, jsonObjectForOtherUserInfo) { success ->

            Glide
                    .with(this)
                    .load("${ipAddress}/getOtherUserImage/"+userEmail)
                    .apply(RequestOptions().skipMemoryCache(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(userProfileBig)

        }

        goOutButton!!.setOnClickListener {

            finish()

        }

    }

}