package com.playplelx.activity.pos

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
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplelx.R
import com.playplelx.activity.MainActivity
import com.playplelx.adapter.possale.PosSaleAdapter
import com.playplelx.model.categoryofproducts.Items
import com.playplelx.model.paymentmode.PaymentModeModel
import com.playplelx.model.posproducts.ProductData
import com.playplelx.network.ApiInterface
import com.playplelx.network.Apiclient
import com.playplelx.util.DatabaseHelper
import com.playplelx.util.PrefManager
import com.playplelx.util.Util
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
    lateinit var apiInterface: ApiInterface
    private var PaymentModeName: String = ""
    private var userId: String = ""
    lateinit var tvSave: TextView

    private var grandTotal: Double = 0.0
    lateinit var dataBaseHelper: DatabaseHelper

    private var discountTotal = 0.0
    private var discountNewTotal = 0.0
    private var shippingTotal = 0.0
    private var shippingNewTotal = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_pos_sale)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        supportActionBar!!.hide()
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

        if (intent.extras != null) {
            posProductModelArrayList =
                (intent.getSerializableExtra("posProductModel") as ArrayList<Items>?)!!
            grandTotal = intent.getDoubleExtra("totalvalue", 0.0)
            userId = intent.getStringExtra("userId")!!
            posProductFilterModelArrayList = posProductModelArrayList
            tvGrandTotalValue.text = grandTotal.toString()
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
        ivBack.setOnClickListener(this)
        tvSave.setOnClickListener(this)

        acPaymentMode.setOnTouchListener { v, event ->
            acPaymentMode.showDropDown()
            false
        }


        edtDiscount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty()) {
                    tvDiscountValue.text = s.toString()
                    discountTotal = tvDiscountValue.text.toString().toDouble()
                    //  discountNewTotal = total - discountTotal
                    discountNewTotal = ((shippingTotal + grandTotal) - discountTotal)
                    tvGrandTotalValue.text = discountNewTotal.toString()
                } else {
                    discountTotal = 0.0
                    discountNewTotal = 0.0
                    discountNewTotal = ((shippingTotal + grandTotal) - discountTotal)
                    tvGrandTotalValue.text = discountNewTotal.toString()
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
                    shippingNewTotal = ((shippingTotal + grandTotal) - discountTotal)
                    tvGrandTotalValue.text = shippingNewTotal.toString()
                } else {
                    shippingTotal = 0.0
                    shippingNewTotal = ((shippingTotal + grandTotal) - discountTotal)
                    tvGrandTotalValue.text = shippingNewTotal.toString()
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
                        .putExtra("PaymontModeAmount", grandTotal)
                        .putExtra("dueAmount",grandTotal)
                )

                /*if (InternetConnection.checkConnection(mContext)) {
                    if (isValidate()) {
                        mNetworkCallPosSaveAPI()
                    }
                } else {
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.str_check_internet_connections),
                        Toast.LENGTH_SHORT
                    ).show()
                }*/
            }
        }
    }

    override fun onAddClick(
        productData: Items, position: Int, tvQuantity: TextView, tvSubTotal: TextView
    ) {
        if (dataBaseHelper.CheckOrderExists(productData.xid, productData.x_unit_id)
                .toDouble() < productData.stock_quantity.toDouble()
        ) {
            productData.quantity++
            tvQuantity.text = dataBaseHelper.AddUpdateOrder(
                productData.xid,
                productData.x_unit_id,
                true,
                this,
                false,
                productData.single_unit_price.toDouble(),
                productData.name
            )
            productData.subtotal = productData.quantity * productData.single_unit_price
            val displaytotal: Double = dataBaseHelper.getTotalCartAmt(PrefManager(mContext))
            val sumTotal = DatabaseHelper.decimalformatData.format(displaytotal)
            grandTotal = sumTotal.toDouble()
            tvGrandTotalValue.text = grandTotal.toString()


            posSaleAdapter.notifyDataSetChanged()

        } else Toast.makeText(
            this, "Stock limit alert", Toast.LENGTH_SHORT
        ).show()

    }

    override fun onMinusClick(
        filterModel: Items, position: Int, tvQuantity: TextView, tvSubTotal: TextView
    ) {
        if (filterModel.quantity > 1) {

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

    override fun onDeleteClick(filterModel: ProductData, position: Int) {
        grandTotal = 0.0


        if (posProductFilterModelArrayList.size > 0) {
            filterModel.isSelected = false
            posProductFilterModelArrayList.removeAt(position)
            posSaleAdapter.notifyDataSetChanged()

            for (i in 0 until posProductFilterModelArrayList.size) {
                grandTotal += posProductFilterModelArrayList[i].single_unit_price.toDouble()
            }
            val disTotal = ((shippingTotal + grandTotal) - discountTotal)
            tvGrandTotalValue.setText(disTotal.toString())
        }
    }


    private fun mNetworkCallPosSaveAPI() {
        val paymentArray = JSONArray()
        val detailsArray = JSONObject()
        detailsArray.put("discount_type", "percentage")
        detailsArray.put("discount_value", 0.0)
        if (edtDiscount.text.toString().trim().isEmpty()) {
            detailsArray.put("discount", 0.0)
        } else {
            detailsArray.put("discount", edtDiscount.text.toString().trim().toFloat())
        }

        if (edtShipping.text.toString().trim().isEmpty()) {
            detailsArray.put("shipping", 0.0)

        } else {
            detailsArray.put("shipping", edtShipping.text.toString().trim().toFloat())

        }


        detailsArray.put("subtotal", tvGrandTotalValue.text.toString().trim().toFloat())
        detailsArray.put("user_id", userId)
        detailsArray.put("tax_rate", 0.0)
        detailsArray.put("tax_amount", 0.0)
        /*      val hashMap: HashMap<String, String> = hashMapOf()
              hashMap["discount_type"] = "percentage"
              hashMap["discount_value"] = "0"
              hashMap["discount"] = edtDiscount.text.toString().trim()
              hashMap["shipping"] = edtShipping.text.toString().trim()
              hashMap["subtotal"] = tvGrandTotalValue.text.toString().trim()
              hashMap["user_id"] = userId
              hashMap["tax_rate"] = "0"
              hashMap["tax_amount"] = "0"*/
        //   val entries: Set<Map.Entry<String, String>> = hashMap.entries
        // val details = JSONArray(entries)
        //detailsArray.put(hashMap)

        Log.e("details", "=" + detailsArray)
        Log.e("paymentArray", "=" + paymentArray)


        val jsonArray = JSONArray()
        for (i in 0 until posProductFilterModelArrayList.size) {
            val element = JSONObject()
            element.put("item_id", posProductFilterModelArrayList.get(i).item_id)
            element.put("xid", posProductFilterModelArrayList.get(i).xid)
            if (edtDiscount.text.toString().trim().isEmpty()) {
                element.put("discount_rate", 0.0)
            } else {
                element.put("discount_rate", edtDiscount.text.toString().trim().toFloat())
            }

            if (edtDiscount.text.toString().trim().isEmpty()) {
                element.put("total_discount", 0.0)
            } else {
                element.put(
                    "total_discount", posProductFilterModelArrayList.get(i).total_discount.toFloat()
                )
            }

            element.put("x_tax_id", "")
            element.put("tax_type", "")
            element.put("tax_rate", 0.0)
            element.put("total_tax", 0.0)
            element.put("x_unit_id", posProductFilterModelArrayList.get(i).x_unit_id)
            element.put("unit_price", posProductFilterModelArrayList.get(i).unit_price.toFloat())
            element.put(
                "single_unit_price",
                posProductFilterModelArrayList.get(i).single_unit_price.toFloat()
            )
            element.put(
                "subtotal", posProductFilterModelArrayList.get(i).single_unit_price.toFloat()
            )
            element.put("quantity", posProductFilterModelArrayList.get(i).quantity.toFloat())
            jsonArray.put(element)
        }

        Log.e("JSONARRAYVAALUE", "=" + Gson().toJson(jsonArray))

        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.posSave(paymentArray, detailsArray, jsonArray)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            Toast.makeText(
                                mContext, jsonObject.optString("message"), Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(mContext, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                mContext, jsonObject.optString("message"), Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        pbLoadData.visibility = View.GONE
                        Toast.makeText(
                            mContext,
                            mContext.resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
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