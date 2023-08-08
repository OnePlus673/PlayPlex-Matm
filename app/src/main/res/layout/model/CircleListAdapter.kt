package com.sg.swapnapay.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.R

class CircleListAdapter(context: Context?,
                         recievedMoneyHistoryModelList: List<CircleModel>,
                         mListener: ListAdapterListener
) : RecyclerView.Adapter<CircleListAdapter.ViewHolder>() {
    private val recievedMoneyHistoryModelList: List<CircleModel>
    private val mInflater: LayoutInflater
    private val mListener: ListAdapterListener
    var mContext: Context? = null

    interface ListAdapterListener {
        // create an interface
        fun onClickAtOKButton(circleModel: CircleModel?) // create callback function
    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder {
        mContext = parent.context
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem: View =
                layoutInflater.inflate(R.layout.layout_list_circle, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int
    ) {
        val circleModel: CircleModel = recievedMoneyHistoryModelList[position]
        holder.tvCircleName.text = circleModel.state_name
        holder.itemView.setOnClickListener { mListener.onClickAtOKButton(circleModel) }
    }

    override fun getItemCount(): Int {
        return recievedMoneyHistoryModelList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvCircleName: TextView

        init {
            tvCircleName = itemView.findViewById(R.id.tvCircleName)
        }
    }
    // RecyclerView recyclerView;
    init {
        mInflater = LayoutInflater.from(context)
        this.recievedMoneyHistoryModelList = recievedMoneyHistoryModelList
        this.mListener = mListener // receive mListener from Fragment (or Activity)
    }
}
