package com.playplexmatm.adapter.holder

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.R
import com.playplexmatm.databinding.OptionsLayoutBinding
import com.playplexmatm.model.bills.OptionModel

class PaymentOptionsVH(val binding: OptionsLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private var click: OptionClick? = null
    fun onBind(optionModel: OptionModel) {
        binding.optionIcon.setImageResource(optionModel.icon)
        binding.optionTitle.text = optionModel.title
        if (optionModel.isSelected == true) {
            binding.optionIcon.setBackgroundResource(R.drawable.ripple_circle_selected)
            binding.optionIcon.setColorFilter(Color.GREEN)
        } else {
            binding.optionIcon.setBackgroundResource(R.drawable.ripple_circle)
            binding.optionIcon.setColorFilter(Color.WHITE)
        }
    }

    fun getClickListener(clickListeners: OptionClick, position: Int) {
        this.click = clickListeners
        binding.root.setOnClickListener {
            click!!.optionClick(position)
        }
    }
}
