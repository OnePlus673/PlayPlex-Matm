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
import com.playplelx.model.customers.CustomerModel
import de.hdodenhof.circleimageview.CircleImageView

class CustomersAdapter(
    val context: Context,
    val customerArrayList: ArrayList<CustomerModel>,
    val onclick: onClick
) :
    RecyclerView.Adapter<CustomersAdapter.myViewholder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): myViewholder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adp_customers, parent, false)
        return myViewholder(view)
    }

    override fun onBindViewHolder(holder: myViewholder, position: Int) {
        val customerModel = customerArrayList[position]
        holder.tvCustomerName.text = "Name:- " + customerModel.name
        holder.tvCustomerEmail.text = "Email:- " + customerModel.email
        holder.tvCustomerPhone.text = "Phone:- " + customerModel.phone
        holder.tvCustomerAddress.text = "Address:- " + customerModel.address
        holder.tvOpeningBalanceType.text =
            customerModel.details.opening_balance_type + "- " + customerModel.details.opening_balance

        if (!customerModel.profile_image_url.isNullOrEmpty()) {
            Glide.with(context).load(customerModel.profile_image_url).into(holder.ivCustomerImage)
        }

        holder.itemView.setOnClickListener {
            onclick.onItemClick(customerModel, position)
        }
        holder.ivDelete.setOnClickListener {
            onclick.onDeleteClick(customerModel, position)
        }
    }

    override fun getItemCount(): Int {
        return customerArrayList.size
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