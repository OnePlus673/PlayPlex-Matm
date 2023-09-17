package com.playplexmatm.adapter.holder

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.R
import com.playplexmatm.databinding.SalesItemBinding
import com.playplexmatm.extentions.beGone
import com.playplexmatm.model.bills.SaleBillRecord

class SalesViewHolder(val binding: SalesItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(saleBillRecord: SaleBillRecord) {
        binding.customerName.text = saleBillRecord.customer.name
        binding.saleBillNumber.text = saleBillRecord.saleBillNumber
        binding.saleDate.text = saleBillRecord.date
        binding.saleAmount.text = "₹${saleBillRecord.saleBillAmount}"
        binding.paymentMode.text = saleBillRecord.paymentType
//        if (isLedger){
//            binding.customerIcon.setImageResource(R.drawable.ic_person1)
//            binding.saleAmount.text = "₹ ${saleBillRecord.customer.currentBalance}"
//            binding.saleAmount.setTextColor(Color.RED)
//            binding.saleBillNumber.beGone()
//            binding.paymentMode.beGone()
//        }
    }
}