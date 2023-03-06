package com.playplelx.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.playplelx.R
import com.playplelx.util.Constants
import com.playplelx.util.PrefManager

class SplashScreenActivity : AppCompatActivity() {

    lateinit var mContext: SplashScreenActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        mContext = this
        initUI()
    }

    private fun initUI() {
        supportActionBar!!.hide()
        Handler().postDelayed(
            { mRedirectScreen() },
            Constants.SPLASH_SCREEN
        )
    }

    private fun mRedirectScreen() {
        if (PrefManager(mContext).getvalue(Constants.Is_Login, false)) {
            startActivity(Intent(mContext, MainActivity::class.java))
            finish()
        } else {
            startActivity(Intent(mContext, LoginActivity::class.java))
            finish()
        }
    }
}