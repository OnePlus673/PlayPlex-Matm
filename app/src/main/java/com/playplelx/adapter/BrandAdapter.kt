package com.playplelx.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.playplelx.R
import com.playplelx.model.brand.BrandModel

class BrandAdapter(
    val context: Context,
    val brandArrayList: ArrayList<BrandModel>,
    val onclick: onClick
) :
    RecyclerView.Adapter<BrandAdapter.myViewholder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): myViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adp_category, parent, false)
        return myViewholder(view)
    }

    override fun onBindViewHolder(holder: myViewholder, position: Int) {
        val brandModel: BrandModel = brandArrayList[position]
        holder.tvCategoryName.text = brandModel.name
        if (brandModel.image_url.isNotEmpty()) {
            Glide.with(context).load(brandModel.image_url).into(holder.ivCategoryImage)
        }

        holder.itemView.setOnClickListener {
            onclick.onItemClick(brandModel, position)
        }
        holder.ivDelete.setOnClickListener {
            onclick.onDeleteClick(brandModel, position)

        }
    }

    override fun getItemCount(): Int {
        return brandArrayList.size
    }

    interface onClick {
        fun onItemClick(brandModel: BrandModel, position: Int)
        fun onDeleteClick(brandModel: BrandModel, position: Int)
    }

    class myViewholder(view: View) : RecyclerView.ViewHolder(view) {
        val ivCategoryImage = view.findViewById(R.id.ivCategoryImage) as AppCompatImageView
        val tvCategoryName = view.findViewById(R.id.tvCategoryName) as AppCompatTextView
        val ivDelete = view.findViewById(R.id.ivDelete) as AppCompatImageView
    }
}