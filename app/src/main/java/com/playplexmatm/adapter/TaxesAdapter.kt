package com.playplexmatm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.R
import com.playplexmatm.activity.settings.TaxesSettingsActivity
import com.playplexmatm.model.taxes.TaxesModel

class TaxesAdapter(
    val mContext: TaxesSettingsActivity, val taxArrayList: ArrayList<TaxesModel>,
    val onclick: onClick
) :
    RecyclerView.Adapter<TaxesAdapter.Myviewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaxesAdapter.Myviewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adp_taxes, parent, false)
        return Myviewholder(view)

    }

    override fun onBindViewHolder(holder: TaxesAdapter.Myviewholder, position: Int) {
        val taxesModel = taxArrayList[position]
        holder.tvName.text = "Name:- " + taxesModel.name
        holder.tvRate.text = "Rate:- " + taxesModel.rate

        holder.itemView.setOnClickListener {
            onclick.onItemClick(taxesModel, position)
        }

        holder.ivDelete.setOnClickListener {
            onclick.onDeleteClick(taxesModel,position)
        }
    }

    override fun getItemCount(): Int {

        return taxArrayList.size
    }

    class Myviewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById(R.id.tvName) as AppCompatTextView
        val tvRate = view.findViewById(R.id.tvRate) as AppCompatTextView
        val ivDelete = view.findViewById(R.id.ivDelete) as AppCompatImageView
    }

    interface onClick {
        fun onItemClick(taxesModel: TaxesModel, position: Int)
        fun onDeleteClick(taxesModel: TaxesModel, position: Int)
    }
}