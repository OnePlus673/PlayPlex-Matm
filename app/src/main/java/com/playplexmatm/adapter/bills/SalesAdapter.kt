package com.playplexmatm.adapter.bills

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.adapter.holder.SalesViewHolder
import com.playplexmatm.databinding.SalesItemBinding
import com.playplexmatm.model.bills.SaleBillRecord

class SalesAdapter(
    private val context: Context,
    private val data: List<SaleBillRecord>
) :
    RecyclerView.Adapter<SalesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesViewHolder =
        createViewHolder(parent)

    override fun onBindViewHolder(holder: SalesViewHolder, position: Int) {
        holder.onBind(data[position])
    }

    override fun getItemCount(): Int = data.size

    private fun createViewHolder(parent: ViewGroup?): SalesViewHolder {
        val binding =
            SalesItemBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return SalesViewHolder(binding, context)
    }

}