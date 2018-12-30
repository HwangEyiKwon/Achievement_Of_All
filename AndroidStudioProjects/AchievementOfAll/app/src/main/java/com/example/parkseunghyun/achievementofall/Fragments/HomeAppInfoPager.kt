package com.example.parkseunghyun.achievementofall.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R

/**
    REFARCTORED
 */

// HomeAppInfoPager
// 홈 화면의 세번째 페이지
// 앱 정보 및 공지사항
class HomeAppInfoPager : Fragment() {

    private var homeAppInfoView: View? = null

    private var appInfo: TextView ?= null
    private var noticeInfo: TextView ?= null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        homeAppInfoView = inflater!!.inflate(R.layout.fragment_home_info, container, false)

        initViewComponents()

        setAppInfo()

        return homeAppInfoView
    }

    // initViewComponent
    // 페이지의 view에 있는 각 요소들을 초기화합니다.
    private fun initViewComponents() {

        appInfo = homeAppInfoView!!.findViewById(R.id.appInfo)
        noticeInfo = homeAppInfoView!!.findViewById(R.id.noticeInfo)

        val scrollLayout = homeAppInfoView!!.findViewById<NestedScrollView>(R.id.info_layout)
        scrollLayout.isVerticalScrollBarEnabled = true

    }

    // setAppInfo
    // 앱 정보를 설정합니다.
    private fun setAppInfo() {

        VolleyHttpService.getAppInfo(activity){ success ->

            appInfo?.setText(success.getString("appInfo"))
            noticeInfo?.setText(success.getString("noticeInfo"))

        }
    }
}
