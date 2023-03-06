package com.playplelx.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.playplelx.fragment.dashboard.PurchaseFragment
import com.playplelx.fragment.dashboard.PurchaseReturnFragment
import com.playplelx.fragment.dashboard.SaleFragment
import com.playplelx.fragment.dashboard.SaleReturnFragment

class DashBoardAdapter(fm: FragmentManager, var tabCount: Int) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> SaleFragment()
            1 -> PurchaseFragment()
            2 -> SaleReturnFragment()
            3 -> PurchaseReturnFragment()
            else -> SaleFragment()
        }
    }

    override fun getCount(): Int {
        return tabCount
    }

    override fun getPageTitle(position: Int): CharSequence {
        return "Tab " + (position)
    }
}