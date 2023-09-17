package com.playplexmatm.activity.fragments.bills

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
import com.playplexmatm.databinding.ActivityLedgerDetailsBinding
import com.playplexmatm.extentions.CUSTOMER_BALANCE
import com.playplexmatm.extentions.CUSTOMER_NAME
import com.playplexmatm.extentions.beGone
import com.playplexmatm.extentions.beVisible
import com.playplexmatm.extentions.extractNumber
import com.playplexmatm.extentions.extractString
import com.playplexmatm.model.bills.SaleBillRecord

class LedgerDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityLedgerDetailsBinding
    private val ledgerRecords: MutableList<SaleBillRecord> = mutableListOf()
    lateinit var salesAdapter: SalesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ledger_details)
        setUpViews()
        fetchSaleBillRecords()
    }

    private fun fetchSaleBillRecords() {
        binding.progress.beVisible()
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val saleBillRef: DatabaseReference = database.reference.child("sales").child(userId)

            saleBillRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    ledgerRecords.clear()
                    for (billSnapshot in snapshot.children) {
                        val saleBillRecord = billSnapshot.getValue(SaleBillRecord::class.java)
                        if (saleBillRecord != null) {
                            if (saleBillRecord.customer.name == intent.getStringExtra(CUSTOMER_NAME)) {
                                ledgerRecords.add(saleBillRecord)
                            }
                        }
                    }
                    binding.progress.beGone()
                    binding.ledgerRv.layoutManager =
                        LinearLayoutManager(this@LedgerDetailsActivity).apply {
                            reverseLayout = true
                            stackFromEnd = true
                        }
                    salesAdapter = SalesAdapter(this@LedgerDetailsActivity, ledgerRecords, 1)
                    binding.ledgerRv.adapter = salesAdapter
                    salesAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.progress.beGone()
                    Log.wtf("Ledger Record error", error.message)
                }
            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpViews() {
        binding.tvTitle.text = intent.getStringExtra(CUSTOMER_NAME)
        binding.getTv.text = "You will ${intent.getStringExtra(CUSTOMER_BALANCE)?.extractString()}"
        binding.customerBalance.text =
            "₹${intent.getStringExtra(CUSTOMER_BALANCE)?.extractNumber()}"
        if (intent.getStringExtra(CUSTOMER_BALANCE)?.startsWith("get") == true) {
            binding.customerBalance.setTextColor(Color.RED)
        } else if (intent.getStringExtra(CUSTOMER_BALANCE)?.startsWith("give") == true) {
            binding.customerBalance.setTextColor(Color.GREEN)
        } else {
            binding.customerBalance.setTextColor(Color.GRAY)
        }
        binding.ivBack.setOnClickListener { finish() }
    }
}