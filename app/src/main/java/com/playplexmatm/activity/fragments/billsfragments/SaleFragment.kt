package com.playplexmatm.activity.fragments.billsfragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.playplexmatm.R
import com.playplexmatm.activity.fragments.bills.AddNewSaleActivity

class SaleFragment : Fragment() {

    lateinit var addSale:Button
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_sale2, container, false)
        addSale = view.findViewById(R.id.addSale)
        addSale.setOnClickListener {
            startActivity(Intent(requireContext(),AddNewSaleActivity::class.java))
        }
        return view
    }
}