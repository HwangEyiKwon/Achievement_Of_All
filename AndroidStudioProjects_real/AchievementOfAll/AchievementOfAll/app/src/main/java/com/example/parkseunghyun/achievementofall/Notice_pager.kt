package com.example.parkseunghyun.achievementofall

/**
 * Created by user on 2016-12-26.
 */

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class Notice_pager : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view =  inflater!!.inflate(R.layout.tab_fragment_1, container, false)

        val bt = view.findViewById(R.id.button2) as Button

        bt.setOnClickListener {
            println("123123123123123123123123")
            (activity as HomeActivity).destroyHomePager()
            (activity as HomeActivity).createContentsPager()
        }
        return view
    }

}
