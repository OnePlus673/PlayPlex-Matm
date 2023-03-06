package com.playplelx.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.playplelx.R
import com.playplelx.activity.PaymentInActivity
import com.playplelx.model.paymentin.PaymentInModel

class PaymentInAdapter(
    val mContext: PaymentInActivity,
    val paymentList: ArrayList<PaymentInModel>,
    val onclick: onClick
) :
    RecyclerView.Adapter<PaymentInAdapter.myViewholder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PaymentInAdapter.myViewholder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adp_paymentin, parent, false)
        return myViewholder(view)
    }

    override fun onBindViewHolder(holder: PaymentInAdapter.myViewholder, position: Int) {
        val paymentInModel = paymentList[position]
        holder.tvName.text = paymentInModel.user.name
        holder.tvDate.text = paymentInModel.date
        holder.tvAmount.text = "₹ " + paymentInModel.amount
        holder.tvPaymentMode.text = "Payment Mode:- " + paymentInModel.payment_mode.name

        holder.itemView.setOnClickListener {
            onclick.onItemClick(paymentInModel, position)
        }
        holder.ivDelete.setOnClickListener {
            onclick.onDeleteClick(paymentInModel, position)
        }
    }

    override fun getItemCount(): Int {
        return paymentList.size
    }

    class myViewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById(R.id.tvName) as AppCompatTextView
        val tvDate = view.findViewById(R.id.tvDate) as AppCompatTextView
        val tvAmount = view.findViewById(R.id.tvAmount) as AppCompatTextView
        val tvPaymentMode = view.findViewById(R.id.tvPaymentMode) as AppCompatTextView
        val ivDelete = view.findViewById(R.id.ivDelete) as AppCompatImageView
    }

    interface onClick {
        fun onItemClick(paymentInModel: PaymentInModel, position: Int)
        fun onDeleteClick(paymentInModel: PaymentInModel, position: Int)
    }
}