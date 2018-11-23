package com.studio572.searchlistview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.parkseunghyun.achievementofall.ContentsHomeActivity
import com.example.parkseunghyun.achievementofall.ExoplayerActivity
import com.example.parkseunghyun.achievementofall.OtherUserHomeActivity
import com.example.parkseunghyun.achievementofall.R
import org.jetbrains.anko.startActivity
import org.w3c.dom.Text


class SearchAdapter(private val list: List<String>, private val context: Context, private  val cu: String) : BaseAdapter() {
    private val inflate: LayoutInflater
    private var viewHolder: ViewHolder? = null
    val REQUEST_FROM_SEARCH = 1010

    init {
        this.inflate = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(i: Int): Any? {
        return null
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = inflate.inflate(R.layout.row_listview, null)

            viewHolder = ViewHolder()
            viewHolder!!.label = convertView!!.findViewById(R.id.label) as TextView

            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }



        // 리스트에 있는 데이터를 리스트뷰 셀에 뿌린다.
        viewHolder!!.label!!.text = list[position]


        viewHolder!!.label!!.setOnClickListener {

            if(cu == "content"){
                // 컨텐츠 홈으로 이동
                Toast.makeText(context, " 서치 어댑터"+ list[position], Toast.LENGTH_LONG).show()

                val goToContents = Intent(context, ContentsHomeActivity::class.java)
                goToContents.putExtra("contentName", list[position])
                val contextToActivity = context as Activity
                contextToActivity.startActivityForResult(goToContents, REQUEST_FROM_SEARCH)

//                context.startActivity<ContentsHomeActivity>(
//                        "contentName" to  list[position]
//                )

            }else if(cu == "user"){
                // 사용자 홈으로 이동
                Toast.makeText(context, " 서치 어댑터"+ list[position], Toast.LENGTH_LONG).show()
                context.startActivity<OtherUserHomeActivity>(
                        "email" to  list[position]
                )
            }

        }

        return convertView
    }


    internal inner class ViewHolder {
        var label: TextView? = null

    }

}