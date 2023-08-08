package com.playplexmatm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.R
import com.playplexmatm.model.brand.BrandModel

class BrandListAdapter(
    val context: Context,
    val brandArrayList: ArrayList<BrandModel>,
    mListener: ListAdapterListener
) :
    RecyclerView.Adapter<BrandListAdapter.myViewholder>() {

    private val mListener: ListAdapterListener


    interface ListAdapterListener {
        fun onClickAtButton(brandModel: BrandModel)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): myViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_category, parent, false)
        return myViewholder(view)
    }

    override fun onBindViewHolder(holder: myViewholder, position: Int) {
        val brandModel = brandArrayList[position]
        holder.tvName.text = brandModel.name

        holder.itemView.setOnClickListener { mListener.onClickAtButton(brandModel) }
    }

    override fun getItemCount(): Int {
        return brandArrayList.size
    }


    class myViewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById(R.id.tvName) as TextView
    }

    init {
        this.mListener = mListener
    }
}