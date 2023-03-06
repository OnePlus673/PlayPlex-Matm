package com.playplelx.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.playplelx.R
import com.playplelx.adapter.ViewPagerAdapter
import com.playplelx.network.ApiInterface
import com.playplelx.network.Apiclient

class ItemListActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var mContext: ItemListActivity
    lateinit var ivBack: ImageView
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    lateinit var apiInterface: ApiInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        supportActionBar!!.hide()
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        ivBack = findViewById(R.id.ivBack)
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        setTabLayOut()
        setupViewPager()

    }

    private fun addListner() {
        ivBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }


    private fun setupViewPager() {
        viewPager.apply {
            adapter = ViewPagerAdapter(supportFragmentManager, tabLayout.tabCount)
            addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        }
    }


    private fun setTabLayOut() {
        tabLayout.apply {
            addTab(this.newTab().setText(mContext.resources.getString(R.string.str_category)))
            addTab(this.newTab().setText(mContext.resources.getString(R.string.str_brand)))
            addTab(this.newTab().setText(mContext.resources.getString(R.string.str_product)))

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.position?.let {
                        viewPager.currentItem = it
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })


        }
    }
}