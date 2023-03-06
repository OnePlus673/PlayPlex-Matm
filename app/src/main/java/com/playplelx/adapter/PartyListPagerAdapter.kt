package com.playplelx.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.playplelx.fragment.itemlist.BrandFragment
import com.playplelx.fragment.itemlist.CategoryFragment
import com.playplelx.fragment.itemlist.ProductFragment
import com.playplelx.fragment.partylist.CustomerFragment
import com.playplelx.fragment.partylist.SupplierFragment


class PartyListPagerAdapter(fm: FragmentManager, var tabCount: Int) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> CustomerFragment()
            1 -> SupplierFragment()
            else -> CustomerFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return "Tab " + (position)
    }
}