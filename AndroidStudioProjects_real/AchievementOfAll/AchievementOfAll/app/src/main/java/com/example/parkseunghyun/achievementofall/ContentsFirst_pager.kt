package com.example.parkseunghyun.achievementofall

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.contents_fragment_1.*

class ContentsFirst_pager : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.contents_fragment_1, container, false)

        button3.setOnClickListener {
            println("dafdsfasdfasdfasdfasdfasdfasd")
            (activity as HomeActivity).destroyHomePager()
            (activity as HomeActivity).createContentsPager()
        }
    }

}