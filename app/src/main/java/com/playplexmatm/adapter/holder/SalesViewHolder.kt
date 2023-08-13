package com.playplexmatm.adapter.holder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.databinding.SalesItemBinding
import com.playplexmatm.model.bills.SaleBillRecord

class SalesViewHolder(val binding: SalesItemBinding, private val context: Context) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(saleBillRecord: SaleBillRecord) {
        binding.customerName.text = saleBillRecord.customer.name
        binding.saleBillNumber.text = "Payment in #${saleBillRecord.saleBillNumber}"
        binding.saleDate.text = saleBillRecord.date
        binding.saleAmount.text = saleBillRecord.amount
        binding.paymentMode.text = saleBillRecord.paymentType
    }
}