package com.playplexmatm.adapter.report

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.playplexmatm.R
import com.playplexmatm.model.report.ProductsReportModel

class ProductReportsAdapter(
    val context: Context,
    var productReportsAdapter: ArrayList<ProductsReportModel>
) : RecyclerView.Adapter<ProductReportsAdapter.myViewholder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): myViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_stock_alert, parent, false)
        return myViewholder(view)
    }

    override fun onBindViewHolder(holder: myViewholder, position: Int) {
        val productsReportModel = productReportsAdapter[position]

        holder.tvProductName.setText(productsReportModel.name)
        holder.tvItemCode.setText("Item Code:- "+productsReportModel.item_code)
        holder.tvCurrentStock.setText("Current Stock:- "+productsReportModel.details.current_stock)
        holder.tvQuantityAlert.setText("Quantity Alert:- "+productsReportModel.details.stock_quantitiy_alert)

        Glide.with(context)
            .load(productsReportModel.image_url)
            .into(holder.ivProductImage)

    }

    override fun getItemCount(): Int {
        return productReportsAdapter.size
    }


    class myViewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProductName = view.findViewById(R.id.tvProductName) as TextView
        val tvItemCode = view.findViewById(R.id.tvItemCode) as TextView
        val tvCurrentStock = view.findViewById(R.id.tvCurrentStock) as TextView
        val tvQuantityAlert = view.findViewById(R.id.tvQuantityAlert) as TextView
        val ivProductImage = view.findViewById(R.id.ivProductImage) as ImageView

    }

    fun updateList(list: List<ProductsReportModel>) {
        productReportsAdapter = list as ArrayList<ProductsReportModel>
        notifyDataSetChanged()
    }
}