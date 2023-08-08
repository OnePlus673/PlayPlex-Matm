package com.playplexmatm.aeps.activities_aeps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.playplexmatm.R
import kotlinx.android.synthetic.main.activity_cash_withdrawal_success.*

class CashWithdrawalSuccessActivity : AppCompatActivity() {

    var aepsmessage: String = ""
    var terminalId: String = ""
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
    var aadhar_no: String = ""
    var bankName: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cash_withdrawal_success)


        val bundle = intent.extras
        if (bundle != null) {
            aepsmessage = bundle.getString("aepsmessage").toString()
            requestTransactionTime = bundle.getString("requestTransactionTime").toString()
            transactionAmount = bundle.getString("transactionAmount").toString()
            transactionStatus = bundle.getString("transactionStatus").toString()
            balanceAmount = bundle.getString("balanceAmount").toString()
            bankRRN = bundle.getString("bankRRN").toString()
            transactionType = bundle.getString("transactionType").toString()
            outletname = bundle.getString("outletname").toString()
            outletmobile = bundle.getString("outletmobile").toString()
            url = bundle.getString("url").toString()
            aadhar_no = bundle.getString("aadhar_no").toString()
            bankName = bundle.getString("bankName").toString()
        }

        if (transactionType.equals("CW")) {
            tvTransactionType.setText("Cash Withdrawal")
        } else {
            tvTransactionType.setText("Aadhar Pay")
        }

        tvSuccessMessage.text = aepsmessage
        tvAadharNumber.text = aadhar_no
        tvBankName.text = bankName
        tvBankUtrNo.text = bankRRN
        tvDateCw.text = requestTransactionTime
        tvOutletName.text = outletname
        tvMobileNumber.text = outletmobile
        tvBalAmountCw.text = resources.getString(R.string.Rupee) + "" + balanceAmount
        tvWithdrawAmount.text = resources.getString(R.string.Rupee) + "" + transactionAmount

        //Toast.makeText(this, "$url", Toast.LENGTH_SHORT).show()

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