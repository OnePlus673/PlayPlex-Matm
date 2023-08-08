package com.playplexmatm.introfragment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.playplexmatm.R
import com.playplexmatm.activity.LoginActivity
import kotlinx.android.synthetic.main.activity_intro_slider.*

class IntroSliderActivity : AppCompatActivity() {
    private val fragmentList = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_slider)

        val adapter = IntroSliderAdapter(this)
        vpIntroSlider.adapter = adapter
        fragmentList.addAll(listOf(Intro1Fragment(), Intro2Fragment(), Intro3Fragment()))
        adapter.setFragmentList(fragmentList)

        registerListeners()
    }

    private fun registerListeners() {
        vpIntroSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position < fragmentList.lastIndex) {
                    tvSkip.visibility = View.VISIBLE
                    tvNext.text = "Next"
                } else {
                    tvSkip.visibility = View.GONE
                    tvNext.text = "Get Started"
                }
            }
        })

        tvSkip.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        tvNext.setOnClickListener {
            val position = vpIntroSlider.currentItem
            if (position < fragmentList.lastIndex) {
                vpIntroSlider.currentItem = position + 1
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                finish()
            }
        }
    }

}