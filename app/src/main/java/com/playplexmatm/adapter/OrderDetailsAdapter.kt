package com.playplexmatm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.R
import com.playplexmatm.activity.onlineorder.OnlineOrderActivity
import com.playplexmatm.model.onlineorder.OrderResponse

class OrderDetailsAdapter(
    val mContext: OnlineOrderActivity,
    val orderList: ArrayList<OrderResponse>,
    val onitemclick: onItemclick
) : RecyclerView.Adapter<OrderDetailsAdapter.Myviewholder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Myviewholder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adp_orderdetail, parent, false)
        return Myviewholder(view)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: Myviewholder, position: Int) {
        val orderModel=orderList.get(position)
        holder.tvInvoiceNumber.setText("Invoice Number:- " + orderModel.invoice_number)
        holder.tvAmount.setText("Amount:- "+orderModel.subtotal.toString())
        holder.tvViewOrder.setOnClickListener {
            onitemclick.onClick(orderModel,position)
        }
    }

    class Myviewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvInvoiceNumber = view.findViewById(R.id.tvInvoiceNumber) as TextView
        val tvAmount = view.findViewById(R.id.tvAmount) as TextView
        val tvViewOrder = view.findViewById(R.id.tvVieworder) as TextView
    }

    interface onItemclick {
        fun onClick(orderResponse: OrderResponse, position: Int)
    }

}