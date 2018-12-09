package adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.example.parkseunghyun.achievementofall.Fragments.HomeAccountPager
import com.example.parkseunghyun.achievementofall.Fragments.HomeAppInfoPager
import com.example.parkseunghyun.achievementofall.Fragments.HomeSearchPager

/**
    REFACTORED
 */

class HomePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private var mNumOfTabs: Int = 0

    init {

        this.mNumOfTabs = 3

    }

    override fun getItem(position: Int): Fragment? {

        when (position) {

            0 -> { return HomeAccountPager() }

            1 -> { return HomeSearchPager() }

            2 -> { return HomeAppInfoPager() }

            else -> return null

        }
    }

    override fun getCount(): Int {

        return mNumOfTabs

    }

    override fun getItemPosition(`object`: Any?): Int {

        return POSITION_NONE

    }
}