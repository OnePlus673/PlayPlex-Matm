package com.playplelx.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.playplelx.R
import com.playplelx.activity.pos.PosSettingActivity
import com.playplelx.model.categoryofproducts.CategoryProductModel
import com.playplelx.model.categoryofproducts.Products
import java.util.ArrayList

class PosCategoryAdapter(
    val mContext: PosSettingActivity,
    val categoryList: ArrayList<Products>
) :
    RecyclerView.Adapter<PosCategoryAdapter.Myviewholder>() {

    lateinit var posProductAdapter: PosProductAdapter

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PosCategoryAdapter.Myviewholder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adp_poscategory, parent, false)
        return Myviewholder(view)
    }

    override fun onBindViewHolder(holder: PosCategoryAdapter.Myviewholder, position: Int) {
        val categoryModel = categoryList[position]

        holder.tvName.text = categoryModel.name.toLowerCase()
        holder.rvProducts.layoutManager = GridLayoutManager(mContext, 2)
        holder.rvProducts.setHasFixedSize(true)
        posProductAdapter = PosProductAdapter(mContext, categoryModel.items, mContext)
        holder.rvProducts.adapter = posProductAdapter


        if (position == itemCount - 1) {
            holder.view.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    public class Myviewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById(R.id.tvName) as AppCompatTextView
        val rvProducts = view.findViewById(R.id.rvProducts) as RecyclerView
        val llMain = view.findViewById(R.id.llMain) as LinearLayout
        val view = view.findViewById(R.id.view) as View
    }

}