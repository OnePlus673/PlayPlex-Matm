package com.playplexmatm.adapter

import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.playplexmatm.R
import com.playplexmatm.activity.OrderDetailsActivity
import com.playplexmatm.activity.pos.saleList.AddSaleActivity
import com.playplexmatm.activity.pos.saleList.SaleListActivity
import com.playplexmatm.model.saleList.SaleListModel
import com.playplexmatm.util.Downloader.DownloadFile
import kotlinx.android.synthetic.main.layout_action_bottomsheet.view.*
import java.io.File
import java.io.IOException


class SaleListAdapter(
    val mContext: SaleListActivity, var saleList: ArrayList<SaleListModel>,
    val onclick: onClick
) : RecyclerView.Adapter<SaleListAdapter.Myviewholder>() {

    lateinit var bottomSheetDialog: BottomSheetDialog

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SaleListAdapter.Myviewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adp_salelist, parent, false)
        return Myviewholder(view)
    }

    override fun onBindViewHolder(holder: SaleListAdapter.Myviewholder, position: Int) {
        val saleListModel = saleList[position]
        holder.tvCustomerName.text = saleListModel.user.name
        holder.tvInvoiceNumber.text = saleListModel.invoice_number
        holder.tvAmount.text = "Amount:- " + "₹" + saleListModel.total.toString()
        holder.tvStatus.text = "Status:- " + saleListModel.payment_status

        holder.ivMore.setOnClickListener {
            showBottomSheet(saleListModel,position)
//            showPopup(holder.ivDelete, saleListModel, position)
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
    }

    interface onClick {
        fun onDeleteClick(saleListModel: SaleListModel, position: Int)
    }

    fun showPopup(v: View, saleListModel: SaleListModel, position: Int) {
        val popupMenu = PopupMenu(mContext , v)
        // add the menu
        popupMenu.inflate(R.menu.popup_menu)
        // implement on menu item click Listener
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener{
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when(item?.itemId){
                    R.id.itemView -> {
                        val bundle = Bundle()
                        val intent = Intent(mContext, OrderDetailsActivity::class.java)
                        bundle.putString("from","saleListModel")
                        bundle.putSerializable("model",saleListModel)
                        intent.putExtras(bundle)
                        mContext.startActivity(intent)
                        return true
                    }
                    R.id.itemEdit -> {
//                        Toast.makeText(mContext, "Edit" , Toast.LENGTH_SHORT).show()
                        val intent = Intent(mContext, AddSaleActivity::class.java)
                        mContext.startActivity(intent)
                        return true
                    }
                    R.id.itemDelete -> {
                        onclick.onDeleteClick(saleListModel,position)
                        return true
                    }

                    R.id.itemDownload -> {
                        Toast.makeText(mContext, "Downloading File...!!!", Toast.LENGTH_SHORT).show()
                        val imageurl: String = "https://pospayplex.com/api/v1/pdf/"+saleListModel.unique_id+"/en"
                        val dm = mContext.getSystemService(DOWNLOAD_SERVICE) as DownloadManager?
                        val request = DownloadManager.Request(Uri.parse(imageurl))

                        request.allowScanningByMediaScanner()
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) //Notify client once download is completed!
                        dm!!.enqueue(request)
                        return true
                    }
                }
                return false
            }
        })
        popupMenu.show()
    }

    fun downloadPdf(unique_id: String) {
        val extStorageDirectory = Environment.getExternalStorageDirectory()
            .toString()
        val folder = File(extStorageDirectory, "pdf")
        folder.mkdir()
        val file = File(folder, "Read.pdf")
        try {
            file.createNewFile()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        DownloadFile("https://pospayplex.com/api/v1/pdf/"+unique_id+"/en", file)

        showPdf()
    }

    fun showPdf() {
        val file = File(Environment.getExternalStorageDirectory().toString() + "/pdf/Read.pdf")
        val packageManager: PackageManager = mContext.getPackageManager()
        val testIntent = Intent(Intent.ACTION_VIEW)
        testIntent.type = "application/pdf"
        val list: List<*> =
            packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY)
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        val uri: Uri = Uri.fromFile(file)
        intent.setDataAndType(uri, "application/pdf")
        mContext.startActivity(intent)
    }


    private fun showBottomSheet(saleListModel: SaleListModel, position: Int)
    {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.layout_action_bottomsheet, null)

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


        bottomSheetDialog = BottomSheetDialog(mContext)
        bottomSheetDialog!!.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetDialog!!.show()

    }

    fun updateList(list: List<SaleListModel>) {
        saleList = list as ArrayList<SaleListModel>
        notifyDataSetChanged()
    }
}