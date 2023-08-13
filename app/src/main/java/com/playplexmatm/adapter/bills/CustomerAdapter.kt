package com.playplexmatm.adapter.bills

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.adapter.holder.CustomerClick
import com.playplexmatm.adapter.holder.CustomerViewHolder
import com.playplexmatm.databinding.CustomerItemBinding
import com.playplexmatm.model.bills.Customer

class CustomerAdapter(
    private val context: Context,
    private val data: List<Customer>,
    private val click: CustomerClick
) :
    RecyclerView.Adapter<CustomerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder =
        createViewHolder(parent)

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        holder.onBind(data[position])
        holder.getClickListener(click,data[position])
    }

    override fun getItemCount(): Int = data.size

    private fun createViewHolder(parent: ViewGroup?): CustomerViewHolder {
        val binding =
            CustomerItemBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return CustomerViewHolder(binding, context)
    }

}
