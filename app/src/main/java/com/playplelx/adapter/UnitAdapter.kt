package com.playplelx.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.playplelx.R
import com.playplelx.activity.settings.UnitSettingActivity
import com.playplelx.model.units.UnitModel

class UnitAdapter(
    val mContext: UnitSettingActivity, val unitArrayList: ArrayList<UnitModel>,
    val onclick: onClick
) :
    RecyclerView.Adapter<UnitAdapter.Myviewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitAdapter.Myviewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adp_taxes, parent, false)
        return Myviewholder(view)

    }

    override fun onBindViewHolder(holder: UnitAdapter.Myviewholder, position: Int) {
        val taxesModel = unitArrayList[position]
        holder.tvName.text = "Name:- " + taxesModel.name
        holder.tvRate.text = "Short Name:- " + taxesModel.short_name

        holder.itemView.setOnClickListener {
            onclick.onItemClick(taxesModel, position)
        }

        holder.ivDelete.setOnClickListener {
            onclick.onDeleteClick(taxesModel,position)
        }
    }

    override fun getItemCount(): Int {

        return unitArrayList.size
    }

    class Myviewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById(R.id.tvName) as AppCompatTextView
        val tvRate = view.findViewById(R.id.tvRate) as AppCompatTextView
        val ivDelete = view.findViewById(R.id.ivDelete) as AppCompatImageView
    }

    interface onClick {
        fun onItemClick(unitModel: UnitModel, position: Int)
        fun onDeleteClick(unitModel: UnitModel, position: Int)
    }
}