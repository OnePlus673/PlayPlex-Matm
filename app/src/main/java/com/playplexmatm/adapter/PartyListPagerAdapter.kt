package com.playplexmatm.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.playplexmatm.fragment.partylist.CustomerFragment
import com.playplexmatm.fragment.partylist.SupplierFragment


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