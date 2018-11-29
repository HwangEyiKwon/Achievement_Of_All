package com.example.parkseunghyun.achievementofall

import adapter.JoinedContentsAdapter
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import kotlinx.android.synthetic.main.contents_pager_container.*
import model.JoinedContentsModel
import org.json.JSONObject
import java.util.ArrayList

class OtherUserHomeActivity : AppCompatActivity() {


    private var otherUserNameView:TextView? = null
    private var otherUserName:String? = null
    private var otherUser: String? = null
    private var joinedContentsView: RecyclerView? = null
    private var joinedContentsModelArrayList: ArrayList<JoinedContentsModel>? = null
    private var joinedContents = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.other_user_home_layout)

        otherUserNameView = findViewById(R.id.other_user_name)


        if(intent.getStringExtra("email")!=null){
            otherUser = intent.getStringExtra("email")
            otherUserName = intent.getStringExtra("userName")

            otherUserNameView!!.setText(otherUserName)
//            otherUserParticipatedInfo()
            println("TEST_____" + otherUserName)
        }
    }




}