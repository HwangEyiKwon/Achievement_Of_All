package com.example.parkseunghyun.achievementofall

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class Info_pager : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view =  inflater!!.inflate(R.layout.tab_fragment_3, container, false)

        val bt_video = view.findViewById(R.id.button_video) as Button

        bt_video.setOnClickListener {
            (activity as HomeActivity).initializePlayer()
        }





        return view

    }
}