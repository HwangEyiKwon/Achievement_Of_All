package com.studio572.searchlistview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.parkseunghyun.achievementofall.Configurations.RequestCodeCollection
import com.example.parkseunghyun.achievementofall.ContentsHomeActivity
import com.example.parkseunghyun.achievementofall.OtherUserHomeActivity
import com.example.parkseunghyun.achievementofall.R
import org.jetbrains.anko.startActivity
import org.json.JSONObject

/*
    REFARCTORED
 */

class SearchAdapter(private val list: List<String>, private val context: Context, private  val categoryToSearch: String) : BaseAdapter() {

    private var inflate: LayoutInflater? = null
    private var viewHolder: ViewHolder? = null
    private var userName: String? = null

    init {

        this.inflate = LayoutInflater.from(context)

    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup): View {

        var mConvertView = convertView

        if (mConvertView == null) {

            mConvertView = inflate?.inflate(R.layout.view_ractangle_for_listview, null)

            viewHolder = ViewHolder()
            viewHolder!!.label = mConvertView!!.findViewById(R.id.label) as TextView
            mConvertView.tag = viewHolder

        } else {

            viewHolder = mConvertView.tag as ViewHolder

        }

        if(categoryToSearch == "content"){

            viewHolder!!.label!!.text = list[position]

        }
        else if (categoryToSearch == "user"){

            viewHolder!!.label!!.text = list[position]

        }

        viewHolder!!.label!!.setOnClickListener {

            if(categoryToSearch == "content"){

                val intentForContentsHome = Intent(context, ContentsHomeActivity::class.java)
                intentForContentsHome.putExtra("contentName", list[position])
                val contextToActivity = context as Activity
                contextToActivity.startActivityForResult(intentForContentsHome, RequestCodeCollection.REQUEST_RETURN_FROM_SEARCH)

            }else if(categoryToSearch == "user"){

                val string= list[position]
                val userObjects = JSONObject(string)

                context.startActivity<OtherUserHomeActivity>(

                        "email" to  userObjects.getString("email"),
                        "userName" to userObjects.getString("name")

                )

            }
        }

        return mConvertView
    }


    internal inner class ViewHolder {

        var label: TextView? = null

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

}