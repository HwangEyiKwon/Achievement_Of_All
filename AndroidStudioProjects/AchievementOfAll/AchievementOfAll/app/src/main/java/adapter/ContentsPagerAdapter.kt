package adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import com.example.parkseunghyun.achievementofall.Fragments.ContentsMyInfoPager
import com.example.parkseunghyun.achievementofall.Fragments.ContentsProgressPager
import com.example.parkseunghyun.achievementofall.Fragments.ContentsNoticePager




class ContentsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    internal var mNumOfTabs: Int = 0

    init {
        this.mNumOfTabs = 3
    }

    override fun getItem(position: Int): Fragment? {

        when (position) {
            0 -> {
                println("adapter1")
                return ContentsMyInfoPager()
            }
            1 -> {
                println("adapter2")
                return ContentsProgressPager()
            }
            2 -> {
                println("adapter3")
                return ContentsNoticePager()
            }

            else -> return null
        }
    }

    override fun getCount(): Int {
        println("getcount"+mNumOfTabs)
        return mNumOfTabs
    }

    override fun getItemPosition(`object`: Any?): Int {
        // refresh all fragments when data set changed
        return PagerAdapter.POSITION_NONE
    }


//    override fun getItemPosition(`object`: Any?): Int {
//        return PagerAdapter.POSITION_NONE
//    }

}