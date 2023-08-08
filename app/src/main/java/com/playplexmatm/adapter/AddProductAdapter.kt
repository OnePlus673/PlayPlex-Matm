package com.playplexmatm.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.playplexmatm.activity.itemlist.PricingFragment
import com.playplexmatm.activity.itemlist.StockFragment

class AddProductAdapter (var context: Context,
                         fm: FragmentManager,
                         var totalTabs: Int
) :
    FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                PricingFragment()
            }
            1 -> {
                StockFragment()
            }

            else -> getItem(position)
        }
    }
    override fun getCount(): Int {
        return totalTabs
    }
}