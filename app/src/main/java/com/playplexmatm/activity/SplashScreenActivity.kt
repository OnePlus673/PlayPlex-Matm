package com.playplexmatm.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.playplexmatm.R
import com.playplexmatm.microatm.MATMTestActivity
import com.playplexmatm.util.AppConstants
import com.playplexmatm.util.AppPrefs
import com.playplexmatm.util.Constants

class SplashScreenActivity : AppCompatActivity() {

    lateinit var mContext: SplashScreenActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        mContext = this
        initUI()
    }

    private fun initUI() {
        Handler().postDelayed(
            { mRedirectScreen() },
            Constants.SPLASH_SCREEN
        )
    }

    private fun mRedirectScreen() {
        val islogin: Boolean = AppPrefs.getBooleanPref(AppConstants.IS_LOGIN, mContext)
        if (islogin) {
            val intent = Intent(mContext, MATMTestActivity::class.java)
            startActivity(intent)
            finish()
        }
        else  {
            val i = Intent(mContext, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()

        }
    }
}