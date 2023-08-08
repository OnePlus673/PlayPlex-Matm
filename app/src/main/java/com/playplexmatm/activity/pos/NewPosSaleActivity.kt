package com.playplexmatm.activity.pos

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplexmatm.R
import com.playplexmatm.adapter.possale.PosSaleAdapter
import com.playplexmatm.model.categoryofproducts.Items
import com.playplexmatm.model.categoryofproducts.Products
import com.playplexmatm.model.paymentmode.PaymentModeModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.DatabaseHelper
import com.playplexmatm.util.PrefManager
import com.playplexmatm.util.Util
import kotlinx.android.synthetic.main.activity_new_pos_sale.*
import kotlinx.android.synthetic.main.fragment_pricing.view.*
import kotlinx.android.synthetic.main.layout_list_category.view.*
import kotlinx.android.synthetic.main.layout_listview_bottomsheet.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.*

class NewPosSaleActivity : AppCompatActivity(), View.OnClickListener, PosSaleAdapter.onClick {

    lateinit var mContext: NewPosSaleActivity
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

    lateinit var from: String
    var subtotal: Double = 00.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_pos_sale)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        //supportActionBar!!.hide()
        dataBaseHelper = DatabaseHelper(mContext)
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        ivBack = findViewById(R.id.ivBack)
        rvProducts = findViewById(R.id.rvProducts)
        edtDiscount = findViewById(R.id.edtDiscount)
        edtShipping = findViewById(R.id.edtShipping)
        tvGrandTotalValue = findViewById(R.id.tvGrandTotalValue)
        tvDiscountValue = findViewById(R.id.tvDiscountValue)
        tvShippingValue = findViewById(R.id.tvShippingValue)
        acPaymentMode = findViewById(R.id.acPaymentMode)
        pbLoadData = findViewById(R.id.pbLoadData)
        tvSave = findViewById(R.id.tvSave)

        tvDiscountValue.setOnClickListener {
            showDiscountYpeBottomSheet()
            Toast.makeText(this, "1", Toast.LENGTH_SHORT).show()
        }

        if (intent.extras != null) {
            posProductModelArrayList =
                (intent.getSerializableExtra("posProductModel") as ArrayList<Items>?)!!
            grandTotal = intent.getDoubleExtra("totalvalue", 0.0)
            subtotal = intent.getDoubleExtra("totalvalue", 0.0)
            userId = intent.getStringExtra("userId")!!
            posProductFilterModelArrayList = posProductModelArrayList
            tvGrandTotalValue.text =  DecimalFormat("##.#").format(grandTotal).toString()
            from = intent.getStringExtra("from")!!

            Log.e("intent","inside intent")
            Log.e("array size",posProductModelArrayList.size.toString())

            for(i in 0 until posProductModelArrayList.size)
            {
                val posproduct = posProductFilterModelArrayList.get(i)
                Log.e("posproduct",posproduct.name.toString())
            }

        }

        if(from.equals("direct")) {
            rvProducts.visibility = View.GONE
        }
        setData()
    }

    private fun setData() {

        rvProducts.layoutManager = LinearLayoutManager(mContext)
        rvProducts.setHasFixedSize(true)
        posSaleAdapter = PosSaleAdapter(mContext, posProductModelArrayList, this)
        rvProducts.adapter = posSaleAdapter
        posSaleAdapter.notifyDataSetChanged()
    }

    private fun addListner() {

        tvDiscountType.setOnClickListener {
            showDiscountYpeBottomSheet()
        }

        ivBack.setOnClickListener(this)
        tvSave.setOnClickListener(this)

        acPaymentMode.setOnTouchListener { v, event ->
            acPaymentMode.showDropDown()
            false
        }


        tvDiscountType.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty()) {

                    Log.e("S value",s.toString())

                    if(s.toString().equals("%"))
                    {
//                        discountTotal = (grandTotal * tvDiscountValue.text.toString().toDouble()) / 100
                        discountNewTotal = (shippingTotal + grandTotal) - (grandTotal * tvDiscountValue.text.toString().toDouble()) / 100
                    }
                    else
                    {
                        discountTotal = edtDiscount.text.toString().toDouble()
                        discountNewTotal = ((shippingTotal + grandTotal) - discountTotal)

                    }
                    tvGrandTotalValue.text =  DecimalFormat("##.#").format(discountNewTotal).toString()
                } else {
                    discountTotal = 0.0
                    discountNewTotal = 0.0
                    discountNewTotal = ((shippingTotal + grandTotal) - discountTotal)
                    tvGrandTotalValue.text =  DecimalFormat("##.#").format(discountNewTotal).toString()
                    tvDiscountValue.text = "0.00"
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        edtDiscount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty()) {

                    tvDiscountValue.text = s.toString()
                    discountTotal = tvDiscountValue.text.toString().toDouble()
                    //  discountNewTotal = total - discountTotal

                    if(tvDiscountType.text.equals("%"))
                    {
                        discountTotal = (grandTotal * tvDiscountValue.text.toString().toDouble()) / 100
                        Log.e("Disc Percent",discountTotal.toString())

                        discountNewTotal = (shippingTotal + grandTotal) - (grandTotal * tvDiscountValue.text.toString().toDouble()) / 100
                    }
                    else
                    {
                        discountNewTotal = ((shippingTotal + grandTotal) - discountTotal)
                    }
                    tvGrandTotalValue.text =  DecimalFormat("##.#").format(discountNewTotal).toString()

                } else {
                    discountTotal = 0.0
                    discountNewTotal = 0.0
                    discountNewTotal = ((shippingTotal + grandTotal) - discountTotal)
                    tvGrandTotalValue.text =  DecimalFormat("##.#").format(discountNewTotal).toString()
                    tvDiscountValue.text = "0.00"
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        edtShipping.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty()) {
                    tvShippingValue.text = s.toString()
                    shippingTotal = edtShipping.text.toString().toDouble()
                    Log.e("Discount",discountTotal.toString())
                    shippingNewTotal = ((shippingTotal + grandTotal) - discountTotal)
                    tvGrandTotalValue.text =  DecimalFormat("##.#").format(shippingNewTotal).toString()

                } else {
                    Log.e("Discount 1",discountTotal.toString())

                    shippingTotal = 0.0
                    shippingNewTotal = ((shippingTotal + grandTotal) - discountTotal)
                    tvGrandTotalValue.text =  DecimalFormat("##.#").format(shippingNewTotal).toString()
                    tvShippingValue.text = "0.00"
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })


    }


    private fun mNetworkCallGetPaymentModeAPI() {
        paymentModeList.clear()
        paymentModeStringList.clear()
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.getPaymentModeDropDown()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONArray("data")
                            paymentModeList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<PaymentModeModel?>?>() {}.type
                                )
                            )

                            setPaymentAdapter(paymentModeList)
                        } else {
                            Toast.makeText(
                                mContext, jsonObject.optString("message"), Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            mContext,
                            mContext.resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject = JSONObject(response.errorBody()!!.string())
                        Util(mContext).logOutAlertDialog(mContext, JsonObject.optString("message"))
                    } catch (e: Exception) {

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setPaymentAdapter(paymentList: ArrayList<PaymentModeModel>) {
        for (i in 0 until paymentList.size) {
            paymentModeStringList.add(paymentList[i].name)
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, paymentModeStringList)

        acPaymentMode.threshold = 0 //will start working from first character

        acPaymentMode.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView

        acPaymentMode.setOnItemClickListener { parent, view, position, id ->
            PaymentModeName = paymentList[position].xid
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                startActivity(
                    Intent(mContext, PosSettingActivity::class.java).putExtra(
                        "posProductModel",
                        posProductModelArrayList
                    ).putExtra("totalvalue", grandTotal)
                )

            }
            R.id.tvSave -> {

                startActivity(
                    Intent(mContext, PaymentModeActivity::class.java)
                        .putExtra(
                            "PaymontModeAmount",
                            tvGrandTotalValue.text.toString().trim().toDouble()
                        )
                        .putExtra(
                            "posProductModel", posProductModelArrayList
                        )
                        .putExtra("dueAmount", tvGrandTotalValue.text.toString().trim().toDouble())
                        .putExtra("userId", userId)
                        .putExtra("discount", edtDiscount.text.toString().trim())
                        .putExtra("shipping", edtShipping.text.toString().trim())
                        .putExtra("from",from)
                        .putExtra("subtotal",subtotal)
                )
                finish()

            }
        }
    }

    override fun onAddClick(
        productData: Items, position: Int, tvQuantity: TextView, tvSubTotal: TextView
    ) {
        if (dataBaseHelper.CheckOrderExists(productData.xid, productData.x_unit_id)
                .toDouble() < productData.stock_quantity.toDouble()
        ) {
            Log.e("Quant",productData.quantity.toString())
            productData.quantity++
            tvQuantity.text = dataBaseHelper.AddUpdateOrder(
                productData.xid,
                productData.x_unit_id,
                true,
                this,
                false,
                productData.single_unit_price,
                productData.name
            )
            productData.subtotal = productData.quantity * productData.single_unit_price
            val displaytotal: Double = dataBaseHelper.getTotalCartAmt(PrefManager(mContext))
            val sumTotal = DatabaseHelper.decimalformatData.format(displaytotal)
            grandTotal = sumTotal.toDouble()

            tvGrandTotalValue.text = grandTotal.toString()


            discountTotal = 0.0
            discountNewTotal = 0.0
            tvDiscountValue.text = "0.00"

            shippingTotal = 0.0
            tvShippingValue.text = "0.00"


            edtDiscount.setText("")
            edtShipping.setText("")

            posSaleAdapter.notifyDataSetChanged()

        } else Toast.makeText(
            this, "Stock limit alert", Toast.LENGTH_SHORT
        ).show()

    }

    override fun onMinusClick(
        filterModel: Items, position: Int, tvQuantity: TextView, tvSubTotal: TextView
    ) {
        Log.e("Quantity 1",dataBaseHelper.CheckOrderExists(filterModel.xid,filterModel.x_unit_id))

        val quantity = (dataBaseHelper.CheckOrderExists(filterModel.xid,filterModel.x_unit_id)).toInt()

        if (quantity > 1) {

            Log.e("Quantity 2",filterModel.quantity.toString())
            filterModel.quantity--

            tvQuantity.setText(
                dataBaseHelper.AddUpdateOrder(
                    filterModel.xid,
                    filterModel.x_unit_id,
                    false,
                    this,
                    false,
                    filterModel.single_unit_price.toDouble(),
                    filterModel.name
                )
            )

            filterModel.subtotal = filterModel.quantity * filterModel.single_unit_price
            val displaytotal: Double = dataBaseHelper.getTotalCartAmt(PrefManager(mContext))
            val sumTotal = DatabaseHelper.decimalformatData.format(displaytotal)
            grandTotal = sumTotal.toDouble()
            tvGrandTotalValue.text = grandTotal.toString()

            discountTotal = 0.0
            discountNewTotal = 0.0
            tvDiscountValue.text = "0.00"

            shippingTotal = 0.0
            tvShippingValue.text = "0.00"

            edtDiscount.setText("")
            edtShipping.setText("")

            posSaleAdapter.notifyDataSetChanged()
        }


    }

/*
    override fun onAddClick(
        filterModel: ProductData,
        position: Int,
        tvQuantity: TextView,
        tvSubTotal: TextView
    ) {
        if (filterModel.quantity <= filterModel.stock_quantity) {
            filterModel.quantity++
            tvQuantity.text = filterModel.quantity.toString()
            val subtotal = (filterModel.unit_price.toInt() * filterModel.quantity)
            tvSubTotal.text = subtotal.toString()
            grandTotal += filterModel.unit_price.toInt()
            val disTotal = ((shippingTotal + grandTotal) - discountTotal)
            tvGrandTotalValue.text = disTotal.toString()
            filterModel.single_unit_price = subtotal
        } else {
            Toast.makeText(mContext, "item stock is finished", Toast.LENGTH_SHORT).show()
        }
    }
*/

/*    override fun onMinusClick(
        filterModel: ProductData,
        position: Int,
        tvQuantity: TextView,
        tvSubTotal: TextView
    ) {
        if (filterModel.quantity > 1) {
            filterModel.quantity--
            tvQuantity.text = filterModel.quantity.toString()

            val subtotal = filterModel.unit_price.toInt() * filterModel.quantity
            tvSubTotal.text = subtotal.toString()

            filterModel.single_unit_price = subtotal

            (grandTotal - filterModel.unit_price.toInt()).also { grandTotal = it }
            val disTotal = ((shippingTotal + grandTotal) - discountTotal)
            tvGrandTotalValue.text = disTotal.toString()
        }
    }*/

    private fun showDiscountYpeBottomSheet()
    {
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



    override fun onDeleteClick(filterModel: Items, position: Int) {
        grandTotal = 0.0

        if (posProductFilterModelArrayList.size > 0) {
            filterModel.isSelected = false


            dataBaseHelper.DeleteOrderData(posProductFilterModelArrayList[position].xid,
                posProductFilterModelArrayList[position].x_unit_id)


            posProductFilterModelArrayList.removeAt(position)
            posSaleAdapter.notifyDataSetChanged()

//            for (i in 0 until posProductFilterModelArrayList.size) {
//                grandTotal += posProductFilterModelArrayList[i].single_unit_price.toDouble()
//            }
//            val disTotal = ((shippingTotal + grandTotal) - discountTotal)
//            tvGrandTotalValue.setText(disTotal.toString())

            val displaytotal: Double = dataBaseHelper.getTotalCartAmt(PrefManager(mContext))
            val sumTotal = DatabaseHelper.decimalformatData.format(displaytotal)
            val total = sumTotal.toDouble()
            grandTotal = total
            tvGrandTotalValue.setText(total.toString())
//
            discountTotal = 0.0
            discountNewTotal = 0.0
            tvDiscountValue.text = "0.00"


            shippingTotal = 0.0
            tvShippingValue.text = "0.00"

            edtDiscount.setText("")
            edtShipping.setText("")

        }
    }


    private fun isValidate(): Boolean {
        var isValid = true
        if (posProductModelArrayList.size == 0) {
            Toast.makeText(mContext, "please select products", Toast.LENGTH_SHORT).show()
            isValid = false
        } /*else if (PaymentModeName.isEmpty()) {
            Toast.makeText(mContext, "Please select payment Mode", Toast.LENGTH_SHORT).show()
            isValid = false
        }*/
        return isValid
    }
}