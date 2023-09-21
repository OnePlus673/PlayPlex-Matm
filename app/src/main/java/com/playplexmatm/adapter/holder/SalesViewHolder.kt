package com.playplexmatm.adapter.holder

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.R
import com.playplexmatm.activity.fragments.bills.BillReceiptActivity
import com.playplexmatm.databinding.SalesItemBinding
import com.playplexmatm.extentions.beGone
import com.playplexmatm.model.bills.SaleBillRecord

class SalesViewHolder(val binding: SalesItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(context: Context,saleBillRecord: SaleBillRecord) {
        binding.customerName.text = saleBillRecord.customer.name
        binding.saleBillNumber.text = saleBillRecord.saleBillNumber
        binding.saleDate.text = saleBillRecord.date
        binding.saleAmount.text = "₹${saleBillRecord.saleBillAmount}"
        binding.paymentMode.text = saleBillRecord.paymentType
        binding.print.setOnClickListener {
            val intent = Intent(context,BillReceiptActivity::class.java)
            intent.putExtra("customerName",saleBillRecord.customer.name)
            intent.putExtra("paymentType",saleBillRecord.paymentType)
            intent.putExtra("saleBillAmount",saleBillRecord.saleBillAmount)
            intent.putExtra("saleBillNumber",saleBillRecord.saleBillNumber)
            intent.putExtra("date",saleBillRecord.date)
            context.startActivity(intent)
        }
    }
}