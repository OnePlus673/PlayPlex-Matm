package com.playplelx.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.playplelx.R
import com.playplelx.activity.pos.PosSettingActivity
import com.playplelx.model.posproducts.PosProductFilterModel
import com.playplelx.model.posproducts.ProductData
import java.text.DecimalFormat
import java.util.ArrayList

class PosProductAdapter(
    val mContext: PosSettingActivity,
    val productList: ArrayList<ProductData>,
    val onclick: onClick
) :
    RecyclerView.Adapter<PosProductAdapter.Myviewholder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PosProductAdapter.Myviewholder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adp_posproduct, parent, false)
        return Myviewholder(view)
    }

    override fun onBindViewHolder(holder: PosProductAdapter.Myviewholder, position: Int) {
        val productData = productList.get(position)
        Glide.with(mContext).load(productData.image_url).into(holder.ivImage)
        holder.tvName.text = productData.name.toLowerCase()
     //   holder.tvPrice.text = productData.single_unit_price.toString()
        holder.tvPrice.text =
            (DecimalFormat("##.#").format(productData.single_unit_price).toDouble()).toString()


        holder.tvQuantity.text = productData.quantity.toString()

        if (productData.isSelected) {
            holder.llMain.setBackgroundResource(R.drawable.rectangle_lightgree)
        } else {
            holder.llMain.setBackgroundResource(R.drawable.simplerectangle_white)

        }

        holder.itemView.setOnClickListener {

            onclick.onItemClick(productData, holder.tvQuantity, holder.tvPrice)
        }
    }

     fun updateList(mChatDetails:ArrayList<ProductData> ) {
        productList.addAll(mChatDetails)
    }
    override fun getItemCount(): Int {
        return productList.size
    }

    class Myviewholder(view: View) : RecyclerView.ViewHolder(view) {
        val llMain = view.findViewById(R.id.llMain) as LinearLayout
        val ivImage = view.findViewById(R.id.ivImage) as AppCompatImageView
        val tvName = view.findViewById(R.id.tvName) as AppCompatTextView
        val tvPrice = view.findViewById(R.id.tvPrice) as AppCompatTextView
        val tvQuantity = view.findViewById(R.id.tvQuantity) as AppCompatTextView
    }

    interface onClick {
        fun onItemClick(productData: ProductData, tvQuantity: TextView, tvPrice: TextView)
    }
}