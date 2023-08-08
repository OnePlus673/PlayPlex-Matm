package com.payplex.aeps.aeps_activities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.payplex.aeps.activities_aeps.aepshistory.AepsLedgerHistoryModel
import com.playplexmatm.R
import com.playplexmatm.util.AppPrefs
import java.util.*

class AepsLedgerHistoryAdapter(
    context: Context?,
    rechargeHistoryModalList: ArrayList<AepsLedgerHistoryModel>
) :
    RecyclerView.Adapter<AepsLedgerHistoryAdapter.ViewHolder>() {
    private var rechargeHistoryModalList: List<AepsLedgerHistoryModel>
    private val mInflater: LayoutInflater
    private var mContext: Context? = null

    var user_type: String = ""
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        mContext = parent.context
        user_type = AppPrefs.getStringPref("user_type", mContext).toString()
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem: View =
            layoutInflater.inflate(R.layout.layout_list_aeps_ledger_history, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val rechargeHistoryModal: AepsLedgerHistoryModel =
            rechargeHistoryModalList[position]
        holder.tvTxnId.text = rechargeHistoryModal.aeps_txn_id
        holder.tvDate.text = rechargeHistoryModal.aeps_txn_date
        holder.tvTransactionType.text =
            "Transaction Type : " + rechargeHistoryModal.aeps_txn_type.toUpperCase()

        holder.tvStatus_ref.text = "Ref ID : " + rechargeHistoryModal.aeps_txn_recrefid.toUpperCase()
        holder.btnCheckStatus.visibility = GONE

        holder.tvBalance.text = "₹ " + rechargeHistoryModal.aeps_txn_clbal
        holder.tvCreditAmnt.text = "₹ " + rechargeHistoryModal.aeps_txn_crdt
        holder.tvDebitAmnt.text = "₹ " + rechargeHistoryModal.aeps_txn_dbdt
        holder.tvOpeningBalance.text = "₹ " + rechargeHistoryModal.aeps_txn_opbal


    }

    override fun getItemCount(): Int {
        return rechargeHistoryModalList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // var ivStatus: ImageView
        var tvTxnId: TextView
        var tvDate: TextView
       // var tvAmount: TextView
        var tvStatus: TextView
        var tvStatus_ref: TextView
        var tvTransactionType: TextView
        var btnCheckStatus: Button
        var tvCreditAmnt: TextView
        var tvDebitAmnt: TextView
        var tvOpeningBalance: TextView
        var tvBalance: TextView


        init {
            tvTxnId = itemView.findViewById(R.id.tvTxnId)
            tvDate = itemView.findViewById(R.id.tvDate)
           // tvAmount = itemView.findViewById(R.id.tvRecAmnt)
            tvStatus = itemView.findViewById(R.id.tvStatus)
            tvStatus_ref = itemView.findViewById(R.id.tvRefId)
            tvTransactionType = itemView.findViewById(R.id.tvTransactionType)
            btnCheckStatus = itemView.findViewById(R.id.btnCheckStatus)
            tvCreditAmnt = itemView.findViewById(R.id.tvCreditAmnt)
            tvBalance = itemView.findViewById(R.id.tvBalance)
            tvDebitAmnt = itemView.findViewById(R.id.tvDebitAmnt)
            tvOpeningBalance = itemView.findViewById(R.id.tvOpeningBal)
        }
    }

    fun filterList(filterdNames: ArrayList<AepsLedgerHistoryModel>) {
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