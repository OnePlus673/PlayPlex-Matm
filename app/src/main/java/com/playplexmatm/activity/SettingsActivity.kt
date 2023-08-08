package com.playplexmatm.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.playplexmatm.R
import com.playplexmatm.activity.settings.PaymentModeSettingsActivity
import com.playplexmatm.activity.settings.TaxesSettingsActivity
import com.playplexmatm.activity.settings.UnitSettingActivity
import com.playplexmatm.activity.settings.WareHouseSettingsActivity

class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var mContext: SettingsActivity
    lateinit var ivBack: ImageView
    lateinit var tvTaxesSettings: TextView
    lateinit var tvPaymentModeSettings: TextView
    lateinit var tvUnitSettings: TextView
    lateinit var tvWareHouseSettings: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        ivBack = findViewById(R.id.ivBack)
        tvTaxesSettings = findViewById(R.id.tvTaxesSettings)
        tvPaymentModeSettings = findViewById(R.id.tvPaymentModeSettings)
        tvUnitSettings = findViewById(R.id.tvUnitSettings)
        tvUnitSettings = findViewById(R.id.tvUnitSettings)
        tvWareHouseSettings = findViewById(R.id.tvWareHouseSettings)
    }

    private fun addListner() {
        ivBack.setOnClickListener(this)
        tvTaxesSettings.setOnClickListener(this)
        tvPaymentModeSettings.setOnClickListener(this)
        tvUnitSettings.setOnClickListener(this)
        tvWareHouseSettings.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.tvTaxesSettings -> {
                startActivity(Intent(mContext, TaxesSettingsActivity::class.java))
            }
            R.id.tvPaymentModeSettings -> {
                startActivity(Intent(mContext, PaymentModeSettingsActivity::class.java))
            }
            R.id.tvUnitSettings -> {
                startActivity(Intent(mContext, UnitSettingActivity::class.java))
            }
            R.id.tvWareHouseSettings -> {
                startActivity(Intent(mContext, WareHouseSettingsActivity::class.java))
            }
        }
    }
}