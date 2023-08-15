package com.playplexmatm.activity.fragments.bills

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.playplexmatm.R
import com.playplexmatm.adapter.bills.CustomerAdapter
import com.playplexmatm.adapter.holder.CustomerClick
import com.playplexmatm.databinding.ActivityAddNewSaleBinding
import com.playplexmatm.extentions.ADD_NEW_PARTY
import com.playplexmatm.extentions.ADD_NEW_PARTY_REQUEST
import com.playplexmatm.extentions.CUSTOMER_NAME
import com.playplexmatm.extentions.CUSTOMER_PHONE
import com.playplexmatm.extentions.beGone
import com.playplexmatm.extentions.beInvisible
import com.playplexmatm.extentions.beVisible
import com.playplexmatm.extentions.generateUniqueRandom
import com.playplexmatm.model.bills.Customer
import com.playplexmatm.model.bills.SaleBillRecord
import com.playplexmatm.util.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddNewSaleActivity : BaseActivity(), CustomerClick {
    private lateinit var binding: ActivityAddNewSaleBinding

    companion object {
        var customerClick: CustomerClick? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_sale)
        setUpViews()
    }

    private fun setUpViews() {
        defaultDate()
        paymentMode()
        paymentDue()
        binding.billNumber.text = generateUniqueRandom().toString()
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
        binding.searchParties.setOnClickListener {
//            fetchCustomerData(
//                onDataLoaded = { customerList ->

            bsShowPartiesList(this@AddNewSaleActivity) { callback ->
                when (callback) {
                    ADD_NEW_PARTY -> {
                        startActivityForResult(
                            Intent(this, AddNewPartyActivity::class.java),
                            ADD_NEW_PARTY_REQUEST
                        )
                    }
                }
            }
//                },
//                onError = { error ->
//                    Log.wtf("Customer fetch error", error.toString())
//                }
//            )
        }
        binding.addNewParty.setOnClickListener {
            startActivityForResult(
                Intent(this, AddNewPartyActivity::class.java),
                ADD_NEW_PARTY_REQUEST
            )
        }
        binding.notesContainer.setOnClickListener {
            bsAddNotes { note ->
                binding.notesHere.text = note
            }
        }
        binding.generateSaleBill.setOnClickListener {
            saveSaleBillRecord()
        }
        binding.ivBack.setOnClickListener { finish() }
    }

    private fun paymentDue() {
        binding.receivedAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(text: Editable?) {
                val totalSaleAmount = binding.amount.text.toString().trim().toIntOrNull() ?: 0
                val receivedAmount =
                    binding.receivedAmount.text.toString().trim().toIntOrNull() ?: 0
                if (totalSaleAmount > 0) {
                    if (receivedAmount <= totalSaleAmount) {
                        val remainingAmount = totalSaleAmount - receivedAmount
                        binding.balanceDue.text = remainingAmount.toString()
                    } else {
                        binding.receivedAmount.error = "Should be smaller than sale bill"
                    }
                } else {
                    binding.receivedAmount.error = "Enter sale bill amount first"
                }
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    private fun saveSaleBillRecord() {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (TextUtils.isEmpty(binding.amount.text.toString().trim())) {
            binding.amount.error = "Amount is required."
            return
        }
        if (binding.paymentModeSpinner.selectedItem != "Unpaid") {
            if (TextUtils.isEmpty(binding.receivedAmount.text.toString().trim())) {
                binding.receivedAmount.error = "Amount is required."
                return
            }
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
                binding.billNumber.text.toString().trim(),
                binding.date.text.toString().trim(),
                customer,
                binding.amount.text.toString().trim(),
                binding.receivedAmount.text.toString().trim(),
                binding.balanceDue.text.toString().trim(),
                binding.paymentModeSpinner.selectedItem.toString(),
                binding.notesHere.text.toString().trim()
            )
            newSaleBillRef.setValue(saleBillRecord)
                .addOnSuccessListener {
                    toast("Sale Record Saved")
                    finish()
                }
                .addOnFailureListener {
                    Log.wtf("Sale record error", it.message.toString())
                }
        }
    }

    private fun defaultDate() {
        val calendar = Calendar.getInstance()
        val dateFormatter = SimpleDateFormat("dd MMM yy", Locale.US)
        val formattedDate = dateFormatter.format(calendar.time)
        binding.date.text = formattedDate
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
                    binding.customerPhone1.text = customerPhone
                }
            }
        }
    }

    override fun customerClick(customer: Customer) {
        binding.customerContainer.beInvisible()
        binding.selectedCustomer.beVisible()
        binding.customerName.text = customer.name
        binding.customerPhone.text = customer.phone
        binding.customerPhone1.text = customer.phone
        toast("Customer is Selected")
    }

    private fun paymentMode() {
        val options = arrayOf("Unpaid", "Cash", "Debit Card", "Credit Card", "AEPS", "UPI")
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
                    if (selectedItem != "Unpaid") {
                        binding.receivedContainer.beVisible()
                    } else {
                        binding.receivedContainer.beGone()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle case where nothing is selected (optional)
                }
            }
    }

}