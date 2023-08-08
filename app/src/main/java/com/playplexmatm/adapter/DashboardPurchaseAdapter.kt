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
import com.playplexmatm.activity.AddPurchaseListActivity
import com.playplexmatm.activity.OrderDetailsActivity
import com.playplexmatm.model.purchasemodel.PurchaseModel
import kotlinx.android.synthetic.main.layout_action_bottomsheet.view.*

class DashboardPurchaseAdapter(
    val mContext: Context, var purchaseList: ArrayList<PurchaseModel>,
    val onclick: onClick

) : RecyclerView.Adapter<DashboardPurchaseAdapter.Myviewholder>() {

    lateinit var bottomSheetDialog: BottomSheetDialog

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Myviewholder {


        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_dashboard_purchase, parent, false)
        return Myviewholder(view)
    }

    override fun onBindViewHolder(holder: Myviewholder, position: Int) {
        val purchaseModel = purchaseList[position]
        holder.tvCustomerName.text = purchaseModel.user.name
        holder.tvInvoiceNumber.text = purchaseModel.invoice_number
        holder.tvAmount.text = "₹ " + purchaseModel.total.toString()
        holder.tvDueAmount.text = "₹ " + purchaseModel.due_amount.toString()
        holder.tvStatus.text = "STATUS:- " + purchaseModel.payment_status.toUpperCase()

        holder.ivMore.setOnClickListener {
            showBottomSheet(purchaseModel,position)
//            showPopup(holder.ivDelete, purchaseModel, position)
        }

        holder.ivPrint.setOnClickListener {
            onclick.onPrintClick(purchaseModel, position)
        }

        holder.ivShare.setOnClickListener {
            onclick.onShareClick(purchaseModel, position)
        }
    }

    override fun getItemCount(): Int {
        return purchaseList.size
    }

    class Myviewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCustomerName = view.findViewById(R.id.tvCustomerName) as AppCompatTextView
        val tvInvoiceNumber = view.findViewById(R.id.tvInvoiceNumber) as AppCompatTextView
        val tvAmount = view.findViewById(R.id.tvAmount) as AppCompatTextView
        val tvStatus = view.findViewById(R.id.tvStatus) as AppCompatTextView
        val tvDueAmount = view.findViewById(R.id.tvDueAmount) as AppCompatTextView
        val ivMore = view.findViewById(R.id.ivMore) as AppCompatImageView
        val ivPrint = view.findViewById(R.id.ivPrint) as AppCompatImageView
        val ivShare = view.findViewById(R.id.ivShare) as AppCompatImageView
    }

    private fun showBottomSheet(purchaseModel: PurchaseModel, position: Int)
    {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.layout_action_bottomsheet, null)


        view.ivClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        view.tvView.setOnClickListener {
            val bundle = Bundle()
            val intent = Intent(mContext, OrderDetailsActivity::class.java)
            bundle.putString("from","purchaseModel")
            bundle.putSerializable("model",purchaseModel)
            intent.putExtras(bundle)
            mContext.startActivity(intent)
            bottomSheetDialog.dismiss()

        }

        view.tvEdit.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("model",purchaseModel)
            val intent = Intent(mContext, AddPurchaseListActivity::class.java)
            intent.putExtras(bundle)
            mContext.startActivity(intent)
            bottomSheetDialog.dismiss()

        }

        view.tvDelete.setOnClickListener {
            onclick.onDeleteClick(purchaseModel,position)
            bottomSheetDialog.dismiss()

        }

        view.tvDownload.setOnClickListener {
            Toast.makeText(mContext, "Downloading File...!!!", Toast.LENGTH_SHORT).show()
            val imageurl: String = "https://pospayplex.com/api/v1/pdf/"+purchaseModel.unique_id+"/en"
            val dm = mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
            val request = DownloadManager.Request(Uri.parse(imageurl))
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) //Notify client once download is completed!
            dm!!.enqueue(request)
            bottomSheetDialog.dismiss()
        }


        bottomSheetDialog = BottomSheetDialog(mContext,R.style.SheetDialog)
        bottomSheetDialog.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetDialog.show()

    }

    interface onClick {
        fun onDeleteClick(purchaseModel: PurchaseModel, position: Int)
        fun onPrintClick(purchaseModel: PurchaseModel,position: Int)
        fun onShareClick(purchaseModel: PurchaseModel,position: Int)
    }

    fun updateList(list: List<PurchaseModel>) {
        purchaseList = list as ArrayList<PurchaseModel>
        notifyDataSetChanged()
    }
}