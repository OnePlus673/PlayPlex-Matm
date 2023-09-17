package com.playplexmatm.adapter.holder

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.databinding.LedgerItemBinding
import com.playplexmatm.extentions.beInvisible
import com.playplexmatm.extentions.extractNumber
import com.playplexmatm.model.bills.SaleBillRecord

class LedgerViewHolder (val binding: LedgerItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun onBind(saleBillRecord: SaleBillRecord) {
        binding.dateTime.text = saleBillRecord.date
        if (saleBillRecord.saleBillNumber.startsWith("Sale Bill")) {
            if (saleBillRecord.balanceDue.startsWith("give")) {
                binding.currentBalance.text = "₹${saleBillRecord.balanceDue.extractNumber()}"
                binding.gaveBalance.text = "₹${saleBillRecord.balanceDue.extractNumber()}"
                binding.gotBalance.beInvisible()
            }
        } else {
            binding.currentBalance.text = "₹${saleBillRecord.saleBillAmount}"
            binding.gotBalance.text = "₹${saleBillRecord.saleBillAmount}"
            binding.gaveBalance.beInvisible()
        }
        binding.mode.text = saleBillRecord.saleBillNumber.substringBeforeLast("#")
//        binding.gaveBalance.text = "₹${saleBillRecord.saleBillAmount}"


    }
}