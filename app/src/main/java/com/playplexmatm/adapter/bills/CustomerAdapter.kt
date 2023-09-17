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
    private var customerList: List<Customer>,
    private val click: CustomerClick,
    private val isLedger: Boolean? = false
) :
    RecyclerView.Adapter<CustomerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder =
        createViewHolder(parent)

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        holder.onBind(customerList[position],isLedger?:false)
        holder.getClickListener(click, customerList[position])
    }

    override fun getItemCount(): Int = customerList.size

    private fun createViewHolder(parent: ViewGroup?): CustomerViewHolder {
        val binding =
            CustomerItemBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return CustomerViewHolder(binding, context)
    }
    fun updateList(filterList: ArrayList<Customer>) {
        customerList = filterList
        notifyDataSetChanged()
    }

}
