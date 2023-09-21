package com.playplexmatm.adapter.bills

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.adapter.holder.LedgerViewHolder
import com.playplexmatm.adapter.holder.SalesViewHolder
import com.playplexmatm.databinding.LedgerItemBinding
import com.playplexmatm.databinding.SalesItemBinding
import com.playplexmatm.model.bills.Customer
import com.playplexmatm.model.bills.SaleBillRecord

class SalesAdapter(
    private val context: Context,
    private var data: List<SaleBillRecord>,
    private val viewIndex:Int
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewIndex) {
            0->{SalesViewHolder(getViewSale(parent))}
            1->{LedgerViewHolder(getViewLedger(parent))}
            else -> {SalesViewHolder(getViewSale(parent))}
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return when (viewIndex) {
            0 -> {
                (holder as SalesViewHolder).onBind(context,data[position])
            }
            1 -> {
                (holder as LedgerViewHolder).onBind(data[position])
            }

            else -> {}
        }

    }

    override fun getItemCount(): Int = data.size
    private fun getViewSale(parent: ViewGroup?): SalesItemBinding {
        return SalesItemBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
    }
    private fun getViewLedger(parent: ViewGroup?): LedgerItemBinding {
        return LedgerItemBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
    }
    fun updateList(files: ArrayList<SaleBillRecord>) {
        data = files
        notifyDataSetChanged()
    }

}