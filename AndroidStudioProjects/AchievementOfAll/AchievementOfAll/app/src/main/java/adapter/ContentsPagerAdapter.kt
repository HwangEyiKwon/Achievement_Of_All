package adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import com.example.parkseunghyun.achievementofall.Fragments.ContentsMyInfoPager
import com.example.parkseunghyun.achievementofall.Fragments.ContentsProgressPager
import com.example.parkseunghyun.achievementofall.Fragments.ContentsNoticePager

/**
    REFACTORED
 */


class ContentsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    internal var mNumOfTabs: Int = 0

    init {

        this.mNumOfTabs = 3

    }

    override fun getItem(position: Int): Fragment? {

        when (position) {

            0 -> { return ContentsMyInfoPager() }

            1 -> { return ContentsProgressPager() }

            2 -> { return ContentsNoticePager() }

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