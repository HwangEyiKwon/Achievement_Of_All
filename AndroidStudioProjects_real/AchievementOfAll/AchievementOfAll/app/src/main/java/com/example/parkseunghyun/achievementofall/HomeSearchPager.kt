package com.example.parkseunghyun.achievementofall

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import com.studio572.searchlistview.SearchAdapter


class HomeSearchPager : Fragment() {
    private var view_: View? = null
    private var list: MutableList<String>? = null  // 데이터를 넣은 리스트변수
    private var listView: ListView? = null          // 검색을 보여줄 리스트변수
    private var editSearch: EditText? = null        // 검색어를 입력할 Input 창
    private var adapter: SearchAdapter? = null      // 리스트뷰에 연결할 아답터
    private var arraylist: ArrayList<String>? = null


    private var homeSearchPagerContext: Context? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeSearchPagerContext = activity
        view_ = inflater!!.inflate(R.layout.fragment_home_search, container, false)

        editSearch = view_!!.findViewById(R.id.editSearch);
        listView = view_!!.findViewById(R.id.listView);

        // 리스트를 생성한다.
        list = ArrayList()

        // 검색에 사용할 데이터을 미리 저장한다.
        settingList()

        // 리스트의 모든 데이터를 arraylist에 복사한다.// list 복사본을 만든다.
        arraylist = ArrayList()
        arraylist!!.addAll(list!!)

        // 리스트에 연동될 아답터를 생성한다.
        adapter = SearchAdapter(list!!, this.context)

        // 리스트뷰에 아답터를 연결한다.
        listView?.adapter = adapter

        // input창에 검색어를 입력시 "addTextChangedListener" 이벤트 리스너를 정의한다.
        editSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                val text = editSearch?.text.toString()
                search(text)
            }
        })

        return view_
    }

    fun search(charText: String) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        list!!.clear()

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length == 0) {
            list!!.addAll(arraylist!!)
        } else {
            // 리스트의 모든 데이터를 검색한다.
            for (i in 0 until arraylist!!.size) {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (arraylist!!.get(i).toLowerCase().contains(charText)) {
                    // 검색된 데이터를 리스트에 추가한다.
                    list!!.add(arraylist!!.get(i))
                }
            }
        }// 문자 입력을 할때..
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        adapter!!.notifyDataSetChanged()
    }

    private fun settingList() {
        list?.add("Smoking")
        list?.add("Diet")
        list?.add("study")
        list?.add("Work Out")
        list?.add("Travel")
        VolleyHttpService.getSearchData(homeSearchPagerContext!!){ success ->
            println("serachdata"+success)
        }

    }
}