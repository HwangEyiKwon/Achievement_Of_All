package com.example.parkseunghyun.achievementofall.Fragments

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import com.example.parkseunghyun.achievementofall.Configurations.VolleyHttpService
import com.example.parkseunghyun.achievementofall.R
import com.studio572.searchlistview.SearchAdapter
import org.json.JSONArray
import org.json.JSONObject

/*
    REFARCTORED
 */

class HomeSearchPager : Fragment() {

    private var homeSearchView: View? = null

    private var editSearch: EditText? = null                // 검색어를 입력할 Input 창
    private var searchedList: MutableList<String>? = null   // 데이터를 넣은 리스트변수
    private var searchedArraylist: ArrayList<String>? = null
    private var searchedListView: ListView? = null          // 검색을 보여줄 리스트변수

    private var searchAdapter: SearchAdapter? = null        // 리스트뷰에 연결할 아답터

    private var selectSearchingTab: TabLayout? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        homeSearchView = inflater!!.inflate(R.layout.fragment_home_search, container, false)

        initViewComponents()

        generateTabLayout()

        // 검색에 사용할 데이터을 미리 저장한다.
        settingContentList()

        return homeSearchView
    }

    private fun initViewComponents() {

        editSearch = homeSearchView!!.findViewById(R.id.edittext_search);
        searchedListView = homeSearchView!!.findViewById(R.id.searched_item_listview);
        searchedListView!!.isVerticalScrollBarEnabled = true

        editSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {

                val textToSearch = editSearch?.text.toString()
                search(textToSearch)

            }
        })

    }


    fun search(charText: String) {
        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        searchedList!!.clear()

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.replace(" ","").equals("")) {

            searchedList!!.addAll(searchedArraylist!!)

        } else {
            // 리스트의 모든 데이터를 검색한다.

            for (indexOfSearchedList in 0 until searchedArraylist!!.size) {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (searchedArraylist!![indexOfSearchedList].contains(charText, ignoreCase = true)) {

                    searchedList!!.add(searchedArraylist!![indexOfSearchedList])

                }

            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        searchAdapter!!.notifyDataSetChanged()

    }

    private fun settingContentList() {

        searchedList!!.clear()

        VolleyHttpService.getSearchContentData(activity){ success ->

            val contentsData = success.get("contents") as JSONArray

            for (indexOfSearchedContentList in 0..(contentsData.length() - 1)) {

                val item = contentsData[indexOfSearchedContentList]
                searchedList?.add(item.toString())

            }

            searchedArraylist = ArrayList()
            searchedArraylist!!.addAll(searchedList!!)
            searchAdapter = SearchAdapter(searchedList!!, this.context, "content")
            searchedListView?.adapter = searchAdapter

        }

    }

    private fun settingUserList() {

        searchedList!!.clear()

        VolleyHttpService.getSearchUserData(activity){ success ->

            val usersData = success.get("users") as JSONArray

            for (i in 0..(usersData.length() - 1)) {

                searchedList?.add((usersData[i] as JSONObject).getString("name"))

            }

            searchedArraylist = ArrayList()
            searchedArraylist!!.addAll(searchedList!!)
            searchAdapter = SearchAdapter(searchedList!!, this.context, "user")
            searchedListView?.adapter = searchAdapter

        }

    }

    private fun generateTabLayout() {

        selectSearchingTab = homeSearchView!!.findViewById(R.id.id_search_tab)
        selectSearchingTab!!.setTabTextColors(Color.parseColor("#000000"), Color.parseColor("#000000"))
        selectSearchingTab!!.addTab(selectSearchingTab!!.newTab().setText("Content").setTag("content"))
        selectSearchingTab!!.addTab(selectSearchingTab!!.newTab().setText("User").setTag("user"))
        selectSearchingTab!!.tabGravity = TabLayout.GRAVITY_FILL

        searchedList = ArrayList()

        selectSearchingTab!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {

                if(tab.tag == "content") {

                    settingContentList()

                }else if(tab.tag == "user"){

                    settingUserList()

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}

        })


    }
}