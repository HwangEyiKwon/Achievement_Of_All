package adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

import fragments.DummyFragment

class CircleIndicatorAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> {
                return DummyFragment()
            }
            1 -> {
                return DummyFragment()
            }
            2 -> {
                return DummyFragment()
            }

            else -> return null
        }
    }

    override fun getCount(): Int {
        return 3
    }
}
