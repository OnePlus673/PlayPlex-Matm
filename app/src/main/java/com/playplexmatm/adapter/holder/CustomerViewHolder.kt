package com.playplexmatm.adapter.holder

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.databinding.CustomerItemBinding
import com.playplexmatm.extentions.beVisible
import com.playplexmatm.model.bills.Customer

class CustomerViewHolder(val binding: CustomerItemBinding, private val context: Context) :
    RecyclerView.ViewHolder(binding.root) {
    private var click: CustomerClick? = null
    fun onBind(customer: Customer,isLedger:Boolean) {
        binding.customerName.text = customer.name
        binding.customerPhone.text = customer.phone
        if (isLedger) {
            binding.amount.beVisible()
            if (customer.currentBalance.startsWith("get")) {
                binding.amount.text = "₹${customer.currentBalance.substringAfterLast("get")}"
                binding.amount.setTextColor(Color.RED)
            } else if (customer.currentBalance.startsWith("give")) {
                binding.amount.text = "₹${customer.currentBalance.substringAfterLast("give")}"
                binding.amount.setTextColor(Color.GREEN)
            } else {
                binding.amount.text = "₹${customer.currentBalance.substringAfterLast("give")}"
            }
            binding.customerPhone.text = customer.dateTime
        }
    }
    fun getClickListener(clickListeners: CustomerClick, customer: Customer) {
        this.click = clickListeners
        binding.root.setOnClickListener {
            click?.customerClick(customer)
        }
    }

}
