package com.playplexmatm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.R
import com.playplexmatm.activity.settings.PaymentModeSettingsActivity
import com.playplexmatm.model.paymentmode.PaymentModeModel

class PaymentModeAdapter(
    val mContext: PaymentModeSettingsActivity,
    val paymentModeArrayList: ArrayList<PaymentModeModel>,
    val onclick: onClick
) :
    RecyclerView.Adapter<PaymentModeAdapter.Myviewholder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PaymentModeAdapter.Myviewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adp_taxes, parent, false)
        return Myviewholder(view)

    }

    override fun onBindViewHolder(holder: PaymentModeAdapter.Myviewholder, position: Int) {
        val paymentModeModel = paymentModeArrayList[position]
        holder.tvName.text = "Name:- " + paymentModeModel.name
        holder.tvRate.text = "Mode Type:- " + paymentModeModel.mode_type

        holder.itemView.setOnClickListener {
            onclick.onItemClick(paymentModeModel, position)
        }

        holder.ivDelete.setOnClickListener {
            onclick.onDeleteClick(paymentModeModel, position)
        }
    }

    override fun getItemCount(): Int {

        return paymentModeArrayList.size
    }

    class Myviewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById(R.id.tvName) as AppCompatTextView
        val tvRate = view.findViewById(R.id.tvRate) as AppCompatTextView
        val ivDelete = view.findViewById(R.id.ivDelete) as AppCompatImageView
    }

    interface onClick {
        fun onItemClick(paymentModeModel: PaymentModeModel, position: Int)
        fun onDeleteClick(paymentModeModel: PaymentModeModel, position: Int)
    }
}