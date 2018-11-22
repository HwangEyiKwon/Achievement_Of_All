package com.example.parkseunghyun.achievementofall.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.parkseunghyun.achievementofall.ContentsHomeActivity
import com.example.parkseunghyun.achievementofall.R

class ContentsThird_pager : Fragment() {


    private var view_: View? = null
    private var contentsName: TextView? = null
    private var contentName: String? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        view_ =  inflater!!.inflate(R.layout.contents_fragment_3, container, false)
        val activity = activity as ContentsHomeActivity
        contentName = activity.content.toString()
        println("TEST3 " + contentName)

        contentsName = view_?.findViewById(R.id.id_contents_name_3)
        contentsName?.setText(contentName)


        return view_
    }
}
