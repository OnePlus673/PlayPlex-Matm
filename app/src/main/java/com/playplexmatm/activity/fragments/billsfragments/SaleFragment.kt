package com.playplexmatm.activity.fragments.billsfragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.playplexmatm.R
import com.playplexmatm.activity.fragments.bills.AddNewSaleActivity
import com.playplexmatm.adapter.bills.SalesAdapter
import com.playplexmatm.extentions.beGone
import com.playplexmatm.extentions.beVisible
import com.playplexmatm.model.bills.SaleBillRecord

class SaleFragment : Fragment() {

    lateinit var addSale:Button
    lateinit var salesRv:RecyclerView
    lateinit var progress:ProgressBar
    private val saleBillRecords: MutableList<SaleBillRecord> = mutableListOf()
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_sale2, container, false)
        addSale = view.findViewById(R.id.addSale)
        salesRv = view.findViewById(R.id.salesRv)
        progress = view.findViewById(R.id.progress)
        setUpViews()
        fetchSaleBillRecords()
        return view
    }

    private fun setUpViews() {
        addSale.setOnClickListener {
            startActivity(Intent(requireContext(),AddNewSaleActivity::class.java))
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
                            saleBillRecords.add(saleBillRecord)
                        }
                    }
                    progress.beGone()
                    salesRv.layoutManager = LinearLayoutManager(requireActivity(),RecyclerView.VERTICAL,false)
                    val adapter = SalesAdapter(requireContext(),saleBillRecords)
                    salesRv.adapter = adapter
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    progress.beGone()
                    Log.wtf("Sales Record error",error.message)
                }
            })
        }
    }
}