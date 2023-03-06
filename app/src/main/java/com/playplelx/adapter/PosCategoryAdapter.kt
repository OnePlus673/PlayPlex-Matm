package com.playplelx.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.playplelx.R
import com.playplelx.activity.pos.PosSettingActivity
import com.playplelx.model.category.CategoryModel
import java.util.ArrayList

class PosCategoryAdapter(
    val mContext: PosSettingActivity,
    val categoryList: ArrayList<CategoryModel>
) :
    RecyclerView.Adapter<PosCategoryAdapter.Myviewholder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PosCategoryAdapter.Myviewholder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adp_poscategory, parent, false)
        return Myviewholder(view)
    }

    override fun onBindViewHolder(holder: PosCategoryAdapter.Myviewholder, position: Int) {
        val categoryModel= categoryList[position]
        holder.tvName.text = categoryModel.name.toLowerCase()
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    public class Myviewholder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById(R.id.tvName) as AppCompatTextView
    }

}