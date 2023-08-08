package com.playplexmatm.activity.reports

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.playplexmatm.R
import kotlinx.android.synthetic.main.activity_select_report.*

class SelectReportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_report)
        //supportActionBar!!.hide()

        cvPayments.setOnClickListener {
            val intent = Intent(this,PaymentReportsActivity::class.java)
            startActivity(intent)
        }

        cvStockAlert.setOnClickListener {
            val intent = Intent(this,ProductReportActivity::class.java)
            startActivity(intent)
        }

        cvSalesSummary.setOnClickListener {
            val intent = Intent(this,SalesSummaryActivity::class.java)
            startActivity(intent)
        }

        cvStockSummary.setOnClickListener {
            val intent = Intent(this,StockSummaryActivity::class.java)
            startActivity(intent)
        }

        cvRateList.setOnClickListener {
            val intent = Intent(this,RateListActivity::class.java)
            startActivity(intent)
        }

        cvProfitLoss.setOnClickListener {
            val intent = Intent(this,ProfitandLossActivity::class.java)
            startActivity(intent)
        }

        cvPurchaseRegister.setOnClickListener {
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
        }

        cvSalesRegister.setOnClickListener {
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
        }

        cvProductSalesSummary.setOnClickListener {
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
        }

        cvUserReports.setOnClickListener {
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
        }

    }
}