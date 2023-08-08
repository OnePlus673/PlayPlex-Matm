package com.playplexmatm.aeps.activities_aeps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.playplexmatm.R
import kotlinx.android.synthetic.main.activity_balance_check_response.*
import kotlinx.android.synthetic.main.activity_balance_check_response.view.*

class BalanceCheckResponseActivity : AppCompatActivity() {

    var aepsmessage: String = ""
    var transactionRefNo: String = ""
    var requestTransactionTime: String = ""
    var transactionAmount: String = ""
    var transactionStatus: String = ""
    var balanceAmount: String = ""
    var bankRRN: String = ""
    var transactionType: String = ""
    var fpTransactionId: String = ""
    var merchantTransactionId: String = ""
    var outletname: String = ""
    var outletmobile: String = ""
    var url: String = ""
    var bankName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_balance_check_response)

        custToolbar.ivBackBtn.setOnClickListener {
            onBackPressed()
        }

        val bundle = intent.extras

        if (bundle != null) {
            aepsmessage = bundle.getString("aepsmessage").toString()
            transactionRefNo = bundle.getString("transactionRefNo").toString()
            requestTransactionTime = bundle.getString("requestTransactionTime").toString()
            transactionAmount = bundle.getString("transactionAmount").toString()
            transactionStatus = bundle.getString("transactionStatus").toString()
            balanceAmount = bundle.getString("balanceAmount").toString()
            bankRRN = bundle.getString("bankRRN").toString()
            bankName = bundle.getString("bankName").toString()
            transactionType = bundle.getString("transactionType").toString()
            merchantTransactionId = bundle.getString("merchantTransactionId").toString()
            outletname = bundle.getString("outletname").toString()
            outletmobile = bundle.getString("outletmobile").toString()
            url = bundle.getString("url").toString()
        }

        tvSuccessMessage.text = aepsmessage
        tvTerminalID.text = transactionRefNo
        tvBankRRNNo.text = bankRRN
        tvDate.text = requestTransactionTime
        tvOutletName.text = transactionRefNo
        tvMobileNumber.text = transactionRefNo
        tvBalanceAmount.text = resources.getString(R.string.Rupee) + "" + balanceAmount
        tvOutletName.text = outletname
        tvMobileNumber.text = outletmobile
        tvBankName.setText(bankName)

        tvPrintReceipt.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("url", url)
            val intent = Intent(this, InvoiceViewActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
            finish()
        }

    }
}