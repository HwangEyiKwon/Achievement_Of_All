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

// AppDescPagerSecond
// 앱 설명 페이지의 Fragment
class AppDescPagerSecond : Fragment() {

    private var appSecondDescView: View? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        appSecondDescView = inflater!!.inflate(R.layout.fragment_appstart_desc_2, container, false)

        return appSecondDescView
    }
}
