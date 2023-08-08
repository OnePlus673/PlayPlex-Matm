package com.playplexmatm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.playplexmatm.R
import com.playplexmatm.model.saleList.Items

class ProductPurchaseAdapter(
    val context: Context, val items: List<Items>
) :
    RecyclerView.Adapter<ProductPurchaseAdapter.myViewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_products, parent, false)
        return myViewholder(view)

    }


    override fun getItemCount(): Int {
        return items.size
    }

    class myViewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProductName = view.findViewById(R.id.tvProductName) as AppCompatTextView
        val tvPrice = view.findViewById(R.id.tvPrice) as AppCompatTextView
        val tvCurrentStock = view.findViewById(R.id.tvCurrentStock) as AppCompatTextView
        val tvDiscount = view.findViewById(R.id.tvDiscount) as AppCompatTextView
        val tvTax = view.findViewById(R.id.tvTax) as AppCompatTextView
        val tvSubtotal = view.findViewById(R.id.tvSubtotal) as AppCompatTextView
        val ivImage = view.findViewById(R.id.ivProductImage) as AppCompatImageView
    }

    override fun onBindViewHolder(holder: myViewholder, position: Int) {

        val itemModel = items[position]

        holder.tvProductName.setText(itemModel.product.name)

        holder.tvPrice.setText("Basic Amount: "+context.resources.getString(R.string.Rupee)+itemModel.single_unit_price)
        holder.tvCurrentStock.setText("Quantity: "+itemModel.quantity)
        holder.tvDiscount.setText("Discount: "+context.resources.getString(R.string.Rupee)+itemModel.total_discount)
        holder.tvTax.setText("Tax: "+context.resources.getString(R.string.Rupee)+itemModel.total_tax)
        holder.tvSubtotal.setText("SubTotal: "+context.resources.getString(R.string.Rupee)+itemModel.subtotal)

        Glide.with(context)
            .load(itemModel.product.image_url)
            .into(holder.ivImage)

    }


}