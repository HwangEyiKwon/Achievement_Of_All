package com.example.parkseunghyun.achievementofall

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class HomeSearchPager : Fragment() {
    private var view_: View? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view_ = inflater!!.inflate(R.layout.fragment_home_search, container, false)
        return view_
    }
}