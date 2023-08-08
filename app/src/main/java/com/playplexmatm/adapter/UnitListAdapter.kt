package com.playplexmatm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.R
import com.playplexmatm.model.units.UnitModel

class UnitListAdapter(
    val context: Context,
    val unitArrayList: ArrayList<UnitModel>,
    mListener: ListAdapterListener

) :
    RecyclerView.Adapter<UnitListAdapter.myViewholder>() {

    private val mListener: ListAdapterListener


    interface ListAdapterListener {
        fun onClickAtButton(unitModel: UnitModel)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): myViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_category, parent, false)
        return myViewholder(view)
    }



    override fun onBindViewHolder(holder: myViewholder, position: Int) {
        val brandModel = unitArrayList[position]
        holder.tvName.text = brandModel.name

        holder.itemView.setOnClickListener { mListener.onClickAtButton(brandModel) }
    }

    override fun getItemCount(): Int {
        return unitArrayList.size
    }

    interface onClick {
        fun onItemClick(brandModel: UnitModel, position: Int)
    }

    class myViewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById(R.id.tvName) as TextView
    }

    init {
        this.mListener = mListener
    }
}