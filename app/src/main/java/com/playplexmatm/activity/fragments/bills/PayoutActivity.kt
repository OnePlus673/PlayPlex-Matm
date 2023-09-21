package com.playplexmatm.activity.fragments.bills

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.playplexmatm.R
import com.playplexmatm.aeps.network_calls.AppApiCalls
import com.playplexmatm.databinding.ActivityPayoutBinding
import com.playplexmatm.microatm.MATMTestActivity
import com.playplexmatm.util.AppCommonMethods
import com.playplexmatm.util.AppConstants
import com.playplexmatm.util.AppPrefs
import com.playplexmatm.util.toast
import com.sg.swapnapay.model.UserModel
import kotlinx.android.synthetic.main.activity_payout.etAmount
import kotlinx.android.synthetic.main.activity_payout.tvAccounHolderNameRv
import kotlinx.android.synthetic.main.activity_payout.tvAccounNumberRv
import kotlinx.android.synthetic.main.activity_payout.tvBankNameRv
import kotlinx.android.synthetic.main.activity_payout.tvIFSCRv
import org.json.JSONObject

class PayoutActivity : AppCompatActivity(),AppApiCalls.OnAPICallCompleteListener {
    lateinit var binding: ActivityPayoutBinding
    lateinit var userModel : UserModel
    private val GET_CHARGE: String = "GET_CHARGE"
    var charge = ""
    private val SEND_AEPS_PAYOUT: String = "SEND_AEPS_PAYOUT"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payout)
        val gson = Gson()
        val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, this)
        userModel = gson.fromJson(json, UserModel::class.java)
        setUpViews()
        getProfileApi(userModel.cus_mobile)
    }

    private fun setUpViews() {
        binding.ivBackBtn.setOnClickListener { finish() }
        binding.btnSubmit.setOnClickListener {
            if(binding.etAmount.text.toString().isNullOrEmpty())
            {
                binding.etAmount.requestFocus()
                binding.etAmount.error = "Invalid Amount"
            }
            else
            {
                getCharge(binding.etAmount.text.toString())

            }
        }
    }
    private fun getCharge(
        amount: String
    ) {
        binding.progressBar.visibility = View.VISIBLE

        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall =
                AppApiCalls(this, GET_CHARGE, this)
            mAPIcall.getCharge(amount,userModel.cus_id)
        } else {

            Toast.makeText(this, "Internet Error", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getProfileApi(
        cus_id: String
    ) {
        binding.progressBar.visibility = View.VISIBLE
        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(
                this,
                AppConstants.PROFILE_API,
                this
            )
            mAPIcall.getProfile(cus_id)

        } else {
            toast(getString(R.string.error_internet))
        }
    }
    private fun aepsPayout(
        cus_id: String, bank_name: String, account_number: String,
        ifsc_code: String, account_holder_name: String, amount: String,
        charge: String, type: String
    ) {
        binding.progressBar.visibility = View.VISIBLE
        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall =
                AppApiCalls(this, SEND_AEPS_PAYOUT, this)
            mAPIcall.aepsPayout(
                cus_id, bank_name, account_number,
                ifsc_code, account_holder_name, amount,
                charge, type
            )
        } else {
            Toast.makeText(this, "Internet Error", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onAPICallCompleteListner(item: Any?, flag: String?, result: String) {
        if (flag.equals(AppConstants.PROFILE_API)) {
            binding.progressBar.visibility = View.GONE
            Log.e(AppConstants.PROFILE_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val messageCode = jsonObject.getString(AppConstants.MESSAGE)

            //   val token = jsonObject.getString(AppConstants.TOKEN)
            Log.e(AppConstants.STATUS, status)
            Log.e(AppConstants.MESSAGE, messageCode)
            if (status.contains(AppConstants.TRUE)) {


                val users = jsonObject.getJSONArray(AppConstants.RESULT)
                for (i in 0 until users.length()) {
                    val notifyObjJson = users.getJSONObject(i)

                    userModel = Gson()
                        .fromJson(notifyObjJson.toString(), UserModel::class.java)
                }

                binding.tvAccounHolderNameRv.setText(userModel.aeps_bankAccountName)

                binding.tvBankNameRv.setText(userModel.aeps_bankName)

                binding.tvIFSCRv.setText(userModel.aeps_bankIfscCode)

                binding.tvAccounNumberRv.setText(userModel.aeps_AccountNumber)


            } else {
                if (messageCode.equals(getString(R.string.error_expired_token))) {
                    AppCommonMethods.logoutOnExpiredDialog(this)

                } else {
                    toast(messageCode.trim())
                }
            }
        }

        if (flag.equals(GET_CHARGE)) {
            Log.e("GET_CHARGE", result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            Log.e(AppConstants.STATUS, status)
            if (status.contains("true")) {
                binding.progressBar.visibility = View.INVISIBLE
                charge = jsonObject.getString("result")
                showConfirmPaymentDialogPaytm()

            } else {
                binding.progressBar.visibility = View.INVISIBLE
            }
        }

        if (flag.equals(SEND_AEPS_PAYOUT)) {
            Log.e("SEND_AEPS_PAYOUT", result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            Log.e(AppConstants.STATUS, status)
            //Log.e(AppConstants.MESSAGE_CODE, messageCode);
            if (status.contains("true")) {
                binding.progressBar.visibility = View.GONE
                val response = jsonObject.getString("message")
                toast(response)
                val intent = Intent(this, MATMTestActivity::class.java)
                startActivity(intent)

            } else {
                val response = jsonObject.getString("message")
                //TODO WASI COMMENTED :: CHANGE HERE ONLY showSuccessRechargeDialog
                binding.progressBar.visibility = View.GONE
                val intent = Intent(this, MATMTestActivity::class.java)
                startActivity(intent)
            }
        }
    }
    private fun showConfirmPaymentDialogPaytm() {
        val builder1 =
            AlertDialog.Builder(this)
        builder1.setTitle("Please Verify..")
        builder1.setMessage(
            "Do you want to Proceed Fund Transfer for\n" +
                    "Amount : ${etAmount.text.toString()}\n" +
                    "Charge : $charge"
            //     "Services Charge: ${charge}"
        )
        builder1.setCancelable(true)
        builder1.setPositiveButton(
            "OK"
        ) { dialog, id ->

            aepsPayout(
                userModel.cus_id, tvBankNameRv.text.toString(),
                etAmount.text.toString(), tvIFSCRv.text.toString(),
                tvAccounHolderNameRv.text.toString(), etAmount.text.toString(),
                charge,"payoutToBank"
            )

            dialog.cancel()
        }
        builder1.setNegativeButton(
            "CANCEL"
        ) { dialog, id ->
            dialog.cancel()
        }
        val alert11 = builder1.create()
        alert11.show()
    }
}