package com.playplelx.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.playplelx.fragment.dashboard.*

class DashBoardAdapter(fm: FragmentManager, var tabCount: Int) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> DashBoardProductFragment()
            1 -> SaleFragment()
            2 -> PurchaseFragment()
            else -> DashBoardProductFragment()
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        return "Tab " + (position)
    }
}