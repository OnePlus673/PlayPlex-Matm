package com.playplexmatm.activity.fragments.bills

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.playplexmatm.R
import com.playplexmatm.activity.fragments.HomeFragment
import com.playplexmatm.activity.fragments.HomeFragment.Companion.email
import com.playplexmatm.activity.fragments.HomeFragment.Companion.password
import com.playplexmatm.adapter.holder.CustomerClick
import com.playplexmatm.databinding.ActivityAddNewPaymentBinding
import com.playplexmatm.extentions.ADD_NEW_PARTY
import com.playplexmatm.extentions.ADD_NEW_PARTY_REQUEST
import com.playplexmatm.extentions.CANCEL
import com.playplexmatm.extentions.CUSTOMER_NAME
import com.playplexmatm.extentions.CUSTOMER_PHONE
import com.playplexmatm.extentions.GO_TO_SDK
import com.playplexmatm.extentions.beInvisible
import com.playplexmatm.extentions.beVisible
import com.playplexmatm.model.bills.Customer
import com.playplexmatm.model.bills.SaleBillRecord
import com.playplexmatm.util.toast
import `in`.credopay.payment.sdk.CredopayPaymentConstants
import `in`.credopay.payment.sdk.PaymentActivity
import `in`.credopay.payment.sdk.Utils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Random

class AddNewPaymentActivity : BaseActivity(), CustomerClick {
    private lateinit var binding: ActivityAddNewPaymentBinding
    var curnValue = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_payment)
        setUpViews()
    }

    private fun setUpViews() {
        defaultDate()
        paymentMode()
        generateBillNumber()
        binding.billNumberIcon.setOnClickListener {
            bsRenameBillNumber {
                binding.billNumber.text = it
            }
        }
        binding.date.setOnClickListener {
            showDatePickerDialog {
                binding.date.text = it
            }
        }
        binding.customerContainer.setOnClickListener {
            bsShowPartiesList(this@AddNewPaymentActivity) { callback ->
                when (callback) {
                    ADD_NEW_PARTY -> {
                        startActivityForResult(
                            Intent(this, AddNewPartyActivity::class.java),
                            ADD_NEW_PARTY_REQUEST
                        )
                    }
                }
            }
        }
        binding.save.setOnClickListener {
            savePaymentRecord()
        }
        binding.ivBack.setOnClickListener { finish() }
    }

    private fun savePaymentRecord() {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (TextUtils.isEmpty(binding.amount.text.toString().trim())) {
            binding.amount.error = "Amount is required."
            return
        }

        if (currentUser != null) {
            val userId = currentUser.uid
            val saleBillRef: DatabaseReference = database.reference.child("sales").child(userId)

            val newSaleBillRef = saleBillRef.push()
            val customer =
                Customer(
                    binding.customerName.text.toString().trim(),
                    binding.customerPhone.text.toString().trim()
                )
            val saleBillRecord = SaleBillRecord(
                "Payment in #${binding.billNumber.text.toString().trim()}",
                binding.date.text.toString().trim(),
                customer,
                binding.amount.text.toString().trim(),
                "",
                "",
                binding.paymentModeSpinner.selectedItem.toString(),
                ""
            )
            newSaleBillRef.setValue(saleBillRecord)
                .addOnSuccessListener {
                    toast("Payment Record Saved")
                    if (binding.paymentModeSpinner.selectedItem.toString() == "Debit Card") {
                        bsGoToSdk(binding.amount.text.toString().trim()) { callBack ->
                            when (callBack) {
                                GO_TO_SDK -> {
                                    finish()
                                    goToSdk(isDebitSelected = true)
                                }

                                CANCEL -> finish()
                            }
                        }
                    } else if (binding.paymentModeSpinner.selectedItem.toString() == "Credit Card") {
                        bsGoToSdk(binding.amount.text.toString().trim()) { callBack ->
                            when (callBack) {
                                GO_TO_SDK -> {
                                    finish()
                                    goToSdk(isCreditSelected = true)
                                }

                                CANCEL -> finish()
                            }
                        }
                    } else if (binding.paymentModeSpinner.selectedItem.toString() == "AEPS") {
                        bsGoToSdk(binding.amount.text.toString().trim()) { callBack ->
                            when (callBack) {
                                GO_TO_SDK -> {
                                    finish()
                                    goToSdk(isAepsSelected = true)
                                }

                                CANCEL -> finish()
                            }
                        }
                    } else {
                        finish()
                    }
                }
                .addOnFailureListener {
                    Log.wtf("Payment record error", it.message.toString())
                }
        }
    }

    private fun goToSdk(
        isDebitSelected: Boolean? = null,
        isCreditSelected: Boolean? = null,
        isAepsSelected: Boolean? = null
    ) {
        val amount = binding.amount.text.toString().trim().toInt() * 100
        var transactionType = 0
        if (isDebitSelected == true) {
            transactionType = CredopayPaymentConstants.MICROATM
        } else if (isCreditSelected == true) {
            transactionType = CredopayPaymentConstants.PURCHASE
        } else if (isAepsSelected == true) {
            transactionType = CredopayPaymentConstants.AEPS_CASH_WITHDRAWAL
        }

        Log.wtf("ppemail", email)
        Log.wtf("pppassword", password)
        Log.wtf("ppamount", amount.toString())
        Log.wtf("pptransaction", transactionType.toString())
        Log.wtf("ppCRN_U", getCurn())

        val intent = Intent(this@AddNewPaymentActivity, PaymentActivity::class.java)
        intent.putExtra("TRANSACTION_TYPE", transactionType)
        intent.putExtra("LOGIN_ID", HomeFragment.email)
        intent.putExtra("LOGIN_PASSWORD", HomeFragment.password)
        intent.putExtra("DEBUG_MODE", true)
        intent.putExtra("PRODUCTION", true)
        intent.putExtra("CRN_U", getCurn())
        intent.putExtra("AMOUNT", amount)
        intent.putExtra(
            "LOGO",
            Utils.getVariableImage(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.logo_tp
                )
            )
        )
        startActivityForResult(intent, 1)
    }

    private fun getCurn(): String {
        val r = Random(System.currentTimeMillis())
        curnValue = "PP" + (10000 + r.nextInt(20000)).toString()
        return curnValue
    }

    private fun generateBillNumber() {
        binding.billNumber.text = getNumber().toString()
        incrementNumber()
    }

    private fun incrementNumber() {
        val count = 1
        var defaultValue = getPreferences(MODE_PRIVATE).getInt("count_key_payment", count)
        ++defaultValue
        getPreferences(MODE_PRIVATE).edit().putInt("count_key_payment", defaultValue).commit()
    }

    private fun getNumber(): Int {
        val count = getPreferences(MODE_PRIVATE).getInt("count_key_payment", 1)
        println("The count value is $count")
        return count
    }

    private fun defaultDate() {
        val calendar = Calendar.getInstance()
        val dateFormatter = SimpleDateFormat("dd MMM yy", Locale.US)
        val formattedDate = dateFormatter.format(calendar.time)
        binding.date.text = formattedDate
    }

    private fun paymentMode() {
        val options = arrayOf("Cash", "Debit Card", "Credit Card", "AEPS", "UPI")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.paymentModeSpinner.adapter = adapter
        binding.paymentModeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = options[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle case where nothing is selected (optional)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_NEW_PARTY_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val customerName = data?.getStringExtra(CUSTOMER_NAME)
                val customerPhone = data?.getStringExtra(CUSTOMER_PHONE)
                if (customerName != null && customerPhone != null) {
                    binding.customerContainer.beInvisible()
                    binding.selectedCustomer.beVisible()
                    binding.customerName.text = customerName
                    binding.customerPhone.text = customerPhone
                    binding.balanceDue.text = "Current Balance 0"
                }
            }
        }
    }

    override fun customerClick(customer: Customer) {
        binding.customerContainer.beInvisible()
        binding.selectedCustomer.beVisible()
        binding.customerName.text = customer.name
        binding.customerPhone.text = customer.phone
        binding.balanceDue.text = "Current Balance 0"
        bsShowParties.dismiss()
    }
}