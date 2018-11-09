package adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.Log
import android.widget.Toast

import com.example.parkseunghyun.achievementofall.HomeAccountPager
import com.example.parkseunghyun.achievementofall.HomeInfoPager
import com.example.parkseunghyun.achievementofall.HomeSearchPager

class HomePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    internal var mNumOfTabs: Int = 0

    init {
        this.mNumOfTabs = 3
    }

    override fun getItem(position: Int): Fragment? {
        Log.d(this.javaClass.name, "POSITION LOGGING   $position")

        when (position) {
            0 -> {
                val homeAccountPager = HomeAccountPager()
                Log.d(this.javaClass.name, "POSITION 0")
                return homeAccountPager
            }
            1 -> {
                val homeInfoPager = HomeInfoPager()
                Log.d(this.javaClass.name, "POSITION 1")
                return homeInfoPager
            }
            2 -> {
                val homeSearchPager = HomeSearchPager()
                Log.d(this.javaClass.name, "POSITION 2")
                return homeSearchPager
            }
            else -> return null
        }
    }

    override fun getCount(): Int {
        return mNumOfTabs
    }
}