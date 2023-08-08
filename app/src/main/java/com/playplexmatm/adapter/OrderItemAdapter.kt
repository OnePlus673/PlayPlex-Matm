package com.playplexmatm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.playplexmatm.R
import com.playplexmatm.activity.onlineorder.OrderDetailActivity
import com.playplexmatm.model.onlineorder.Items

class OrderItemAdapter(val mContext: OrderDetailActivity, val items: ArrayList<Items>) :
    RecyclerView.Adapter<OrderItemAdapter.Myviewholder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Myviewholder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adp_itemorder, parent, false)
        return Myviewholder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: Myviewholder, position: Int) {
        val items = items[position]
        holder.tvProductName.text = items.product.name
        holder.tvUnitPrice.text = "Unit price:- " + items.unit_price.toString()
        holder.tvQuantity.text = "Quantity:- " + items.quantity.toString()
        holder.tvTotal.text = "Total:- " + items.subtotal.toString()

        if (!items.product.image_url.isEmpty()) {
            Glide.with(mContext).load(items.product.image_url).into(holder.ivImage)
        }
    }

    class Myviewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProductName = view.findViewById(R.id.tvProductName) as TextView
        val tvUnitPrice = view.findViewById(R.id.tvUnitPrice) as TextView
        val tvQuantity = view.findViewById(R.id.tvQuantity) as TextView
        val tvTotal = view.findViewById(R.id.tvTotal) as TextView
        val ivImage = view.findViewById(R.id.ivImage) as AppCompatImageView
    }
}