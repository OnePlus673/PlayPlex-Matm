package com.playplelx.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.playplelx.R
import com.playplelx.activity.PaymentInActivity
import com.playplelx.activity.PaymentOutActivity
import com.playplelx.model.paymentin.PaymentInModel
import com.playplelx.model.paymentout.PaymentOutModel

class PaymentOutAdapter(
    val mContext: PaymentOutActivity,
    val paymentList: ArrayList<PaymentOutModel>,
    val onclick: onClick
) :
    RecyclerView.Adapter<PaymentOutAdapter.myViewholder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PaymentOutAdapter.myViewholder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adp_paymentin, parent, false)
        return myViewholder(view)
    }

    override fun onBindViewHolder(holder: PaymentOutAdapter.myViewholder, position: Int) {
        val paymentOutModel = paymentList[position]
        holder.tvName.text = paymentOutModel.user.name
        holder.tvDate.text = paymentOutModel.date
        holder.tvAmount.text = "₹ " + paymentOutModel.amount
        holder.tvPaymentMode.text = "Payment Mode:- " + paymentOutModel.payment_mode.name

        holder.itemView.setOnClickListener {
            onclick.onItemClick(paymentOutModel, position)
        }
        holder.ivDelete.setOnClickListener {
            onclick.onDeleteClick(paymentOutModel, position)
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
        fun onItemClick(paymentOutModel: PaymentOutModel, position: Int)
        fun onDeleteClick(paymentOutModel: PaymentOutModel, position: Int)
    }
}