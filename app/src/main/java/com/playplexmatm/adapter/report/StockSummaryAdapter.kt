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
import com.playplexmatm.model.report.StockSummaryModel

class StockSummaryAdapter(
    val context: Context,
    var productReportsAdapter: ArrayList<StockSummaryModel>
) : RecyclerView.Adapter<StockSummaryAdapter.myViewholder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): myViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_stock_summary, parent, false)
        return myViewholder(view)
    }

    override fun onBindViewHolder(holder: myViewholder, position: Int) {
        val productsReportModel = productReportsAdapter[position]

        holder.tvProductName.setText(productsReportModel.name)
        holder.tvItemCode.setText("Item Code:- "+productsReportModel.item_code)
        holder.tvCurrentStock.setText("Current Stock:- "+productsReportModel.details.current_stock)
        holder.tvCategory.setText("Category:- "+productsReportModel.category.name)
        try {
            holder.tvBrand.setText("Brand:- "+productsReportModel.brand.name)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        holder.tvPurchasePrice.setText(context.resources.getString(R.string.Rupee)+" "+productsReportModel.details.purchase_price)
        holder.tvSalesPrice.setText(context.resources.getString(R.string.Rupee)+" "+productsReportModel.details.sales_price)
        holder.tvStockValuePurchase.setText(context.resources.getString(R.string.Rupee)+" "+(productsReportModel.details.purchase_price.toDouble() * productsReportModel.details.current_stock.toDouble()))
        holder.tvStockValueSales.setText(context.resources.getString(R.string.Rupee)+" "+(productsReportModel.details.sales_price.toDouble() * productsReportModel.details.current_stock.toDouble()))
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
        val tvCategory = view.findViewById(R.id.tvCategory) as TextView
        val tvBrand = view.findViewById(R.id.tvBrand) as TextView
        val tvPurchasePrice = view.findViewById(R.id.tvPurchasePrice) as TextView
        val tvSalesPrice = view.findViewById(R.id.tvSalesPrice) as TextView
        val tvStockValuePurchase = view.findViewById(R.id.tvStockValuePurchase) as TextView
        val tvStockValueSales = view.findViewById(R.id.tvStockValueSales) as TextView
        val ivProductImage = view.findViewById(R.id.ivProductImage) as ImageView

    }

    fun updateList(list: List<StockSummaryModel>) {
        productReportsAdapter = list as ArrayList<StockSummaryModel>
        notifyDataSetChanged()
    }
}