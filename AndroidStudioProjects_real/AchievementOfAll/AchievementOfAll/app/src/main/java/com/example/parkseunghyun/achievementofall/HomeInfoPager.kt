package com.example.parkseunghyun.achievementofall

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class HomeInfoPager : Fragment() {

    private var view_: View? = null
    private var homeInfoPagerContext: Context? = null

    private var appInfo: TextView ?= null
    private var noticeInfo: TextView ?= null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        homeInfoPagerContext = activity
        view_ = inflater!!.inflate(R.layout.fragment_home_info, container, false)
        appInfo = view_!!.findViewById<TextView>(R.id.appInfo)
        noticeInfo = view_!!.findViewById<TextView>(R.id.noticeInfo)

        setAppInfo()

        return view_
    }
    private fun setAppInfo() {
        VolleyHttpService.getAppInfo(homeInfoPagerContext!!){ success ->

            println(success)
            appInfo?.setText(success.getString("appInfo"))
            noticeInfo?.setText(success.getString("noticeInfo"))
        }

    }
}
