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

/**
    REFARCTORED
 */

// HomeSearchPager
// 홈 화면의 두번째 페이지
// 찾기 페이지
class HomeSearchPager : Fragment() {

    private var homeSearchView: View? = null
    private var editSearch: EditText? = null
    private var searchedList: MutableList<String>? = null
    private var searchedEmailList: MutableList<String>? = null
    private var searchedArraylist: ArrayList<String>? = null
    private var searchedListView: ListView? = null
    private var searchAdapter: SearchAdapter? = null
    private var selectSearchingTab: TabLayout? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        homeSearchView = inflater!!.inflate(R.layout.fragment_home_search, container, false)

        // 초기화
        initViewComponents()

        generateTabLayout()

        settingContentList() /** 검색에 사용할 데이터을 미리 저장한다. */

        return homeSearchView
    }

    // initViewComponent
    // 메인 페이지의 view에 있는 각 요소들을 초기화합니다.
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

    // search
    // 찾기 기능
    fun search(charText: String) {

        searchedList!!.clear()
        searchedEmailList!!.clear()

        if (charText.replace(" ","").equals("")) {

            searchedList!!.addAll(searchedArraylist!!)

        } else {

            for (indexOfSearchedList in 0 until searchedArraylist!!.size) {

                if (searchedArraylist!![indexOfSearchedList].contains(charText, ignoreCase = true)) {

                    searchedList!!.add(searchedArraylist!![indexOfSearchedList])

                }

            }
        }

        searchAdapter!!.notifyDataSetChanged()

    }

    // settingContentList
    // 컨텐츠 리스트를 설정합니다.
    private fun settingContentList() {

        searchedList!!.clear()
        searchedEmailList!!.clear()

        VolleyHttpService.getSearchContentData(activity){ success ->

            val contentsData = success.get("contents") as JSONArray

            for (indexOfSearchedContentList in 0..(contentsData.length() - 1)) {

                val item = contentsData[indexOfSearchedContentList]
                searchedList?.add(item.toString())

            }

            searchedArraylist = ArrayList()
            searchedArraylist!!.addAll(searchedList!!)
            searchAdapter = SearchAdapter(searchedList!!, null, this.context, "content")
            searchedListView?.adapter = searchAdapter

        }

    }

    // settingUserList
    // 사용자 리스트를 설정합니다.
    private fun settingUserList() {

        searchedList!!.clear()
        searchedEmailList!!.clear()

        VolleyHttpService.getSearchUserData(activity){ success ->

            val usersData = success.get("users") as JSONArray

            for (indexOfUserDate in 0..(usersData.length() - 1)) {

                searchedList?.add((usersData[indexOfUserDate] as JSONObject).getString("name").replace("+", " "))
                searchedEmailList?.add((usersData[indexOfUserDate] as JSONObject).getString("email"))

            }

            searchedArraylist = ArrayList()
            searchedArraylist!!.addAll(searchedList!!)
            searchAdapter = SearchAdapter(searchedList!!, searchedEmailList!!, this.context, "user")
            searchedListView?.adapter = searchAdapter

        }

    }

    // generateTabLayout
    // TabLayout을 생성합니다.
    private fun generateTabLayout() {

        selectSearchingTab = homeSearchView!!.findViewById(R.id.id_search_tab)
        selectSearchingTab!!.setTabTextColors(Color.parseColor("#000000"), Color.parseColor("#000000"))
        selectSearchingTab!!.addTab(selectSearchingTab!!.newTab().setText("Content").setTag("content"))
        selectSearchingTab!!.addTab(selectSearchingTab!!.newTab().setText("User").setTag("user"))
        selectSearchingTab!!.tabGravity = TabLayout.GRAVITY_FILL

        searchedList = ArrayList()
        searchedEmailList = ArrayList()

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