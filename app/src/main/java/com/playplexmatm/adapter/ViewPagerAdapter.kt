package com.playplexmatm.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.playplexmatm.fragment.itemlist.BrandFragment
import com.playplexmatm.fragment.itemlist.CategoryFragment
import com.playplexmatm.fragment.itemlist.ProductFragment

class ViewPagerAdapter(fm: FragmentManager, var tabCount: Int) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> CategoryFragment()
            1 -> BrandFragment()
            2 -> ProductFragment()
            else -> CategoryFragment()
        }
    }

    override fun getCount(): Int {
        return tabCount
    }

    override fun getPageTitle(position: Int): CharSequence {
        return "Tab " + (position + 1)
    }
}
