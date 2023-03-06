package com.playplelx.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.playplelx.R
import com.playplelx.activity.settings.TaxesSettingsActivity
import com.playplelx.activity.settings.WareHouseSettingsActivity
import com.playplelx.model.taxes.TaxesModel
import com.playplelx.model.warehouse.WareHouseModel

class WareHouseAdapter(
    val mContext: WareHouseSettingsActivity, val wareHosueArrayList: ArrayList<WareHouseModel>,
    val onclick: onClick
) :
    RecyclerView.Adapter<WareHouseAdapter.Myviewholder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WareHouseAdapter.Myviewholder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adp_warehouse, parent, false)
        return Myviewholder(view)

    }

    override fun onBindViewHolder(holder: WareHouseAdapter.Myviewholder, position: Int) {
        val wareHouseModel = wareHosueArrayList[position]
        holder.tvName.text = "Name:- " + wareHouseModel.name
        holder.tvEmail.text = "Email:- " + wareHouseModel.email
        holder.tvPhone.text = "Phone Number:- " + wareHouseModel.phone
        holder.tvTerms.text = "Terms:- " + wareHouseModel.terms_condition

        if (!wareHouseModel.logo_url.isNullOrEmpty()){
            Glide.with(mContext).load(wareHouseModel.logo_url).into(holder.ivImage)
        }

        holder.itemView.setOnClickListener {
            onclick.onItemClick(wareHouseModel, position)
        }

        holder.ivDelete.setOnClickListener {
            onclick.onDeleteClick(wareHouseModel, position)
        }
    }

    override fun getItemCount(): Int {

        return wareHosueArrayList.size
    }

    class Myviewholder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage = view.findViewById(R.id.ivImage) as AppCompatImageView
        val tvName = view.findViewById(R.id.tvName) as AppCompatTextView
        val tvEmail = view.findViewById(R.id.tvEmail) as AppCompatTextView
        val tvPhone = view.findViewById(R.id.tvPhoneNumber) as AppCompatTextView
        val tvTerms = view.findViewById(R.id.tvTerms) as AppCompatTextView
        val ivDelete = view.findViewById(R.id.ivDelete) as AppCompatImageView
    }

    interface onClick {
        fun onItemClick(wareHouseModel: WareHouseModel, position: Int)
        fun onDeleteClick(wareHouseModel: WareHouseModel, position: Int)
    }
}