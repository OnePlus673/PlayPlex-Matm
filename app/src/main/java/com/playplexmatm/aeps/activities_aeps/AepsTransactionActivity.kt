package com.playplexmatm.aeps.activities_aeps

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.payplex.aeps.activities_aeps.aepsfinger.AepsBankModel
import com.payplex.aeps.activities_aeps.aepsfinger.BankAepsListAdapter
import com.playplexmatm.R
import com.playplexmatm.aeps.activities_aeps.aepsfinger.MantraDeviceActivity
import com.playplexmatm.aeps.model.UserModel
import com.playplexmatm.aeps.network_calls.AppApiCalls
import com.playplexmatm.util.AppCommonMethods
import com.playplexmatm.util.AppConstants
import com.playplexmatm.util.AppPrefs
import com.playplexmatm.util.toast
import kotlinx.android.synthetic.main.activity_aeps_transaction.*
import kotlinx.android.synthetic.main.activity_aeps_transaction.view.*
import kotlinx.android.synthetic.main.layout_list_bottomsheet_banklist.view.*
import org.json.JSONArray
import org.json.JSONObject

class AepsTransactionActivity : AppCompatActivity(), AppApiCalls.OnAPICallCompleteListener,
    BankAepsListAdapter.ListAdapterListener, AdapterView.OnItemSelectedListener {

    var bottomSheetDialogUsers: BottomSheetDialog? = null
    lateinit var bankListAdapter: BankAepsListAdapter

    var bankListModelArrayList = ArrayList<AepsBankModel>()
    private val AEPS_BANKS: String = "AEPS_BANKS"
    private val AEPS_TRANSACTION: String = "AEPS_TRANSACTION"

    var pidData: String = ""
    var transactionType: String = ""
    lateinit var userModel: UserModel
    lateinit var nationalBankIdenticationNumber: String
    var sendAmount = "0"
    var list_of_items = arrayOf("Mantra MFS100", "Morpho")
    lateinit var selected_device: String

    lateinit var jsonArray: JSONArray
    lateinit var paymentArray: JSONArray
    lateinit var detailsArray: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aeps_transaction)

        custToolbar.ivBackBtn.setOnClickListener {
            onBackPressed()
        }


        val gson = Gson()
        val json = AppPrefs.getStringPref("userModel", this)
        userModel = gson.fromJson(json, UserModel::class.java)

        val bundle = intent.extras

        if (bundle != null) {
            sendAmount = bundle.getString("amount").toString()

            etAepsAmount.setText(sendAmount.toInt().toString())
            etAepsAmount.isEnabled = false
            etAepsAmount.isFocusable = false
        }

        tvSelectBank.setOnClickListener {
            bankListAeps()
        }



        rbCashWithdrawal.setOnClickListener {
            transactionType = "cashwithdrawal"
            rbCashWithdrawal.isChecked = true
            if (rbCashWithdrawal.isChecked) {
                rbBalanceCheck.isChecked = false
                rbMiniStatement.isChecked = false
                rbAadharPayment.isChecked = false
                ll_aepsamount.visibility = View.VISIBLE
            }
        }

        rbBalanceCheck.setOnClickListener {
            transactionType = "balancecheck"

            rbBalanceCheck.isChecked = true
            if (rbBalanceCheck.isChecked) {
                rbCashWithdrawal.isChecked = false
                rbMiniStatement.isChecked = false
                rbAadharPayment.isChecked = false
                ll_aepsamount.visibility = View.GONE

            }
        }

        rbMiniStatement.setOnClickListener {
            transactionType = "ministatement"

            rbMiniStatement.isChecked = true
            if (rbMiniStatement.isChecked) {
                rbCashWithdrawal.isChecked = false
                rbBalanceCheck.isChecked = false
                rbAadharPayment.isChecked = false
                ll_aepsamount.visibility = View.GONE

            }
        }

        rbAadharPayment.setOnClickListener {
            transactionType = "aadharpay"
            rbAadharPayment.isChecked = true
            if (rbAadharPayment.isChecked) {
                rbCashWithdrawal.isChecked = false
                rbBalanceCheck.isChecked = false
                rbMiniStatement.isChecked = false
                ll_aepsamount.visibility = View.VISIBLE

            }
        }


        btnSubmit.setOnClickListener {


            if (etAepsAadharNo.text.toString().length < 12 || etAepsAadharNo.text.isNullOrEmpty()) {
                etAepsAadharNo.requestFocus()
                etAepsAadharNo.error = "Invalid Aadhar Number"

            } else if (!AppCommonMethods.checkForMobile(etAepsMobileNumber)) {
                etAepsMobileNumber.requestFocus()
                etAepsMobileNumber.error = "Invalid Mobile"

            } else if (tvSelectBank.text.toString().isNullOrEmpty()) {

                tvSelectBank.requestFocus()
                tvSelectBank.error = "Please Select Bank"
            } else if (transactionType.isNullOrEmpty()) {
                toast("Please Select Transaction Type")
            } else {

                if (transactionType.equals("ministatement") || transactionType.equals("balancecheck")) {
                    sendAmount = "0"
                    if (selected_device.isNullOrEmpty()) {

                        Toast.makeText(this, "Please Select A Device", Toast.LENGTH_SHORT).show()
                    } else if (selected_device.equals("Mantra MFS100")) {
                        val bundle = Bundle()
                        bundle.putString("flag","aeps")
                        bundle.putString("cus_id", userModel.cus_id)
                        bundle.putString("aadhar_no", etAepsAadharNo.text.toString())
                        bundle.putString(
                            "nationalBankIdenticationNumber",
                            nationalBankIdenticationNumber
                        )
                        bundle.putString("mobile_no", etAepsMobileNumber.text.toString())
                        bundle.putString("transactionType", transactionType)
                        bundle.putString("sendAmount", sendAmount)

                        val intent = Intent(this, MantraDeviceActivity::class.java)
                        intent.putExtras(bundle)
                        startActivityForResult(intent, 108)

                    }
                } else {
                    if (etAepsAmount.text.toString().isNullOrEmpty() || etAepsAmount.text.toString().toInt() <= 0
                    ) {
                        etAepsAmount.requestFocus()
                        etAepsAmount.error = "Invalid Amount"
                    } else {
                        sendAmount = etAepsAmount.text.toString()

                            /*       val intent = Intent(this, AepsTransactionActivity::class.java)
                                   startActivity(intent)*/

                            val bundle = Bundle()
                            bundle.putString("cus_id", userModel.cus_id)
                            bundle.putString("aadhar_no", etAepsAadharNo.text.toString())
                            bundle.putString(
                                "nationalBankIdenticationNumber",
                                nationalBankIdenticationNumber
                            )
                            bundle.putString("mobile_no", etAepsMobileNumber.text.toString())
                            bundle.putString("transactionType", transactionType)
                            bundle.putString("sendAmount", sendAmount)
                            bundle.putString("bankName", tvSelectBank.text.toString())

                            val intent = Intent(this, MantraDeviceActivity::class.java)
                            intent.putExtras(bundle)
                            startActivityForResult(intent, 108)

                    }

                }

            }
        }

        spinner!!.setOnItemSelectedListener(this)

        // Create an ArrayAdapter using a simple spinner layout and languages array
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, list_of_items)
        // Set layout to use when the list of choices appear
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        spinner!!.setAdapter(aa)


    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selected_device = list_of_items[position]
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    fun bankListAeps() {
        progress_bar.visibility = View.VISIBLE

        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall =
                AppApiCalls(this, AEPS_BANKS, this)
            mAPIcall.bankListAeps()
        } else {

            Toast.makeText(this, "Internet Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun aepsTransaction(
        cus_id: String,
        txtPidData: String,
        adhaarNumber: String,
        nationalBankIdenticationNumber: String,
        mobileNumber: String,
        type: String,
        transactionAmount: String
    ) {
        progress_bar.visibility = View.VISIBLE

        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall =
                AppApiCalls(this, AEPS_TRANSACTION, this)
            mAPIcall.aepsTransaction(
                cus_id,
                txtPidData,
                adhaarNumber,
                nationalBankIdenticationNumber,
                mobileNumber,
                type,
                transactionAmount
            )
        } else {
            Toast.makeText(this, "Internet Error", Toast.LENGTH_SHORT).show()
        }
    }


    private fun ShowBottomSheetBankList() {
        val view: View = layoutInflater.inflate(R.layout.layout_list_bottomsheet_banklist, null)
        view.etSearchMobName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable?) {

                filter(s.toString());

            }
        })

        view.rvBankList.apply {
            layoutManager = LinearLayoutManager(this@AepsTransactionActivity)
            bankListAdapter = BankAepsListAdapter(
                context, bankListModelArrayList, this@AepsTransactionActivity
            )
            view.rvBankList.adapter = bankListAdapter
        }
        bottomSheetDialogUsers = BottomSheetDialog(this)
        bottomSheetDialogUsers!!.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetDialogUsers!!.show()
    }

    override fun onClickAtOKButton(mobileRechargeModal: AepsBankModel?) {

        if (mobileRechargeModal != null) {
            tvSelectBank.setText(mobileRechargeModal.bankName)
            nationalBankIdenticationNumber = mobileRechargeModal.iinno
        }

        bottomSheetDialogUsers!!.dismiss()
    }


    fun filter(text: String) {
        val temp: MutableList<AepsBankModel> = ArrayList()
        for (d in bankListModelArrayList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.bankName.contains(text, ignoreCase = true)) {

                temp.add(d)
            }
        }
        //update recyclerview
        bankListAdapter.updateList(temp)
    }

    override fun onAPICallCompleteListner(item: Any?, flag: String?, result: String) {
        if (flag.equals(AEPS_BANKS)) {
            bankListModelArrayList.clear()
            Log.e("AEPS_BANKS", result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            Log.e(AppConstants.STATUS, status)
            if (status.contains("true")) {

                progress_bar.visibility = View.INVISIBLE

                val cast = jsonObject.getJSONArray("result")

                for (i in 0 until cast.length()) {
                    val notifyObjJson = cast.getJSONObject(i)
                    val aeps_bank_id = notifyObjJson.getString("aeps_bank_id")
                    Log.e("aeps_bank_id ", aeps_bank_id)
                    val bankListModel = Gson()
                        .fromJson(
                            notifyObjJson.toString(),
                            AepsBankModel::class.java
                        )


                    bankListModelArrayList.add(bankListModel)
                }

                ShowBottomSheetBankList()

            } else {
                progress_bar.visibility = View.INVISIBLE


            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 108) {
            if(resultCode == Activity.RESULT_OK) {
                val extra: Bundle = data!!.getBundleExtra("data")!!

                Log.e("data", extra.getString("transactionType")!!)
                val bundle = Bundle()
                bundle.putString("transactionType", extra.getString("transactionType")!!)
                val resultIntent = Intent()
                resultIntent.putExtra("data", bundle)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}