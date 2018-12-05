package adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import com.example.parkseunghyun.achievementofall.Fragments.*

class AppStartPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private var mNumOfTabs: Int = 0

    init {
        this.mNumOfTabs = 3
    }

    override fun getItem(position: Int): Fragment? {

        when (position) {
            0 -> {
                return AppDescPagerFirst()
            }
            1 -> {
                return AppDescPagerSecond()
            }
            2 -> {
                return AppDescPagerThird()
            }

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