package com.playplexmatm.adapter.report

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.R
import com.playplexmatm.model.report.SalesSummaryModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SalesSummaryAdapter(
    val context: Context,
    var salesSummaryAdapter: ArrayList<SalesSummaryModel>
) : RecyclerView.Adapter<SalesSummaryAdapter.myViewholder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): myViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_sales_summary, parent, false)
        return myViewholder(view)
    }

    override fun onBindViewHolder(holder: myViewholder, position: Int) {
        val salesSummaryModel = salesSummaryAdapter[position]

        holder.tvUserName.setText("User:- "+salesSummaryModel.user.name)
        holder.tvInvoiceNumber.setText(salesSummaryModel.invoice_number)
        holder.tvDate.setText(convertDate(salesSummaryModel.order_date))
        holder.tvPaymentStatus.setText("Payment Status:- "+salesSummaryModel.payment_status)
        holder.tvTotal.setText(salesSummaryModel.total)
    }

    override fun getItemCount(): Int {
        return salesSummaryAdapter.size
    }


    class myViewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUserName = view.findViewById(R.id.tvUserName) as TextView
        val tvDate = view.findViewById(R.id.tvDate) as TextView
        val tvInvoiceNumber = view.findViewById(R.id.tvInvoiceNumber) as TextView
        val tvPaymentStatus = view.findViewById(R.id.tvPaymentStatus) as TextView
        val tvTotal = view.findViewById(R.id.tvTotal) as TextView

    }


    fun convertDate(date: String): String {
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+SS:SS")
        val output = SimpleDateFormat("dd-MM-yyyy")

        var d: Date? = null
        try {
            d = input.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val formatted = output.format(d)
        Log.i("DATE", "" + formatted)
        return formatted
    }

    fun updateList(list: List<SalesSummaryModel>) {
        salesSummaryAdapter = list as ArrayList<SalesSummaryModel>
        notifyDataSetChanged()
    }
}