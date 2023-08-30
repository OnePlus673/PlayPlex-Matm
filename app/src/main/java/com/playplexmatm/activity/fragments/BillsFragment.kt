package com.playplexmatm.activity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.playplexmatm.R
import com.playplexmatm.adapter.BillsViewPagerAdapter
import com.playplexmatm.microatm.MATMTestActivity
//import kotlinx.android.synthetic.main.activity_matmtest.custToolbar
//import kotlinx.android.synthetic.main.activity_matmtest.view.tvTitle

class BillsFragment : Fragment() {

    lateinit var tabs: TabLayout
    lateinit var viewPager: ViewPager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bills, container, false)
//        (activity as MATMTestActivity).custToolbar.tvTitle.text = "Bills"
        tabs = view.findViewById(R.id.tabs)
        viewPager = view.findViewById(R.id.view_pager)
        setUpViewPager()
        return view
    }

    private fun setUpViewPager() {
        tabs.addTab(tabs.newTab().setText(getString(R.string.sale)))
        tabs.addTab(tabs.newTab().setText(getString(R.string.paymeny)))
        tabs.addTab(tabs.newTab().setText(getString(R.string.ledger)))
        tabs.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = BillsViewPagerAdapter(
            requireContext(), childFragmentManager,
            tabs.tabCount
        )
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 3
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
                when (tab.position) {
                    0 -> {
                        tabs.getTabAt(tab.position)?.select()
                    }

                    1 -> {
                        tabs.getTabAt(tab.position)?.select()
                    }

                    2 -> {
                        tabs.getTabAt(tab.position)?.select()
                    }

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }
}