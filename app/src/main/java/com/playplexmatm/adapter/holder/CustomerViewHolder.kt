package com.playplexmatm.adapter.holder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.databinding.CustomerItemBinding
import com.playplexmatm.model.bills.Customer

class CustomerViewHolder(val binding: CustomerItemBinding, private val context: Context) :
    RecyclerView.ViewHolder(binding.root) {
    private var click: CustomerClick? = null
    fun onBind(customer: Customer) {
        binding.customerName.text = customer.name
        binding.customerPhone.text = customer.phone
    }
    fun getClickListener(clickListeners: CustomerClick, customer: Customer) {
        this.click = clickListeners
        binding.root.setOnClickListener {
            click?.customerClick(customer)
        }
    }

}
