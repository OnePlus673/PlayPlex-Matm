package com.playplexmatm.activity.fragments.bills

import android.os.Bundle
import android.text.TextUtils
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.playplexmatm.R
import com.playplexmatm.databinding.ActivityAddNewPartyBinding
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
            userRef.child("name").setValue(partyName)
            userRef.child("phone").setValue(phoneNumber)
            toast("Customer added")
        }else {
            toast("user is not logged in")
        }
    }
}