package com.playplexmatm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.playplexmatm.R
import com.playplexmatm.model.report.StockSummaryModel

class RateListAdapter(
    val context: Context,
    var productReportsAdapter: ArrayList<StockSummaryModel>
) : RecyclerView.Adapter<RateListAdapter.myViewholder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): myViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_rate_list, parent, false)
        return myViewholder(view)
    }

    override fun onBindViewHolder(holder: myViewholder, position: Int) {
        val productsReportModel = productReportsAdapter[position]

        holder.tvProductName.setText(productsReportModel.name)
        holder.tvItemCode.setText("Item Code:- "+productsReportModel.item_code)
        holder.tvCategory.setText("Category:- "+productsReportModel.category.name)

        holder.tvMrp.setText(context.resources.getString(R.string.Rupee)+" "+productsReportModel.details.mrp)
        holder.tvSalesPrice.setText(context.resources.getString(R.string.Rupee)+" "+productsReportModel.details.sales_price)
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
        val tvCategory = view.findViewById(R.id.tvCategory) as TextView
        val tvMrp = view.findViewById(R.id.tvMrp) as TextView
        val tvSalesPrice = view.findViewById(R.id.tvSalesPrice) as TextView
        val ivProductImage = view.findViewById(R.id.ivProductImage) as ImageView

    }

    fun updateList(list: List<StockSummaryModel>) {
        productReportsAdapter = list as ArrayList<StockSummaryModel>
        notifyDataSetChanged()
    }

}