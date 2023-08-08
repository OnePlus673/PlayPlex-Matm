package com.playplexmatm.adapter

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.R
import com.playplexmatm.model.UserReportModel
import com.playplexmatm.util.Constants
import com.playplexmatm.util.PrefManager


class UserReportAdapter(
    val context: Context,
    var userReportArrayList: ArrayList<UserReportModel>
) :
    RecyclerView.Adapter<UserReportAdapter.myViewholder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): myViewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_user_reports, parent, false)
        return myViewholder(view)
    }

    override fun onBindViewHolder(holder: myViewholder, position: Int) {
        val userReportModel = userReportArrayList[position]
        holder.tvName.text = userReportModel.name

        holder.tvTotalAmount.setText("Total Amount:- "+context.resources.getString(R.string.Rupee)+" "+userReportModel.details.total_amount)
        holder.tvPaidAmount.setText("Paid Amount:- "+context.resources.getString(R.string.Rupee)+" "+userReportModel.details.paid_amount)
        holder.tvDueAmount.setText("Due Amount:- "+context.resources.getString(R.string.Rupee)+" "+userReportModel.details.due_amount)

        holder.ivWhatsapp.setOnClickListener {
            val i = Intent(Intent.ACTION_SEND)

            i.type = "text/plain"
            i.setPackage("com.whatsapp")

            // Give your message here

            val message = "Dear ${userReportModel.name}, your Balance is ${context.resources.getString(R.string.Rupee)} ${userReportModel.details.due_amount}. Kindly pay on time. Ignore this message if already paid. \nFrom: ${PrefManager(context).getValue(Constants.name)}"

            val sendIntent = Intent("android.intent.action.MAIN")
            sendIntent.action = Intent.ACTION_VIEW
            sendIntent.setPackage("com.whatsapp")
            val url =
                "https://api.whatsapp.com/send?phone=+91" + userReportModel.phone + "&text=" + message
            sendIntent.data = Uri.parse(url)
            if (sendIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(sendIntent)
            }
        }
    }

    override fun getItemCount(): Int {
        return userReportArrayList.size
    }


    class myViewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById(R.id.tvName) as TextView
        val tvTotalAmount = view.findViewById(R.id.tvTotalAmount) as TextView
        val tvPaidAmount = view.findViewById(R.id.tvPaidAmount) as TextView
        val tvDueAmount = view.findViewById(R.id.tvDueAmount) as TextView
        val ivWhatsapp = view.findViewById(R.id.ivWhatsapp) as ImageView
    }


    fun updateList(list: List<UserReportModel>) {
        userReportArrayList = list as ArrayList<UserReportModel>
        notifyDataSetChanged()
    }

    private fun openWhatsApp(
        phone: String, message: String
    ) {
        val isWhatsappInstalled: Boolean = whatsappInstalledOrNot("com.whatsapp")
        if (isWhatsappInstalled) {
            val sendIntent = Intent()
            sendIntent.setAction(Intent.ACTION_SEND)
                .setPackage("com.whatsapp")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(phone) + "@s.whatsapp.net") //phone number without "+" prefix
            sendIntent.setType("text/plain").putExtra(Intent.EXTRA_TEXT, message)
            context.startActivity(sendIntent)
        } else {
            val uri = Uri.parse("market://details?id=com.whatsapp")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            Toast.makeText(
                context, "WhatsApp not Installed",
                Toast.LENGTH_SHORT
            ).show()
            context.startActivity(goToMarket)
        }
    }

    private fun whatsappInstalledOrNot(uri: String): Boolean {
        val pm: PackageManager = context.getPackageManager()
        var app_installed = false
        app_installed = try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return app_installed
    }

}