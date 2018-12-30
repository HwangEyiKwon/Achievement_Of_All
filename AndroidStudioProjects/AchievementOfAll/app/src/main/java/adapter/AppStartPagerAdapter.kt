package adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import com.example.parkseunghyun.achievementofall.Fragments.AppDescPagerFirst
import com.example.parkseunghyun.achievementofall.Fragments.AppDescPagerSecond
import com.example.parkseunghyun.achievementofall.Fragments.AppDescPagerThird

/**
    REFARCTORED
 */

// AppStartPagerAdapter
// 앱 메인 화면에서 앱 설명란에 필요한 어댑터입니다.
class AppStartPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private var mNumOfTabs: Int = 0

    init {

        this.mNumOfTabs = 3

    }

    override fun getItem(position: Int): Fragment? {

        when (position) {

            0 -> { return AppDescPagerFirst() }

            1 -> { return AppDescPagerSecond() }

            2 -> { return AppDescPagerThird() }

            else -> return null
        }
    }

    override fun getCount(): Int {

        return mNumOfTabs

    }

    override fun getItemPosition(`object`: Any?): Int {

        return PagerAdapter.POSITION_NONE

    }

}