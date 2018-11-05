package adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
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
                println("adapterh1")
                return Desc_pager()
            }
            1 -> {
                println("adapterh2")
                return Notice_pager()
            }
            2 -> {
                println("adapterh3")
                return Info_pager()
            }
            else -> return null
        }
    }

    override fun getCount(): Int {
        println("getcounth"+mNumOfTabs)
        return mNumOfTabs
    }
    override fun getItemPosition(`object`: Any?): Int {
        // refresh all fragments when data set changed
        return PagerAdapter.POSITION_NONE
    }


//    fun clear() {
//        val transaction = fm
//        for (fragment in mFragmentList) {
//            transaction.remove(fragment)
//        }
//        mFragmentList.clear()
//        transaction.commitAllowingStateLoss()
//    }


//    override fun getItemPosition(`object`: Any?): Int {
//        return PagerAdapter.POSITION_NONE
//    }
//    override fun destroyItem(collection: View, position: Int, view: Any) {
//        (collection as ViewPager).removeView(view as View)
//    }



//    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any) {
//        super.destroyItem(container, position, `object`)
//
//        if (position <= count) {
//            val manager = (`object` as Fragment).fragmentManager
//            val trans = manager.beginTransaction()
//            trans.remove(`object`)
//            trans.commit()
//        }
//    }
}