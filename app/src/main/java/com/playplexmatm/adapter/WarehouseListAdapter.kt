package com.playplexmatm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.R
import com.playplexmatm.model.warehouse.WareHouseModel

class WarehouseListAdapter(
    val context: Context,
    var wareHouseArrayList: ArrayList<WareHouseModel>,
    mListener: ListAdapterListener
) :
    RecyclerView.Adapter<WarehouseListAdapter.myViewholder>() {

    private val mListener: ListAdapterListener


    interface ListAdapterListener {
        fun onClickAtButton(wareHouseModel: WareHouseModel)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): myViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_category, parent, false)
        return myViewholder(view)
    }

    override fun onBindViewHolder(holder: myViewholder, position: Int) {
        val categoryModel = wareHouseArrayList[position]
        holder.tvName.text = categoryModel.name

        holder.itemView.setOnClickListener { mListener.onClickAtButton(categoryModel) }
    }

    override fun getItemCount(): Int {
        return wareHouseArrayList.size
    }

    init {
        this.mListener = mListener
    }

    class myViewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById(R.id.tvName) as TextView
    }


    fun updateList( list: List<WareHouseModel>) {
        wareHouseArrayList = list as ArrayList<WareHouseModel>
        notifyDataSetChanged()
    }

}