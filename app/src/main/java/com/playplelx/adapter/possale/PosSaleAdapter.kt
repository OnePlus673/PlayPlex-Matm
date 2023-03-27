package com.playplelx.adapter.possale

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.playplelx.R
import com.playplelx.activity.pos.NewPosSaleActivity
import com.playplelx.model.categoryofproducts.Items
import com.playplelx.model.posproducts.PosProductFilterModel
import com.playplelx.model.posproducts.ProductData
import com.playplelx.model.productfilter.ProductFilterModel
import com.playplelx.util.DatabaseHelper
import java.text.DecimalFormat

class PosSaleAdapter(
    val mContext: NewPosSaleActivity,
    val posProductModelArrayList: ArrayList<Items>,
    val onclick: onClick
) : RecyclerView.Adapter<PosSaleAdapter.Myviewholder>() {

    private var dataBaseHelper = DatabaseHelper(mContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosSaleAdapter.Myviewholder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adp_possale, parent, false)
        return Myviewholder(view)
    }

    override fun onBindViewHolder(holder: PosSaleAdapter.Myviewholder, position: Int) {
        val posProductFilterModel = posProductModelArrayList.get(position)
        Glide.with(mContext).load(posProductFilterModel.image_url).into(holder.ivImage)
        holder.tvName.text = posProductFilterModel.name.toLowerCase()
        holder.tvQuantity.text = dataBaseHelper.CheckOrderExists(
            posProductFilterModel.xid,
            posProductFilterModel.x_unit_id
        ).toString()
        holder.tvSubTotal.text = (DecimalFormat("##.#").format(
            dataBaseHelper.CheckItemsTotal(
                posProductFilterModel.xid,
                posProductFilterModel.x_unit_id,
                posProductFilterModel.single_unit_price,
                posProductFilterModel.quantity
            )
        ).toDouble()).toString()


        holder.tvAdd.setOnClickListener {
            onclick.onAddClick(
                posProductFilterModel, position,
                holder.tvQuantity,
                holder.tvSubTotal
            )
        }

        holder.tvMinus.setOnClickListener {
            onclick.onMinusClick(
                posProductFilterModel,
                position,
                holder.tvQuantity,
                holder.tvSubTotal
            )
        }


        /*     holder.tvAdd.setOnClickListener {
                 onclick.onAddClick(
                     posProductFilterModel,
                     position,
                     holder.tvQuantity,
                     holder.tvSubTotal
                 )
             }
             holder.tvMinus.setOnClickListener {
                 onclick.onMinusClick(
                     posProductFilterModel,
                     position,
                     holder.tvQuantity,
                     holder.tvSubTotal
                 )
             }
             holder.ivDelete.setOnClickListener {
                 onclick.onDeleteClick(posProductFilterModel, position)

             }*/


    }

    override fun getItemCount(): Int {
        return posProductModelArrayList.size

    }


    class Myviewholder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage = view.findViewById(R.id.ivImage) as AppCompatImageView
        val tvName = view.findViewById(R.id.tvName) as AppCompatTextView
        val tvAdd = view.findViewById(R.id.tvAdd) as AppCompatTextView
        val tvQuantity = view.findViewById(R.id.tvQuantity) as AppCompatTextView
        val tvMinus = view.findViewById(R.id.tvMinus) as AppCompatTextView
        val tvSubTotal = view.findViewById(R.id.tvSubTotal) as AppCompatTextView
        val ivDelete = view.findViewById(R.id.ivDelete) as AppCompatImageView
    }

    interface onClick {
        fun onAddClick(
            filterModel: Items,
            position: Int,
            tvQuantity: TextView,
            tvSubTotal: TextView
        )

        fun onMinusClick(
            filterModel: Items,
            position: Int,
            tvQuantity: TextView,
            tvSubTotal: TextView
        )

        fun onDeleteClick(filterModel: ProductData, position: Int)
    }
}