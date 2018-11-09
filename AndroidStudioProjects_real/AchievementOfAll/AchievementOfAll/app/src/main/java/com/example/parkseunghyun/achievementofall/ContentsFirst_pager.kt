package com.example.parkseunghyun.achievementofall

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class ContentsFirst_pager : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view =  inflater!!.inflate(R.layout.contents_fragment_1, container, false)

        val bt = view.findViewById(R.id.button3) as Button

        bt.setOnClickListener {
//            println("conFirst")
//            (activity as HomeActivity).destroyAllFragment()
//            println("conFirst1")
//            (activity as HomeActivity).createHomePager()
//            println("conFirst2")
        }
        return view

    }

}