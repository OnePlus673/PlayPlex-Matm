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
import com.playplexmatm.model.product.ProductModel

class ProductAdapter(
    val context: Context, val productArrayList: ArrayList<ProductModel>,
    val onclick: onClick
) :
    RecyclerView.Adapter<ProductAdapter.myViewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdapter.myViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adp_products, parent, false)
        return myViewholder(view)

    }

    override fun onBindViewHolder(holder: ProductAdapter.myViewholder, position: Int) {
        val productModel = productArrayList[position]
        holder.tvProductName.text = productModel.name
        holder.tvSalePrice.text =
            "Sale Price:- " + productModel.details.sales_price + "(Purchase Price:- " + productModel.details.purchase_price + ")"
        holder.tvCurrentStock.text = "Current Stock:- " + productModel.details.current_stock

        if (productModel.image_url.isNotEmpty()) {
            Glide.with(context).load(productModel.image_url).into(holder.ivImage)
        }

        holder.itemView.setOnClickListener {
            onclick.onItemClick(productModel,position)
        }
        holder.ivDelete.setOnClickListener {
            onclick.onDeleteClick(productModel,position)
        }
    }

    override fun getItemCount(): Int {
        return productArrayList.size
    }

    class myViewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProductName = view.findViewById(R.id.tvProductName) as AppCompatTextView
        val tvSalePrice = view.findViewById(R.id.tvSalePrice) as AppCompatTextView
        val tvCurrentStock = view.findViewById(R.id.tvCurrentStock) as AppCompatTextView
        val ivImage = view.findViewById(R.id.ivImage) as AppCompatImageView
        val ivDelete = view.findViewById(R.id.ivDelete) as AppCompatImageView
    }

    interface onClick {
        fun onItemClick(productModel: ProductModel, position: Int)
        fun onDeleteClick(productModel: ProductModel, position: Int)
    }
}