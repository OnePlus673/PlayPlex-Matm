package com.playplexmatm.adapter.possale

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.playplexmatm.activity.partylist.SupplierAddressFragment
import com.playplexmatm.activity.partylist.SupplierGstFragment

class SupplierAdressAdapter (var context: Context,
                             fm: FragmentManager,
                             var totalTabs: Int,
                             var adress : String,
                             var billing_address : String,
                             var tax : String,
                             val status: String,
                             val openingBalance: String,
                             val balanceType: String,
                             val creditPeriod: String,
                             val creditLimit: String
) :
    FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                SupplierAddressFragment(adress,billing_address)
            }
            1 -> {
                SupplierGstFragment(tax, status, openingBalance, balanceType, creditPeriod,
                creditLimit)
            }

            else -> getItem(position)
        }
    }
    override fun getCount(): Int {
        return totalTabs
    }
}