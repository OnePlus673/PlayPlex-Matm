package com.playplexmatm.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.playplexmatm.activity.partylist.CustomerAdresssFragment
import com.playplexmatm.activity.partylist.CustomerGstFragment

class CustomerAdressAdpater (var context: Context,
                             fm: FragmentManager,
                             var totalTabs: Int,
                             val address: String,
                             val shipping_address: String,
                             val openingBalance: String,
                             val balanceType: String,
                             val creditPeriod: String,
                             val creditLimit: String
) :
    FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                CustomerAdresssFragment(address, shipping_address)
            }
            1 -> {
                CustomerGstFragment(openingBalance, balanceType, creditPeriod, creditLimit)
            }

            else -> getItem(position)
        }
    }
    override fun getCount(): Int {
        return totalTabs
    }
}