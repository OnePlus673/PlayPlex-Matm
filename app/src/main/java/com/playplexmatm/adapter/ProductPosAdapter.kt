package com.playplexmatm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.playplexmatm.R
import com.playplexmatm.activity.pos.PosSettingActivity
import com.playplexmatm.model.categoryofproducts.Items
import com.playplexmatm.model.posproducts.ProductData
import com.playplexmatm.util.DatabaseHelper
import java.text.DecimalFormat
import kotlin.collections.ArrayList

class ProductPosAdapter(
    val mContext: PosSettingActivity, var productList: ArrayList<ProductData>, val onclick: onClick
) : RecyclerView.Adapter<ProductPosAdapter.Myviewholder>() {

    private var databaseHelper: DatabaseHelper = DatabaseHelper(mContext)
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ProductPosAdapter.Myviewholder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adp_posproduct, parent, false)
        return Myviewholder(view)
    }

    fun hell() {

    }

    fun updateList(list: List<Items>) {
        productList = list as ArrayList<ProductData>
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ProductPosAdapter.Myviewholder, position: Int) {
//        val productData = productList[position]
        val productData: ProductData =
            productList[position]
//        val productData = productList.get(position)
        Glide.with(mContext).load(productData.image_url).into(holder.ivImage)
        holder.tvName.text = productData.name.toLowerCase()
        holder.tvPrice.text =
            (DecimalFormat("##.#").format(productData.single_unit_price).toDouble()).toString()

        holder.tvSubTotal.text = (DecimalFormat("##.#").format(
            databaseHelper.CheckItemsTotal(
                productData.xid, productData.x_unit_id, productData.single_unit_price, productData.quantity
            )
        ).toDouble()).toString()

//        if (productData.isSelected) {
//            holder.llMain.setBackgroundResource(R.drawable.rectangle_lightgree)
//        } else {
//            holder.llMain.setBackgroundResource(R.drawable.simplerectangle_white)
//
//        }

        holder.quantityStock.setText("Stock:- "+productData.stock_quantity)


        holder.tvQuantity.text =
            databaseHelper.CheckOrderExists(productData.xid, productData.x_unit_id).toString()

        holder.itemView.setOnClickListener {
            onclick.onItemClick(productData, holder.tvQuantity, holder.tvPrice)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class Myviewholder(view: View) : RecyclerView.ViewHolder(view) {
        val llMain = view.findViewById(R.id.llMain) as CardView
        val ivImage = view.findViewById(R.id.ivImage) as AppCompatImageView
        val tvName = view.findViewById(R.id.tvName) as AppCompatTextView
        val tvPrice = view.findViewById(R.id.tvPrice) as AppCompatTextView
        val tvQuantity = view.findViewById(R.id.tvQuantity) as AppCompatTextView
        val tvSubTotal = view.findViewById(R.id.tvSubTotal) as AppCompatTextView
        val quantityStock = view.findViewById(R.id.quantityStock) as AppCompatTextView
    }

    interface onClick {
        fun onItemClick(productData: ProductData, tvQuantity: TextView, tvPrice: TextView)
    }


}