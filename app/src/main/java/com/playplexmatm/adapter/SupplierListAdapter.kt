package com.playplexmatm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.R
import com.playplexmatm.model.customers.CustomerModel

class SupplierListAdapter (
    val context: Context,
    var customerArrayList: List<CustomerModel>,
    mListener: ListAdapterListener
) :
    RecyclerView.Adapter<SupplierListAdapter.myViewholder>() {

    private val mListener: ListAdapterListener

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): myViewholder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_list_customers, parent, false)
        return myViewholder(view)
    }

    interface ListAdapterListener {
        // create an interface
        fun onClickAtOKButton(customerModel: CustomerModel) // create callback function
    }

    override fun onBindViewHolder(holder: myViewholder, position: Int) {
        val customerModel = customerArrayList[position]
        holder.tvCustomerName.text = "Name:- " + customerModel.name

        holder.tvCustomerNumber.setText("Number:- "+customerModel.phone)

        holder.itemView.setOnClickListener {
            mListener.onClickAtOKButton(customerModel)
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
        val tvCustomerName = view.findViewById(R.id.tvCustomerName) as TextView
        val tvCustomerNumber = view.findViewById(R.id.tvCustomerNumber) as TextView
    }

    fun updateList( list: List<CustomerModel>) {
        customerArrayList = list
        notifyDataSetChanged()
    }

    init {
        this.mListener = mListener // receive mListener from Fragment (or Activity)

    }
}