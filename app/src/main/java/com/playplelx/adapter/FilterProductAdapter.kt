package com.playplelx.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.playplelx.R
import com.playplelx.activity.pos.saleList.AddEditSaleListActivity
import com.playplelx.model.product.ProductModel
import com.playplelx.model.productfilter.ProductFilterModel

class FilterProductAdapter(
    val mContext: Context,
    val productName: String,
    val productImage: String,
    val filterProductList: ArrayList<ProductFilterModel>,
    val onclick: onClick
) : RecyclerView.Adapter<FilterProductAdapter.Myviewholder>() {

    var count: Int = 1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilterProductAdapter.Myviewholder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adp_prodctfilter, parent, false)
        return Myviewholder(view)
    }

    override fun onBindViewHolder(holder: FilterProductAdapter.Myviewholder, position: Int) {
        Glide.with(mContext).load(filterProductList[position].image_url).into(holder.ivImage)
        holder.tvName.text = filterProductList[position].name
        holder.tvQuantity.setText(filterProductList[position].quantity.toString())

        holder.tvAdd.setOnClickListener {
            onclick.onAddClick(
                filterProductList[position],
                position,
                holder.tvQuantity,
                holder.tvSubTotal
            )

            /*    if (count <= filterProductList[position].quantity) {
                    holder.tvQuantity.text = count.toString()
                } else {
                    Toast.makeText(mContext, "item stock is finished", Toast.LENGTH_SHORT).show()
                }*/
        }
        holder.tvMinus.setOnClickListener {
            onclick.onMinusClick(
                filterProductList[position],
                position,
                holder.tvQuantity,
                holder.tvSubTotal
            )
            /* if (holder.tvQuantity.text.toString().toInt() > 1) {
                 count--
                 holder.tvQuantity.text = count.toString()

             }*/
        }

        holder.ivDelete.setOnClickListener {
            onclick.onDeleteClick(filterProductList[position], position)
        }

        holder.tvSubTotal.text = filterProductList[position].single_unit_price.toString()
    }

    override fun getItemCount(): Int {
        return filterProductList.size
    }

    class Myviewholder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage = view.findViewById(R.id.ivImage) as AppCompatImageView
        val tvName = view.findViewById(R.id.tvName) as AppCompatTextView
        val tvAdd = view.findViewById(R.id.tvAdd) as AppCompatTextView
        val tvQuantity = view.findViewById(R.id.tvQuantity) as AppCompatTextView
        val tvMinus = view.findViewById(R.id.tvMinus) as AppCompatTextView
        val tvSubTotal = view.findViewById(R.id.tvSubTotal) as AppCompatTextView
        val ivDelete = view.findViewById(R.id.ivDelete) as AppCompatImageView
    }

    interface onClick {
        fun onAddClick(
            filterModel: ProductFilterModel,
            position: Int,
            tvQuantity: TextView,
            tvSubTotal: TextView
        )

        fun onMinusClick(
            filterModel: ProductFilterModel,
            position: Int,
            tvQuantity: TextView,
            tvSubTotal: TextView
        )

        fun onDeleteClick(filterModel: ProductFilterModel, position: Int)
    }
}