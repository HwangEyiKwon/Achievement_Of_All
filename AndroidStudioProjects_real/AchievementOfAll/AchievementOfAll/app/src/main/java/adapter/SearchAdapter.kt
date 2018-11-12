package com.studio572.searchlistview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.parkseunghyun.achievementofall.ContentsHomeActivity
import com.example.parkseunghyun.achievementofall.R
import org.jetbrains.anko.startActivity

class SearchAdapter(private val list: List<String>, private val context: Context) : BaseAdapter() {
    private val inflate: LayoutInflater
    private var viewHolder: ViewHolder? = null

    init {
        this.inflate = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(i: Int): Any? {

        println(list[i])
        println("왜 두번 호출됨?????????????")
        return null
    }

    override fun getItemId(i: Int): Long {
        // 컨텐츠 홈으로 이동
        Toast.makeText(context, " 서치 어댑터"+ list[i], Toast.LENGTH_LONG).show()

        context.startActivity<ContentsHomeActivity>(
                "contentName" to  list[i]
        )
        println("왜 두번 호출됨")
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

        return convertView
    }

    internal inner class ViewHolder {
        var label: TextView? = null
    }

}