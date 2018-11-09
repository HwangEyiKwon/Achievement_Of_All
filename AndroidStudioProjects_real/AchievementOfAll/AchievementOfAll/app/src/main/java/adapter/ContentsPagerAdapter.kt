package adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import com.example.parkseunghyun.achievementofall.ContentsFirst_pager
import com.example.parkseunghyun.achievementofall.ContentsSecond_pager
import com.example.parkseunghyun.achievementofall.ContentsThird_pager




class ContentsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    internal var mNumOfTabs: Int = 0

    init {
        this.mNumOfTabs = 3
    }

    override fun getItem(position: Int): Fragment? {

        when (position) {
            0 -> {
                println("adapter1")
                return ContentsFirst_pager()
            }
            1 -> {
                println("adapter2")
                return ContentsSecond_pager()
            }
            2 -> {
                println("adapter3")
                return ContentsThird_pager()
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