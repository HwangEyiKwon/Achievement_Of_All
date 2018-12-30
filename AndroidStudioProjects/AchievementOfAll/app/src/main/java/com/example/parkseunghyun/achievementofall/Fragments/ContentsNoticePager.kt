package com.example.parkseunghyun.achievementofall.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.parkseunghyun.achievementofall.ContentsHomeActivity
import com.example.parkseunghyun.achievementofall.R

// TODO 여기를 공지가 아니라 규칙으로 걍 바꿔야될듯.

// ContentsNoticePager
// 컨텐츠 페이지 중 세번째 Fragment
// 공지사항 화면
class ContentsNoticePager : Fragment() {


    private var view_: View? = null
    private var contentsName: TextView? = null
    private var contentName: String? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        view_ =  inflater!!.inflate(R.layout.fragment_contents_noti, container, false)
        val activity = activity as ContentsHomeActivity
        contentName = activity.content.toString()

        contentsName = view_?.findViewById(R.id.id_contents_name_3)
        contentsName?.setText(contentName)

        return view_

    }
}
