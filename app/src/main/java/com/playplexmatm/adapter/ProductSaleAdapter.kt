package com.playplexmatm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.R
import com.playplexmatm.model.saleList.Items

class ProductSaleAdapter (
    val context: Context, val items: List<Items>
) :
    RecyclerView.Adapter<ProductSaleAdapter.myViewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_products, parent, false)
        return ProductSaleAdapter.myViewholder(view)

    }


    override fun getItemCount(): Int {
        return items.size
    }

    class myViewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProductName = view.findViewById(R.id.tvProductName) as AppCompatTextView
//        val tvSalePrice = view.findViewById(R.id.tvSalePrice) as AppCompatTextView
//        val tvCurrentStock = view.findViewById(R.id.tvCurrentStock) as AppCompatTextView
        val ivImage = view.findViewById(R.id.ivProductImage) as AppCompatImageView
    }

    override fun onBindViewHolder(holder: myViewholder, position: Int) {

//        val itemModel = purchaseList[position]
//
//        holder.tvProductName.setText()

//        holder.tvInvoiceNumber.text = purchaseModel.invoice_number
//        holder.tvAmount.text = "Amount:- " + "₹" + purchaseModel.total.toString()
//        holder.tvStatus.text = "Status:- " + purchaseModel.payment_status


    }


}