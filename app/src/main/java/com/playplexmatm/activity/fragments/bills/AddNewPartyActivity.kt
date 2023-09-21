package com.playplexmatm.activity.fragments.bills

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.playplexmatm.R
import com.playplexmatm.databinding.ActivityAddNewPartyBinding
import com.playplexmatm.extentions.CUSTOMER_NAME
import com.playplexmatm.extentions.CUSTOMER_PHONE
import com.playplexmatm.extentions.beVisible
import com.playplexmatm.model.bills.Customer
import com.playplexmatm.util.toast

class AddNewPartyActivity : BaseActivity() {
    lateinit var binding: ActivityAddNewPartyBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_party)
        setUpViews()
    }

    private fun setUpViews() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        binding.addCustomer.setOnClickListener {
            saveNewCustomer()
        }
        binding.addGstinAddress.setOnClickListener {
            binding.gstinContainer.beVisible()
        }
        binding.ivBack.setOnClickListener { finish() }
    }

    private fun saveNewCustomer() {
        val user = auth.currentUser
        val partyName = binding.partyName.text.toString().trim()
        val phoneNumber = binding.phoneNumber.text.toString().trim()
        if (TextUtils.isEmpty(partyName)) {
            binding.partyName.error = "Name is required."
            return
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            binding.phoneNumber.error = "Phone number is required."
            return
        }

        val userUid = user?.uid
        if (userUid != null) {
            val userRef = database.reference.child("customers").child(userUid)
            val newCustomerRef = userRef.push()
            newCustomerRef.child("name").setValue(partyName)
            newCustomerRef.child("phone").setValue(phoneNumber)
            newCustomerRef.child("gstin").setValue(binding.gstin.text.toString().trim())
            newCustomerRef.child("buildingNumber").setValue(binding.buildingNumber.text.toString().trim())
            newCustomerRef.child("area").setValue(binding.area.text.toString().trim())
            newCustomerRef.child("pinCode").setValue(binding.pinCode.text.toString().trim())
            newCustomerRef.child("city").setValue(binding.city.text.toString().trim())
            newCustomerRef.child("state").setValue(binding.state.text.toString().trim())
            newCustomerRef.child("currentBalance").setValue("")
            newCustomerRef.child("dateTime").setValue("")
            toast("Customer added")
            sendCustomerData(partyName, phoneNumber)
        } else {
            toast("User is not logged in")
        }
    }

    private fun sendCustomerData(name: String, phone: String) {
        val returnIntent = Intent()
        returnIntent.putExtra(CUSTOMER_NAME, name)
        returnIntent.putExtra(CUSTOMER_PHONE, phone)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}