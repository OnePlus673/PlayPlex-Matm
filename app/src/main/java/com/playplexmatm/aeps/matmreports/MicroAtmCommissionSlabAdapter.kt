package com.playplexmatm.aeps.matmreports

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.playplexmatm.R
import com.playplexmatm.util.AppConstants
import com.playplexmatm.util.AppPrefs
import com.sg.swapnapay.model.MicroAtmCommissionSlabModel
import com.sg.swapnapay.model.UserModel
import java.util.ArrayList

class MicroAtmCommissionSlabAdapter (
    context: Context?,
    rechargeHistoryModalList: ArrayList<MicroAtmCommissionSlabModel>
) :
    RecyclerView.Adapter<MicroAtmCommissionSlabAdapter.ViewHolder>() {
    private var rechargeHistoryModalList: List<MicroAtmCommissionSlabModel>
    private val mInflater: LayoutInflater
    private var mContext: Context? = null
    lateinit var userModel : UserModel

    var user_type: String = ""
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        mContext = parent.context
        user_type = AppPrefs.getStringPref("user_type", mContext).toString()
        val layoutInflater = LayoutInflater.from(parent.context)

        val gson = Gson()
        val json = AppPrefs.getStringPref(AppConstants.USER_MODEL,mContext)
        userModel = gson.fromJson(json, UserModel::class.java)

        val listItem: View =
            layoutInflater.inflate(R.layout.layout_list_commisionslab_aeps, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val rechargeHistoryModal: MicroAtmCommissionSlabModel =
            rechargeHistoryModalList[position]

        holder.tvOpertorName.text = rechargeHistoryModal.amount_min_range + " - " + rechargeHistoryModal.amount_max_range

        holder.tvType.text = rechargeHistoryModal.type

        if(userModel.cus_type.equals("retailer",true))
        {
            holder.tvCommisionRecvd.text = rechargeHistoryModal.retailer_comm
        }
        else if(userModel.cus_type.equals("distributor",true))
        {
            holder.tvCommisionRecvd.text = rechargeHistoryModal.distributor_comm
        }
        else if(userModel.cus_type.equals("master",true))
        {
            holder.tvCommisionRecvd.text = rechargeHistoryModal.master_comm
        }




    }

    override fun getItemCount(): Int {
        return rechargeHistoryModalList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvOpertorName: TextView
        var tvCommisionRecvd: TextView
        var tvType: TextView

        init {
            tvOpertorName = itemView.findViewById(R.id.tvPackageName)
            tvCommisionRecvd = itemView.findViewById(R.id.tvCommisionRecvd)
            tvType = itemView.findViewById(R.id.tvMembertype)

//            ivStatus = itemView.findViewById(R.id.ivStatus)
        }
    }

    fun filterList(filterdNames: ArrayList<MicroAtmCommissionSlabModel>) {
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