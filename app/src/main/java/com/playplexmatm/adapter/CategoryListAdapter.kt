package com.playplexmatm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.R
import com.playplexmatm.model.category.CategoryModel

class CategoryListAdapter(
    val context: Context,
    val categoryArrayList: ArrayList<CategoryModel>,
    mListener: ListAdapterListener

) : RecyclerView.Adapter<CategoryListAdapter.myViewholder>() {
    private val mListener: ListAdapterListener


    interface ListAdapterListener {
        fun onClickAtButton(categoryModel: CategoryModel)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): myViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_category, parent, false)
        return myViewholder(view)
    }

    override fun onBindViewHolder(holder: myViewholder, position: Int) {
        val categoryModel = categoryArrayList[position]
        holder.tvName.text = categoryModel.name

        holder.itemView.setOnClickListener { mListener.onClickAtButton(categoryModel) }
    }

    override fun getItemCount(): Int {
        return categoryArrayList.size
    }

    class myViewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById(R.id.tvName) as TextView
    }

    init {
        this.mListener = mListener // receive mListener from Fragment (or Activity)

    }
}