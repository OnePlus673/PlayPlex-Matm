package com.playplexmatm.adapter.bills

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.adapter.holder.OptionClick
import com.playplexmatm.adapter.holder.PaymentOptionsVH
import com.playplexmatm.databinding.OptionsLayoutBinding
import com.playplexmatm.model.bills.OptionModel

class PaymentOptionsAdapter(
    private val context: Context,
    private val data: ArrayList<OptionModel>,
    private val clickListener: OptionClick
) : RecyclerView.Adapter<PaymentOptionsVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentOptionsVH =
        createViewHolder(parent)

    override fun onBindViewHolder(holder: PaymentOptionsVH, position: Int) {
        holder.onBind(data[position])
        holder.getClickListener(clickListener, position)
    }

    override fun getItemCount(): Int = data.size

    private fun createViewHolder(parent: ViewGroup?): PaymentOptionsVH {
        val binding =
            OptionsLayoutBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return PaymentOptionsVH(binding)
    }
}
