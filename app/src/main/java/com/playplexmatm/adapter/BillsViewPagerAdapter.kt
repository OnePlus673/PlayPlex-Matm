package com.playplexmatm.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.playplexmatm.activity.fragments.billsfragments.LedgerFragment
import com.playplexmatm.activity.fragments.billsfragments.PaymentFragment
import com.playplexmatm.activity.fragments.billsfragments.SaleFragment

internal class BillsViewPagerAdapter (
    var context: Context,
    fm: FragmentManager,
    var totalTabs: Int
) :
    FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                SaleFragment()
            }

            1 -> {
                PaymentFragment()
            }

            2 -> {
                LedgerFragment()
            }

            else -> getItem(position)
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }
}
