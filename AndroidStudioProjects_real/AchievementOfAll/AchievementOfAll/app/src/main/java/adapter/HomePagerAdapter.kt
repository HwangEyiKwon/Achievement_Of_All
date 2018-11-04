package adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import com.example.parkseunghyun.achievementofall.Desc_pager
import com.example.parkseunghyun.achievementofall.Info_pager
import com.example.parkseunghyun.achievementofall.Notice_pager




class HomePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    internal var mNumOfTabs: Int = 0

    init {
        this.mNumOfTabs = 3
    }

    override fun getItem(position: Int): Fragment? {

        when (position) {
            0 -> {
                return Desc_pager()
            }
            1 -> {
                return Notice_pager()
            }
            2 -> {
                return Info_pager()
            }
            else -> return null
        }
    }

    override fun getCount(): Int {
        return mNumOfTabs
    }

    override fun destroyItem(collection: View, position: Int, view: Any) {
        (collection as ViewPager).removeView(view as View)
    }
}