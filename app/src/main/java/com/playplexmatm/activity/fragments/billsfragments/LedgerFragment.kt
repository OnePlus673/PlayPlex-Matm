package com.playplexmatm.activity.fragments.billsfragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.playplexmatm.R
import com.playplexmatm.activity.fragments.bills.LedgerDetailsActivity
import com.playplexmatm.adapter.bills.CustomerAdapter
import com.playplexmatm.adapter.holder.CustomerClick
import com.playplexmatm.extentions.CUSTOMER_BALANCE
import com.playplexmatm.extentions.CUSTOMER_NAME
import com.playplexmatm.extentions.beGone
import com.playplexmatm.extentions.beVisible
import com.playplexmatm.model.bills.Customer

class LedgerFragment : Fragment(), CustomerClick {
    lateinit var ledgerRv: RecyclerView
    lateinit var progress: ProgressBar
    lateinit var searchLedger: AppCompatEditText
    private val ledgerRecords: MutableList<Customer> = mutableListOf()
    lateinit var adapter: CustomerAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ledger, container, false)
        setUpViews(view)
        fetchLedgerRecords()
        searchLedger()
        return view
    }

    private fun searchLedger() {
        searchLedger.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                val filteredList: ArrayList<Customer> = ArrayList()
                for (list in ledgerRecords) {
                    if (list.name.toLowerCase().contains(query)) {
                        filteredList.add(list)
                    }
                }
                adapter.updateList(filteredList)
            }
        })
    }

    private fun fetchLedgerRecords() {
        progress.beVisible()
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val paymentRef: DatabaseReference = database.reference.child("customers").child(userId)

            paymentRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    ledgerRecords.clear()
                    for (billSnapshot in snapshot.children) {
                        val customerRecord = billSnapshot.getValue(Customer::class.java)
                        if (customerRecord != null) {
                            ledgerRecords.add(customerRecord)
                        }
                    }
                    progress.beGone()
                    ledgerRv.layoutManager = LinearLayoutManager(requireActivity()).apply {
                        reverseLayout = true
                        stackFromEnd = true
                    }
                    adapter = CustomerAdapter(
                        requireContext(),
                        ledgerRecords,
                        this@LedgerFragment,
                        isLedger = true
                    )
                    ledgerRv.adapter = adapter
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    progress.beGone()
                    Log.wtf("Ledger Record error", error.message)
                }
            })
        }
    }

    private fun setUpViews(view: View) {
        ledgerRv = view.findViewById(R.id.ledgerRv)
        progress = view.findViewById(R.id.progress)
        searchLedger = view.findViewById(R.id.searchLedger)
    }

    override fun customerClick(customer: Customer) {
        startActivity(Intent(requireContext(),LedgerDetailsActivity::class.java).apply {
            putExtra(CUSTOMER_NAME,customer.name)
            putExtra(CUSTOMER_BALANCE,customer.currentBalance)
        })
    }
}