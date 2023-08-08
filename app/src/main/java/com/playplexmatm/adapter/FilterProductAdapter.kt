package com.playplexmatm.adapter

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.playplexmatm.R
import com.playplexmatm.model.productfilter.ProductFilterModel
import kotlinx.android.synthetic.main.adp_prodctfilter.*
import kotlinx.android.synthetic.main.layout_edit_product.*
import kotlinx.android.synthetic.main.layout_list_category.view.*
import kotlinx.android.synthetic.main.layout_listview_bottomsheet.view.*
import java.text.DecimalFormat


class FilterProductAdapter(

    val mContext: Context,
    val productName: String,
    val productImage: String,
    val filterProductList: ArrayList<ProductFilterModel>,
    val onclick: onClick,
    val taxStringList : ArrayList<String>


) : RecyclerView.Adapter<FilterProductAdapter.Myviewholder>() {

    var bottomSheetDialog: BottomSheetDialog? = null
    var count: Int = 1
    lateinit var dialog: Dialog
    val context = mContext
    private var taxExlusiveArray : ArrayList<String>  = arrayListOf("Exclusive","Inclusive")
    var myPosition : Int = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Myviewholder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_list_product_filter, parent, false)

        return Myviewholder(view)
    }

    override fun onBindViewHolder(holder: FilterProductAdapter.Myviewholder, position: Int) {
        Glide.with(mContext).load(filterProductList[position].image_url).into(holder.ivImage)
        holder.tvName.text = filterProductList[position].name

        Log.e("position",filterProductList.size.toString())

        holder.tvUnitRate.text = mContext.resources.getString(R.string.Rupee)+" "+(DecimalFormat("##.##").format(filterProductList[position].single_unit_price)).toString()

        holder.tvQuantity.setText(filterProductList[position].quantity.toString())

        holder.tvDiscountTAG.setText("Discount ("+filterProductList[position].discount_rate+"%)")
        holder.tvDiscountRate.setText(mContext.resources.getString(R.string.Rupee)+" "+filterProductList[position].total_discount.toString())

        holder.ivDelete.setOnClickListener {
            onclick.onDeleteClick(filterProductList[position], position)
        }
        holder.tvTaxRate.text = (DecimalFormat("##.##").format(filterProductList[position].total_tax)).toString()
        holder.tvSubTotal.setText(mContext.resources.getString(R.string.Rupee)+" "+(DecimalFormat("##.##").format(filterProductList[position].single_unit_price * filterProductList[position].quantity)).toString())
        holder.tvTaxTAG.setText("Tax ("+filterProductList[position].tax_rate+"%)")

        holder.tvDelete.setOnClickListener {
            onclick.onDeleteClick(filterProductList[position], position)
        }
        
        holder.tvEdit.setOnClickListener {
            onclick.onEditClick(filterProductList[position], position)
        }
    }

    override fun getItemCount(): Int {
        return filterProductList.size
    }

    class Myviewholder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage = view.findViewById(R.id.ivImage) as AppCompatImageView
        val tvName = view.findViewById(R.id.tvName) as AppCompatTextView
        val tvAdd = view.findViewById(R.id.tvAdd) as AppCompatTextView
        val tvQuantity = view.findViewById(R.id.tvQuantity) as AppCompatTextView
        val tvDiscountRate = view.findViewById(R.id.tvDiscountRateRv) as AppCompatTextView
        val tvUnitRate = view.findViewById(R.id.tvUnitRateRv) as AppCompatTextView
        val tvMinus = view.findViewById(R.id.tvMinus) as AppCompatTextView
        val tvSubTotal = view.findViewById(R.id.tvSubTotal) as AppCompatTextView
        val tvTaxRate = view.findViewById(R.id.tvTaxRate) as AppCompatTextView
        val ivDelete = view.findViewById(R.id.ivDelete) as AppCompatImageView
        val ivEdit = view.findViewById(R.id.ivEdit) as AppCompatImageView
        val tvTaxTAG = view.findViewById(R.id.tvTaxTAG) as TextView
        val tvDiscountTAG = view.findViewById(R.id.tvDiscountTAG) as TextView
        val tvDelete = view.findViewById(R.id.tvDelete) as TextView
        val tvEdit = view.findViewById(R.id.tvEdit) as TextView

    }

    interface onClick {
        fun onAddClick(
            filterModel: ProductFilterModel,
            position: Int,
            tvQuantity: TextView,
            tvSubTotal: TextView
        )

        fun onMinusClick(
            filterModel: ProductFilterModel,
            position: Int,
            tvQuantity: TextView,
            tvSubTotal: TextView
        )

        fun onDeleteClick(filterModel: ProductFilterModel, position: Int)
        fun onEditClick(filterModel: ProductFilterModel, position: Int)
    }

    private fun showTaxExclusiveBottomSheet()
    {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.layout_listview_bottomsheet, null)

        val  listAdapter = ArrayAdapter<String>(mContext, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, taxExlusiveArray)

        view.lvCategory.adapter = listAdapter

        bottomSheetDialog = BottomSheetDialog(mContext)
        bottomSheetDialog!!.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        view.lvCategory.setOnItemClickListener { parent, view, position, id ->
            val element = listAdapter.getItem(position) // The item that was clicked

//            Toast.makeText(this,element,Toast.LENGTH_SHORT).show()

            dialog.acTaxExclusive.setText(element)

            bottomSheetDialog!!.dismiss()
        }

        bottomSheetDialog!!.show()

    }

}