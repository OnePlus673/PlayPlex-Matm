package com.playplelx.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.playplelx.R
import com.playplelx.activity.PurchaseListActivity
import com.playplelx.activity.saleList.SaleListActivity
import com.playplelx.model.purchasemodel.PurchaseModel
import com.playplelx.model.saleList.SaleListModel

class PurchaseAdapter(
    val mContext: PurchaseListActivity, val purchaseList: ArrayList<PurchaseModel>,
    val onclick: onClick
) : RecyclerView.Adapter<PurchaseAdapter.Myviewholder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PurchaseAdapter.Myviewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adp_salelist, parent, false)
        return Myviewholder(view)
    }

    override fun onBindViewHolder(holder: PurchaseAdapter.Myviewholder, position: Int) {
        val purchaseModel = purchaseList[position]
        holder.tvCustomerName.text = purchaseModel.user.name
        holder.tvInvoiceNumber.text = purchaseModel.invoice_number
        holder.tvAmount.text = "Amount:- " + "₹" + purchaseModel.total.toString()
        holder.tvStatus.text = "Status:- " + purchaseModel.payment_status

        holder.ivDelete.setOnClickListener {
            onclick.onDeleteClick(purchaseModel,position)
        }
    }

    override fun getItemCount(): Int {
        return purchaseList.size
    }

    class Myviewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCustomerName = view.findViewById(R.id.tvCustomerName) as AppCompatTextView
        val tvInvoiceNumber = view.findViewById(R.id.tvInvoiceNumber) as AppCompatTextView
        val tvAmount = view.findViewById(R.id.tvAmount) as AppCompatTextView
        val tvStatus = view.findViewById(R.id.tvStatus) as AppCompatTextView
        val ivDelete = view.findViewById(R.id.ivDelete) as AppCompatImageView
    }

    interface onClick {
        fun onDeleteClick(purchaseModel: PurchaseModel, position: Int)
    }

}