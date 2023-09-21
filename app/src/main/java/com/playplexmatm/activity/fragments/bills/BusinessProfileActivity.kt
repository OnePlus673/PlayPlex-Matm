package com.playplexmatm.activity.fragments.bills

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.playplexmatm.R
import com.playplexmatm.adapter.bills.SalesAdapter
import com.playplexmatm.databinding.ActivityBusinessProfileBinding
import com.playplexmatm.databinding.ActivityLedgerDetailsBinding
import com.playplexmatm.extentions.beGone
import com.playplexmatm.extentions.beVisible
import com.playplexmatm.model.bills.BusinessProfile
import com.playplexmatm.model.bills.SaleBillRecord
import com.playplexmatm.util.toast

class BusinessProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityBusinessProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_business_profile)
        setUpView()
    }

    private fun setUpView() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        binding.addBusiness.setOnClickListener {
            saveBusinessProfile()
        }
        binding.ivBack.setOnClickListener { finish() }
    }

    private fun saveBusinessProfile() {
        val user = auth.currentUser
        val userId = user?.uid
        val businessName = binding.businessName.text.toString().trim()
        val phoneNumber = binding.contactNumber.text.toString().trim()
        val businessAddress = binding.businessAddress.text.toString().trim()
        if (TextUtils.isEmpty(businessName)) {
            binding.businessName.error = "Business Name is required."
            return
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            binding.contactNumber.error = "Contact number is required."
            return
        }
        if (TextUtils.isEmpty(businessAddress)) {
            binding.businessAddress.error = "Business address is required."
            return
        }
        val databaseReference = userId?.let {
            database.reference
                .child("business_profiles")
                .child(it)
        }

            val businessProfileModel = BusinessProfile(
                businessName,
                phoneNumber,
                businessAddress,
                binding.city.text.toString().trim(),
                binding.state.text.toString().trim(),
                binding.panCardNo.text.toString().trim(),
                binding.gstinNo.text.toString().trim()
            )
            databaseReference?.setValue(businessProfileModel)
                ?.addOnSuccessListener {
                    toast("Business Profile Added")
                    finish()
                }
                ?.addOnFailureListener { e ->
                    Log.wtf("Business profile record error", e.message.toString())
                }
//        }
//        val userUid = user?.uid
//        if (userUid != null) {
//            val userRef = database.reference.child("businessProfile").child(userUid)
////            val businessProfileRef = userRef.push()
//            val businessProfileModel = BusinessProfile(
//                businessName,
//                phoneNumber,
//                businessAddress,
//                binding.city.text.toString().trim(),
//                binding.state.text.toString().trim(),
//                binding.panCardNo.text.toString().trim(),
//                binding.gstinNo.text.toString().trim()
//            )
//            userRef.setValue(businessProfileModel).addOnSuccessListener {
//                toast("Business Profile Added")
//            }
//                .addOnFailureListener {
//                    Log.wtf("Business profile record error", it.message.toString())
//                }
//            finish()
//        } else {
//            toast("User is not logged in")
//        }
    }

}