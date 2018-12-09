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

class AppDescPagerFirst : Fragment() {

    private var appFirstDescView: View? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        appFirstDescView = inflater!!.inflate(R.layout.fragment_appstart_desc_1, container, false)

        return appFirstDescView
    }

}
