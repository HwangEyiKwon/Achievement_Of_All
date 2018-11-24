package adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.Log
import com.example.parkseunghyun.achievementofall.Fragments.HomeAccountPager
import com.example.parkseunghyun.achievementofall.Fragments.HomeInfoPager
import com.example.parkseunghyun.achievementofall.Fragments.HomeSearchPager



class HomePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    internal var mNumOfTabs: Int = 0

    var homeAccountPager: HomeAccountPager? = null
    var homeSearchPager: HomeSearchPager? = null
    var homeInfoPager: HomeInfoPager? = null

    init {
        this.mNumOfTabs = 3
    }




    override fun getItem(position: Int): Fragment? {

        when (position) {
            0 -> {
                homeAccountPager = HomeAccountPager()
                homeAccountPager!!.onResume()
                Log.d(this.javaClass.name, "POSITION ACCOUNT")
                return homeAccountPager
            }
            1 -> {
                homeSearchPager = HomeSearchPager()
                Log.d(this.javaClass.name, "POSITION SEARCH")
                return homeSearchPager
            }
            2 -> {
                homeInfoPager = HomeInfoPager()
                Log.d(this.javaClass.name, "POSITION INFO")
                return homeInfoPager
            }
            else -> return null
        }
    }

    override fun getCount(): Int {
        return mNumOfTabs
    }
}