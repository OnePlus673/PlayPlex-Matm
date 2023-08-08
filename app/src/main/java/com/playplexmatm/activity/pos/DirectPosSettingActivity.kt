package com.playplexmatm.activity.pos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.playplexmatm.R
import com.playplexmatm.adapter.possale.PosSaleAdapter
import com.playplexmatm.model.categoryofproducts.Items
import com.playplexmatm.model.categoryofproducts.Products
import com.playplexmatm.model.paymentmode.PaymentModeModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.util.DatabaseHelper
import kotlinx.android.synthetic.main.activity_direct_pos_setting.*
import kotlinx.android.synthetic.main.activity_direct_pos_setting.tvDiscountType
import kotlinx.android.synthetic.main.activity_new_pos_sale.*
import kotlinx.android.synthetic.main.layout_listview_bottomsheet.view.*
import java.util.ArrayList

class DirectPosSettingActivity : AppCompatActivity() {

    lateinit var mContext: DirectPosSettingActivity
    lateinit var ivBack: ImageView
    lateinit var rvProducts: RecyclerView
    lateinit var edtDiscount: EditText
    lateinit var edtShipping: EditText
    private var posProductModelArrayList: ArrayList<Items> = arrayListOf()
    private var posProductFilterModelArrayList: ArrayList<Items> = arrayListOf()
    lateinit var posSaleAdapter: PosSaleAdapter
    lateinit var tvGrandTotalValue: TextView
    lateinit var tvDiscountValue: TextView
    lateinit var tvShippingValue: TextView
    lateinit var pbLoadData: ProgressBar
    lateinit var acPaymentMode: AutoCompleteTextView
    private var paymentModeList: ArrayList<PaymentModeModel> = arrayListOf()
    private var paymentModeStringList: ArrayList<String> = arrayListOf()
    private var discountTypeList: ArrayList<String> = arrayListOf("%","\u20B9")
    lateinit var apiInterface: ApiInterface
    private var PaymentModeName: String = ""
    private var userId: String = ""
    lateinit var tvSave: TextView
    var bottomSheetDialogCategory : BottomSheetDialog? = null
    private var grandTotal: Double = 0.0
    lateinit var dataBaseHelper: DatabaseHelper
    private var categoryPassFilterProdcuctList: ArrayList<Products> = arrayListOf()

    private var discountTotal = 0.0
    private var discountNewTotal = 0.0
    private var shippingTotal = 0.0
    private var shippingNewTotal = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_direct_pos_setting)
        mContext = this


    }




    private fun showDiscountYpeBottomSheet() {
        val view: View = layoutInflater.inflate(R.layout.layout_listview_bottomsheet, null)

//        Toast.makeText(this,"Bottomsheet",Toast.LENGTH_SHORT).show()

        val  listAdapter = ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, discountTypeList)

        view.lvCategory.adapter = listAdapter

        bottomSheetDialogCategory = BottomSheetDialog(this)
        bottomSheetDialogCategory!!.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        view.lvCategory.setOnItemClickListener { parent, view, position, id ->
            val element = listAdapter.getItem(position) // The item that was clicked

//            Toast.makeText(this,element,Toast.LENGTH_SHORT).show()

            tvDiscountType.setText(element)

            bottomSheetDialogCategory!!.dismiss()

        }
        bottomSheetDialogCategory!!.show()

    }

}