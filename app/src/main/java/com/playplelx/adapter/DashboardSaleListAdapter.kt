package com.playplelx.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.playplelx.R
import com.playplelx.activity.saleList.SaleListActivity
import com.playplelx.model.saleList.SaleListModel

class DashboardSaleListAdapter(
    val mContext: Context, val saleList: ArrayList<SaleListModel>
) : RecyclerView.Adapter<DashboardSaleListAdapter.Myviewholder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DashboardSaleListAdapter.Myviewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adp_salelist, parent, false)
        return Myviewholder(view)
    }

    override fun onBindViewHolder(holder: DashboardSaleListAdapter.Myviewholder, position: Int) {
        val saleListModel = saleList[position]
        holder.tvCustomerName.text = saleListModel.user.name
        holder.tvInvoiceNumber.text = saleListModel.invoice_number
        holder.tvAmount.text = "Amount:- " + "₹" + saleListModel.total.toString()
        holder.tvStatus.text = "Status:- " + saleListModel.payment_status

        holder.ivDelete.visibility = View.GONE

    }

    override fun getItemCount(): Int {
        return saleList.size
    }

    class Myviewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCustomerName = view.findViewById(R.id.tvCustomerName) as AppCompatTextView
        val tvInvoiceNumber = view.findViewById(R.id.tvInvoiceNumber) as AppCompatTextView
        val tvAmount = view.findViewById(R.id.tvAmount) as AppCompatTextView
        val tvStatus = view.findViewById(R.id.tvStatus) as AppCompatTextView
        val ivDelete = view.findViewById(R.id.ivDelete) as AppCompatImageView
    }

}