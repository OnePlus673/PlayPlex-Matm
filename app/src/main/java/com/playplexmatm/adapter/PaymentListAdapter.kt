package com.playplexmatm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.R
import com.playplexmatm.activity.pos.PaymentModeActivity
import com.playplexmatm.model.pos.PaymentModel

class PaymentListAdapter(
    val mContext: PaymentModeActivity,
    val paymentFilterList: ArrayList<PaymentModel>
) : RecyclerView.Adapter<PaymentListAdapter.MyviewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyviewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adp_payment_filter, parent, false)
        return MyviewHolder(view)
    }

    override fun getItemCount(): Int {
        return paymentFilterList.size
    }

    override fun onBindViewHolder(holder: MyviewHolder, position: Int) {
        val paymentModel = paymentFilterList.get(position)
        holder.tvName.text = "Name:- " + paymentModel.payment_mode_name
        holder.tvRate.text = "Amount:- " + paymentModel.amount
    }

    class MyviewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById(R.id.tvName) as AppCompatTextView
        val tvRate = view.findViewById(R.id.tvRate) as AppCompatTextView
    }
}