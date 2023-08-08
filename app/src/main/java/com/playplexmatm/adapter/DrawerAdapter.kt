package com.playplexmatm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.playplexmatm.R
import com.playplexmatm.activity.MainActivity
import com.playplexmatm.model.drawer.DrawerModel
import java.util.ArrayList

class DrawerAdapter(
    val mContext: MainActivity,
    val drawerModelList: ArrayList<DrawerModel>,
    val onclick: onItemClick
) :
    RecyclerView.Adapter<DrawerAdapter.Myviewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Myviewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adp_drawer, parent, false)
        return Myviewholder(view)
    }

    override fun onBindViewHolder(holder: Myviewholder, position: Int) {

        val drawerModel = drawerModelList[position]
        holder.tvName.text = drawerModel.name
        holder.ivImage.setImageResource(drawerModel.image)

        holder.itemView.setOnClickListener {
            onclick.onClick(position, drawerModel)
        }
    }

    override fun getItemCount(): Int {
        return drawerModelList.size
    }

    class Myviewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val ivImage = itemview.findViewById(R.id.ivImage) as AppCompatImageView
        val tvName = itemview.findViewById(R.id.tvName) as AppCompatTextView

    }



    interface onItemClick {
        fun onClick(position: Int, drawerModel: DrawerModel)
    }
}