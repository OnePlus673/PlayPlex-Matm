package com.playplexmatm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.playplexmatm.R
import com.playplexmatm.model.customers.CustomerModel
import de.hdodenhof.circleimageview.CircleImageView

class SupplierAdapter(
    val context: Context,
    val supplierArrayList: ArrayList<CustomerModel>,
    val onclick: onClick
) :
    RecyclerView.Adapter<SupplierAdapter.myViewholder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): myViewholder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adp_customers, parent, false)
        return myViewholder(view)
    }

    override fun onBindViewHolder(holder: myViewholder, position: Int) {
        val supplierModel = supplierArrayList[position]
        holder.tvCustomerName.text = "Name:- " + supplierModel.name
        holder.tvCustomerEmail.text = "Email:- " + supplierModel.email
        holder.tvCustomerPhone.text = "Phone:- " + supplierModel.phone
        holder.tvCustomerAddress.text = "Address:- " + supplierModel.address
        holder.tvOpeningBalanceType.text =
            supplierModel.details.opening_balance_type + "- " + supplierModel.details.opening_balance

        if (!supplierModel.profile_image_url.isNullOrEmpty()) {
            Glide.with(context).load(supplierModel.profile_image_url).into(holder.ivCustomerImage)
        }

        holder.itemView.setOnClickListener {
            onclick.onItemClick(supplierModel, position)
        }
        holder.ivDelete.setOnClickListener {
            onclick.onDeleteClick(supplierModel, position)
        }
    }

    override fun getItemCount(): Int {
        return supplierArrayList.size
    }

    interface onClick {
        fun onItemClick(customerModel: CustomerModel, position: Int)
        fun onDeleteClick(customerModel: CustomerModel, position: Int)
    }

    class myViewholder(view: View) : RecyclerView.ViewHolder(view) {
        val ivCustomerImage = view.findViewById(R.id.ivCustomerImage) as CircleImageView
        val tvCustomerName = view.findViewById(R.id.tvCustomerName) as AppCompatTextView
        val tvCustomerEmail = view.findViewById(R.id.tvCustomerEmail) as AppCompatTextView
        val tvCustomerPhone = view.findViewById(R.id.tvCustomerPhone) as AppCompatTextView
        val tvCustomerAddress = view.findViewById(R.id.tvCustomerAddress) as AppCompatTextView
        val tvOpeningBalanceType = view.findViewById(R.id.tvOpeningBalanceType) as AppCompatTextView
        val ivDelete = view.findViewById(R.id.ivDelete) as AppCompatImageView
    }
}