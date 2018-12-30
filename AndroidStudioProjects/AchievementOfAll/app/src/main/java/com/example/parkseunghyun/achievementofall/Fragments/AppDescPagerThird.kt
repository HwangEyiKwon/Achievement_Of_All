package com.example.parkseunghyun.achievementofall.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.parkseunghyun.achievementofall.R

/**
    REFACTORED
 */

// AppDescPagerThird
// 앱 설명 페이지의 Fragment
class AppDescPagerThird : Fragment() {

    private var appThirdDescView: View? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        appThirdDescView = inflater!!.inflate(R.layout.fragment_appstart_desc_3, container, false)

        return appThirdDescView
    }
}
