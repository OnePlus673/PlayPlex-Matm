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
import com.playplelx.model.category.CategoryModel

class CategoryAdapter(
    val context: Context,
    val categoryArrayList: ArrayList<CategoryModel>,
    val onclick: onClick
) :
    RecyclerView.Adapter<CategoryAdapter.myViewholder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): myViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adp_category, parent, false)
        return myViewholder(view)
    }

    override fun onBindViewHolder(holder: myViewholder, position: Int) {
        val categoryModel = categoryArrayList[position]
        holder.tvCategoryName.text = categoryModel.name
        if (categoryModel.image_url.isNotEmpty()) {
            Glide.with(context).load(categoryModel.image_url).into(holder.ivCategoryImage)
        }

        holder.itemView.setOnClickListener {
            onclick.onItemClick(categoryModel, position)
        }
        holder.ivDelete.setOnClickListener {
            onclick.onDeleteClick(categoryModel,position)
        }
    }

    override fun getItemCount(): Int {
        return categoryArrayList.size
    }

    interface onClick {
        fun onItemClick(categoryModel: CategoryModel, position: Int)
        fun onDeleteClick(categoryModel: CategoryModel,position: Int)
    }

    class myViewholder(view: View) : RecyclerView.ViewHolder(view) {
        val ivCategoryImage = view.findViewById(R.id.ivCategoryImage) as AppCompatImageView
        val tvCategoryName = view.findViewById(R.id.tvCategoryName) as AppCompatTextView
        val ivDelete=view.findViewById(R.id.ivDelete) as AppCompatImageView
    }


}