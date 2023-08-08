package com.playplexmatm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.R
import com.playplexmatm.model.saleList.OrderPayments

class PaymentsPurchaseAdapter (
    val context: Context, val items: List<OrderPayments>,
) :
    RecyclerView.Adapter<PaymentsPurchaseAdapter.myViewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_payments, parent, false)
        return PaymentsPurchaseAdapter.myViewholder(view)

    }


    override fun getItemCount(): Int {
        return items.size
    }

    class myViewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAmount = view.findViewById(R.id.tvAmount) as AppCompatTextView
        val tvPaymentMode = view.findViewById(R.id.tvPaymentMode) as AppCompatTextView
        val tvDate = view.findViewById(R.id.tvDate) as AppCompatTextView
    }

    override fun onBindViewHolder(holder: myViewholder, position: Int) {

        val itemModel = items[position]

        holder.tvAmount.setText("Amount: "+context.resources.getString(R.string.Rupee)+" "+itemModel.amount)
        holder.tvDate.setText("Date: "+itemModel.payment.date)
        holder.tvPaymentMode.setText("Payment Mode: "+itemModel.payment.payment_mode.name)
    }

}