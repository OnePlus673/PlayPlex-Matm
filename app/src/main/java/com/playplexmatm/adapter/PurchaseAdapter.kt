package com.playplexmatm.adapter

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.playplexmatm.R
import com.playplexmatm.activity.AddPurchaseListActivity
import com.playplexmatm.activity.OrderDetailsActivity
import com.playplexmatm.activity.PurchaseListActivity
import com.playplexmatm.model.purchasemodel.PurchaseModel
import kotlinx.android.synthetic.main.layout_action_bottomsheet.view.*
import java.util.*


class PurchaseAdapter(
    val mContext: PurchaseListActivity, var purchaseList: ArrayList<PurchaseModel>,
    val onclick: onClick
) : RecyclerView.Adapter<PurchaseAdapter.Myviewholder>() {
    var manager: DownloadManager? = null
    lateinit var bottomSheetDialog: BottomSheetDialog

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PurchaseAdapter.Myviewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adp_salelist, parent, false)
        return Myviewholder(view)
    }

    override fun onBindViewHolder(holder: PurchaseAdapter.Myviewholder, position: Int) {
        val purchaseModel = purchaseList[position]
        holder.tvCustomerName.text = purchaseModel.user.name
        holder.tvInvoiceNumber.text = purchaseModel.invoice_number
        holder.tvAmount.text = "Amount:- " + "₹" + purchaseModel.total.toString()
        holder.tvStatus.text = "Status:- " + purchaseModel.payment_status

        holder.ivMore.setOnClickListener {
           showBottomSheet(purchaseModel,position)
//            showPopup(holder.ivDelete, purchaseModel, position)
        }

//        holder.itemView.setOnClickListener {
////            val converter: PdfConverter = PdfConverter.getInstance()
////            val file = File(mContext.externalCacheDir, "name_file.pdf")
////            file.createNewFile()
//            val htmlString = "<html><body><p>Hello World</p></body></html>"
////            converter.convert(mContext, htmlString, file)
////            Log.e("File",file.path+" "+file.name+" "+file.extension)
////            val uri = FileProvider.getUriForFile(
////                Objects.requireNonNull(mContext.getApplicationContext()),
////                BuildConfig.APPLICATION_ID + ".provider", file);
////            val share = Intent()
////            share.action = Intent.ACTION_SEND
////            share.type = "application/pdf"
////            share.putExtra(Intent.EXTRA_STREAM, uri)
////            share.setPackage("com.whatsapp")
////            mContext.startActivity(share)
//
////            var file =
////                File(
////                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
////                    "1234.pdf"
////                )
////            Log.e("File",file.exists().toString())
////            if (file.exists()) {
////                val uri = if (Build.VERSION.SDK_INT < 24) Uri.fromFile(file) else Uri.parse(file.path)
////                val shareIntent = Intent().apply {
////                    action = Intent.ACTION_SEND
////                    type = "application/pdf"
////                    putExtra(Intent.EXTRA_STREAM, uri)
////                    putExtra(
////                        Intent.EXTRA_SUBJECT,
////                        "Purchase Bill..."
////                    )
////                    putExtra(
////                        Intent.EXTRA_TEXT,
////                        "Sharing Bill purchase items..."
////                    )
////                }
////                mContext.startActivity(Intent.createChooser(shareIntent, "Share Via"))
////
////            }
//        }
    }

    override fun getItemCount(): Int {
        return purchaseList.size
    }

    class Myviewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCustomerName = view.findViewById(R.id.tvCustomerName) as AppCompatTextView
        val tvInvoiceNumber = view.findViewById(R.id.tvInvoiceNumber) as AppCompatTextView
        val tvAmount = view.findViewById(R.id.tvAmount) as AppCompatTextView
        val tvStatus = view.findViewById(R.id.tvStatus) as AppCompatTextView
        val ivMore = view.findViewById(R.id.ivMore) as AppCompatImageView
    }

    interface onClick {
        fun onDeleteClick(purchaseModel: PurchaseModel, position: Int)
    }

    private fun showBottomSheet(purchaseModel: PurchaseModel, position: Int)
    {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.layout_action_bottomsheet, null)

        view.tvView.setOnClickListener {
            val bundle = Bundle()
            val intent = Intent(mContext,OrderDetailsActivity::class.java)
            bundle.putString("from","purchaseModel")
            bundle.putSerializable("model",purchaseModel)
            intent.putExtras(bundle)
            mContext.startActivity(intent)
            bottomSheetDialog.dismiss()

        }

        view.tvEdit.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("model",purchaseModel)
            val intent = Intent(mContext,AddPurchaseListActivity::class.java)
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


        bottomSheetDialog = BottomSheetDialog(mContext)
        bottomSheetDialog.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetDialog.show()

    }

    fun updateList(list: List<PurchaseModel>) {
        purchaseList = list as ArrayList<PurchaseModel>
        notifyDataSetChanged()
    }



    private fun setData(data: String) {
        val webView = WebView(mContext)
        webView.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?, request: WebResourceRequest?
            ): Boolean {
                return false;
            }

            override fun onPageFinished(view: WebView?, url: String?) {

            }
        })
        val myHtml: String = data
        webView.loadDataWithBaseURL(null, myHtml, "text/HTML", "UTF-8", null)
    }


    private fun getBitmapFromView(view: View): Bitmap? {
        //Define a bitmap with the same size as the view
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        //Get the view's background
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        //return the bitmap
        return returnedBitmap
    }

    private fun shareResultAsImage(webView: WebView) {
        val bitmap = getBitmapOfWebView(webView)
        val pathofBmp =
            MediaStore.Images.Media.insertImage(mContext.contentResolver, bitmap, "IMG_" + Calendar.getInstance().getTime(), null)
        val bmpUri = Uri.parse(pathofBmp)
        val emailIntent1 = Intent(Intent.ACTION_SEND)
        emailIntent1.type = "image/png"
        emailIntent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri)
        mContext.startActivity(emailIntent1)
    }

    private fun getBitmapOfWebView(webView: WebView): Bitmap {
        val picture = webView.capturePicture()
        val bitmap = Bitmap.createBitmap(picture.width, picture.height, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        picture.draw(canvas)
        return bitmap
    }

}