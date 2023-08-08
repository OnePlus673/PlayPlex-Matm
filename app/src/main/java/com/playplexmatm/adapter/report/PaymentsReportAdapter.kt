package com.playplexmatm.adapter.report

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.R
import com.playplexmatm.model.report.PaymentsReportModel

class PaymentsReportAdapter(
    val context: Context,
    var paymentReportsAdapter: ArrayList<PaymentsReportModel>
) :
    RecyclerView.Adapter<PaymentsReportAdapter.myViewholder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): myViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_payments_report, parent, false)
        return myViewholder(view)
    }

    override fun onBindViewHolder(holder: myViewholder, position: Int) {
        val paymentsReportModel = paymentReportsAdapter[position]

        holder.tvAmount.setText(context.resources.getString(R.string.Rupee)+" "+paymentsReportModel.amount)
        holder.tvDate.setText(paymentsReportModel.date)
        holder.tvModeType.setText("Mode Type:- "+paymentsReportModel.payment_mode.name)
        holder.tvPaymentType.setText("Payment Type:- "+paymentsReportModel.payment_type)
        holder.tvReferenceId.setText(paymentsReportModel.payment_number)
        holder.tvUserName.setText("User:- "+paymentsReportModel.user.name)
    }

    override fun getItemCount(): Int {
        return paymentReportsAdapter.size
    }


    class myViewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvReferenceId = view.findViewById(R.id.tvReferenceId) as TextView
        val tvDate = view.findViewById(R.id.tvDate) as TextView
        val tvUserName = view.findViewById(R.id.tvUserName) as TextView
        val tvPaymentType = view.findViewById(R.id.tvPaymentType) as TextView
        val tvModeType = view.findViewById(R.id.tvModeType) as TextView
        val tvAmount = view.findViewById(R.id.tvAmount) as TextView
    }

    fun updateList(list: List<PaymentsReportModel>) {
        paymentReportsAdapter = list as ArrayList<PaymentsReportModel>
        notifyDataSetChanged()
    }

}