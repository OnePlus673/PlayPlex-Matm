package com.playplexmatm.activity.fragments.billsfragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.playplexmatm.R
import com.playplexmatm.activity.fragments.bills.AddNewPaymentActivity
import com.playplexmatm.activity.fragments.bills.AddNewSaleActivity
import com.playplexmatm.adapter.bills.SalesAdapter
import com.playplexmatm.extentions.beGone
import com.playplexmatm.extentions.beVisible
import com.playplexmatm.model.bills.SaleBillRecord
import kotlinx.android.synthetic.main.fragment_payment.addPayment

class PaymentFragment : Fragment() {
    lateinit var addPayment: Button
    lateinit var paymentRv: RecyclerView
    lateinit var progress: ProgressBar
    lateinit var searchSale: AppCompatEditText
    private val saleBillRecords: MutableList<SaleBillRecord> = mutableListOf()
    lateinit var adapter : SalesAdapter
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_payment, container, false)
        addPayment = view.findViewById(R.id.addPayment)
        paymentRv = view.findViewById(R.id.paymentRv)
        progress = view.findViewById(R.id.progress)
        searchSale = view.findViewById(R.id.searchSale)
        setUpViews()
        fetchSaleBillRecords()
        searchSaleBill()
        return view
    }
    private fun searchSaleBill() {
        searchSale.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                val filteredList: ArrayList<SaleBillRecord> = ArrayList()
                for (list in saleBillRecords) {
                    if (list.customer.name.toLowerCase().contains(query)) {
                        filteredList.add(list)
                    }
                }
                adapter.updateList(filteredList)
            }
        })
    }

    private fun setUpViews() {
        addPayment.setOnClickListener {
            startActivity(Intent(requireContext(), AddNewPaymentActivity::class.java))
        }
    }
    private fun fetchSaleBillRecords() {
        progress.beVisible()
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val saleBillRef: DatabaseReference = database.reference.child("sales").child(userId)

            saleBillRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    saleBillRecords.clear()
                    for (billSnapshot in snapshot.children) {
                        val saleBillRecord = billSnapshot.getValue(SaleBillRecord::class.java)
                        if (saleBillRecord != null) {
                            if (saleBillRecord.saleBillNumber.startsWith("Payment in")) {
                                saleBillRecords.add(saleBillRecord)
                            }
                        }
                    }
                    progress.beGone()
                    paymentRv.layoutManager = LinearLayoutManager(requireActivity()).apply {
                        reverseLayout = true
                        stackFromEnd = true
                    }
                    adapter = SalesAdapter(requireContext(),saleBillRecords,0)
                    paymentRv.adapter = adapter
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    progress.beGone()
                    Log.wtf("Payment Record error",error.message)
                }
            })
        }
    }

}