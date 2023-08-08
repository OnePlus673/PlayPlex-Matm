package com.playplexmatm.adapter

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.playplexmatm.R
import com.playplexmatm.activity.OrderDetailsActivity
import com.playplexmatm.activity.pos.saleList.AddSaleActivity
import com.playplexmatm.model.saleList.SaleListModel
import kotlinx.android.synthetic.main.layout_action_bottomsheet.view.*

class DashboardSaleListAdapter(
    val mContext: Context, var saleList: ArrayList<SaleListModel>,
    val onclick: onClick
) : RecyclerView.Adapter<DashboardSaleListAdapter.Myviewholder>() {

    lateinit var bottomSheetDialog: BottomSheetDialog

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Myviewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_dashboard_sale, parent, false)
        return Myviewholder(view)
    }

    override fun onBindViewHolder(holder: Myviewholder, position: Int) {
        val saleListModel = saleList[position]
        holder.tvCustomerName.text = saleListModel.user.name
        holder.tvInvoiceNumber.text = saleListModel.invoice_number
        holder.tvAmount.text = "₹ " + saleListModel.total
        holder.tvDueAmount.text = "₹ " + saleListModel.due_amount
        holder.tvStatus.text = "STATUS:- " + saleListModel.payment_status.toUpperCase()

        holder.ivMore.setOnClickListener {
            showBottomSheet(saleListModel,position)
        }

        holder.ivPrint.setOnClickListener {
            onclick.onPrintClick(saleListModel, position)
        }

        holder.ivShare.setOnClickListener {
            onclick.onShareClick(saleListModel, position)
        }

    }

    override fun getItemCount(): Int {
        return saleList.size
    }

    class Myviewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCustomerName = view.findViewById(R.id.tvCustomerName) as AppCompatTextView
        val tvInvoiceNumber = view.findViewById(R.id.tvInvoiceNumber) as AppCompatTextView
        val tvAmount = view.findViewById(R.id.tvAmount) as AppCompatTextView
        val tvStatus = view.findViewById(R.id.tvStatus) as AppCompatTextView
        val ivMore = view.findViewById(R.id.ivMore) as AppCompatImageView
        val ivPrint = view.findViewById(R.id.ivPrint) as AppCompatImageView
        val ivShare = view.findViewById(R.id.ivShare) as AppCompatImageView
        val tvDueAmount = view.findViewById(R.id.tvDueAmount) as AppCompatTextView
    }

    private fun showBottomSheet(saleListModel: SaleListModel, position: Int)
    {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.layout_action_bottomsheet, null)

        view.ivClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        view.tvView.setOnClickListener {
            val bundle = Bundle()
            val intent = Intent(mContext, OrderDetailsActivity::class.java)
            bundle.putString("from","saleListModel")
            bundle.putSerializable("model",saleListModel)
            intent.putExtras(bundle)
            mContext.startActivity(intent)
            bottomSheetDialog.dismiss()
        }

        view.tvEdit.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("model",saleListModel)
            val intent = Intent(mContext, AddSaleActivity::class.java)
            intent.putExtras(bundle)
            mContext.startActivity(intent)
            bottomSheetDialog.dismiss()

        }

        view.tvDelete.setOnClickListener {
            onclick.onDeleteClick(saleListModel,position)
            bottomSheetDialog.dismiss()

        }

        view.tvDownload.setOnClickListener {
            Toast.makeText(mContext, "Downloading File...!!!", Toast.LENGTH_SHORT).show()
            val imageurl: String = "https://pospayplex.com/api/v1/pdf/"+saleListModel.unique_id+"/en"
            val dm = mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
            val request = DownloadManager.Request(Uri.parse(imageurl))
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) //Notify client once download is completed!
            dm!!.enqueue(request)
            bottomSheetDialog.dismiss()

        }


        bottomSheetDialog = BottomSheetDialog(mContext,R.style.SheetDialog)
        bottomSheetDialog!!.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetDialog!!.show()

    }

    interface onClick {
        fun onDeleteClick(saleListModel: SaleListModel, position: Int)
        fun onPrintClick(saleListModel: SaleListModel, position: Int)
        fun onShareClick(saleListModel: SaleListModel, position: Int)
    }

    fun updateList(list: List<SaleListModel>) {
        saleList = list as ArrayList<SaleListModel>
        notifyDataSetChanged()
    }
}