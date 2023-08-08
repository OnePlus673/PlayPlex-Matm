package com.playplexmatm.microatm

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.playplexmatm.R
import com.playplexmatm.util.AppConstants
import com.playplexmatm.util.AppPrefs
import com.sg.swapnapay.model.MicroAtmHistoryModel
import com.sg.swapnapay.model.UserModel
import java.util.ArrayList

class MicroAtmHistoryAdapter (
    context: Context?,
    rechargeHistoryModalList: ArrayList<MicroAtmHistoryModel>
) :
    RecyclerView.Adapter<MicroAtmHistoryAdapter.ViewHolder>() {
    private var rechargeHistoryModalList: List<MicroAtmHistoryModel>
    private val mInflater: LayoutInflater
    private var mContext: Context? = null
    lateinit var userModel: UserModel

    var user_type: String = ""
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        mContext = parent.context
        user_type = AppPrefs.getStringPref("user_type", mContext).toString()
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem: View =
            layoutInflater.inflate(R.layout.layout_list_matm_history, parent, false)

        val gson = Gson()
        val json = AppPrefs.getStringPref(AppConstants.USER_MODEL,mContext)
        userModel = gson.fromJson(json, UserModel::class.java)

        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val rechargeHistoryModal: MicroAtmHistoryModel =
            rechargeHistoryModalList[position]
        holder.tvTxnId.text = rechargeHistoryModal.ma_id
        holder.tvDate.text = rechargeHistoryModal.ma_date_time
        holder.tvTransactionType.text =
            "Transaction Type : " + rechargeHistoryModal.transType.toUpperCase()

        holder.tvStatus_ref.text = "Ref ID : " + rechargeHistoryModal.transId
        holder.btnCheckStatus.visibility = View.GONE

        holder.ivPdf.visibility = View.GONE

        when {
            user_type.equals("retailer") -> {
                holder.tvCommission.setText(mContext!!.resources.getString(R.string.Rupee)+" "+rechargeHistoryModal.retailer_commission)
            }
            user_type.equals("distributor") -> {
                holder.tvCommission.setText(mContext!!.resources.getString(R.string.Rupee)+" "+rechargeHistoryModal.distributor_commission)
            }
            else -> {
                holder.tvCommission.setText(mContext!!.resources.getString(R.string.Rupee)+" "+rechargeHistoryModal.master_commission)
            }
        }

        if (rechargeHistoryModal.status.equals("Success",ignoreCase = true)) {
            holder.tvStatus.text = rechargeHistoryModal.status.toUpperCase()
            holder.tvStatus.setTextColor(mContext!!.resources.getColor(R.color.material_green_700))
            holder.btnCheckStatus.visibility = View.GONE
        }
        else if (rechargeHistoryModal.status.equals("Pending",ignoreCase = true)) {

            holder.tvStatus.text = rechargeHistoryModal.status.toUpperCase()
            holder.tvStatus.setTextColor(mContext!!.resources.getColor(R.color.amber))
            holder.btnCheckStatus.visibility = View.GONE

        }
        else {
            holder.tvStatus.text = rechargeHistoryModal.status.toUpperCase()
            holder.tvStatus.setTextColor(mContext!!.resources.getColor(R.color.material_red_500))
            holder.btnCheckStatus.visibility = View.GONE

        }
        holder.tvAmount.text = "₹ " + rechargeHistoryModal.transAmount
    }

    override fun getItemCount(): Int {
        return rechargeHistoryModalList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // var ivStatus: ImageView
        var tvTxnId: TextView
        var tvDate: TextView
        var tvAmount: TextView
        var tvStatus: TextView
        var tvStatus_ref: TextView
        var tvTransactionType: TextView
        var btnCheckStatus: Button
        var ivPdf : ImageView
        var tvCommission: TextView

        init {
            tvTxnId = itemView.findViewById(R.id.tvTxnId)
            tvDate = itemView.findViewById(R.id.tvDate)
            tvAmount = itemView.findViewById(R.id.tvRecAmnt)
            tvStatus = itemView.findViewById(R.id.tvStatus)
            tvStatus_ref = itemView.findViewById(R.id.tvRefId)
            tvTransactionType = itemView.findViewById(R.id.tvTransactionType)
            btnCheckStatus = itemView.findViewById(R.id.btnCheckStatus)
            ivPdf = itemView.findViewById(R.id.ivPdf)
            tvCommission = itemView.findViewById(R.id.tvCommission)
        }
    }

    fun filterList(filterdNames: ArrayList<MicroAtmHistoryModel>) {
        rechargeHistoryModalList = filterdNames
        notifyDataSetChanged()
    }

    companion object {
        const val imgUrl = " http://edigitalvillage.in/assets/operator_img/"
    }

    // RecyclerView recyclerView;
    init {
        mInflater = LayoutInflater.from(context)
        this.rechargeHistoryModalList = rechargeHistoryModalList
    }
}