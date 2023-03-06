package com.playplelx.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplelx.R
import com.playplelx.activity.saleList.AddEditSaleListActivity
import com.playplelx.adapter.FilterProductAdapter
import com.playplelx.model.customers.CustomerModel
import com.playplelx.model.product.ProductModel
import com.playplelx.model.productfilter.ProductFilterModel
import com.playplelx.model.taxes.TaxesModel
import com.playplelx.model.warehouse.WareHouseModel
import com.playplelx.network.ApiInterface
import com.playplelx.network.Apiclient
import com.playplelx.util.InternetConnection
import com.playplelx.util.Util
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class AddEditPurchaseListActivity : AppCompatActivity(),View.OnClickListener,FilterProductAdapter.onClick {

    lateinit var mContext: AddEditPurchaseListActivity

    lateinit var ivBack: AppCompatImageView
    lateinit var edtInvoiceNumber: EditText
    lateinit var acUserName: AutoCompleteTextView
    lateinit var acOrderTax: AutoCompleteTextView
    lateinit var acProduct: AutoCompleteTextView
    lateinit var acWareHouse: AutoCompleteTextView
    lateinit var edtDate: EditText
    private var userList: ArrayList<CustomerModel> = arrayListOf()
    private var userStringList: ArrayList<String> = arrayListOf()
    private var userId: String = ""

    private var wareHouseList: ArrayList<WareHouseModel> = arrayListOf()
    private var wareHouseStringList: ArrayList<String> = arrayListOf()
    private var wareHouseId: String = ""

    private var productId: String = ""
    private var productName: String = ""
    private var productImage: String = ""
    private var taxId: String = ""
    private var taxRate: String = ""
    private var taxAmount: String = ""
    private var productList: ArrayList<ProductModel> = arrayListOf()
    private var productStringList: ArrayList<String> = arrayListOf()
    private var filterProductList: ArrayList<ProductFilterModel> = arrayListOf()
    private var taxList: ArrayList<TaxesModel> = arrayListOf()
    private var taxStringList: ArrayList<String> = arrayListOf()

    lateinit var pbLoadData: ProgressBar
    lateinit var tvSave: TextView
    lateinit var apiInterface: ApiInterface
    private var selectedDate: String = ""
    lateinit var rvProducts: RecyclerView
    lateinit var edtDiscount: EditText
    lateinit var edtShipping: EditText
    lateinit var edtNotes: EditText
    lateinit var filterProductAdapter: FilterProductAdapter
    lateinit var productFilterModel: ProductFilterModel
    lateinit var tvTotal: TextView
    private var total = 0
    private var discountTotal = 0
    private var discountNewTotal = 0
    private var shippingTotal = 0
    private var shippingNewTotal = 0
    lateinit var tvDiscount: TextView
    lateinit var tvShippping: TextView
    lateinit var edtTerms: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_purchase_list)
        mContext=this
        initUI()
        addListner()
    }
    private fun initUI() {
        supportActionBar!!.hide()
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        ivBack = findViewById(R.id.ivBack)
        edtInvoiceNumber = findViewById(R.id.edtInvoiceNumber)
        acUserName = findViewById(R.id.acUserName)
        acProduct = findViewById(R.id.acProduct)
        edtDate = findViewById(R.id.edtDate)
        pbLoadData = findViewById(R.id.pbLoadData)
        tvSave = findViewById(R.id.tvSave)
        rvProducts = findViewById(R.id.rvProducts)
        acOrderTax = findViewById(R.id.acOrderTax)
        acWareHouse = findViewById(R.id.acWareHouse)
        edtDiscount = findViewById(R.id.edtDiscount)
        edtShipping = findViewById(R.id.edtShipping)
        edtNotes = findViewById(R.id.edtNotes)
        tvTotal = findViewById(R.id.tvTotal)
        tvDiscount = findViewById(R.id.tvDiscount)
        tvShippping = findViewById(R.id.tvShippping)
        edtTerms = findViewById(R.id.edtTerms)

        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallGetUserAPI()
            mNetworkCallGetProductAPI()
            mNetworkCallGetTaxAPI()
            mNetworkCallGetWareHouseAPI()
        } else {
            Toast.makeText(
                mContext, mContext.resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addListner() {
        ivBack.setOnClickListener(this)
        edtDate.setOnClickListener(this)
        tvSave.setOnClickListener(this)

        acUserName.setOnTouchListener { v, event ->
            acUserName.showDropDown()
            false
        }
        acProduct.setOnTouchListener { v, event ->
            acProduct.showDropDown()
            false
        }

        acOrderTax.setOnTouchListener { v, event ->
            acOrderTax.showDropDown()
            false
        }

        acWareHouse.setOnTouchListener { v, event ->
            acWareHouse.showDropDown()
            false
        }

        edtDiscount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty()) {
                    tvDiscount.text = s.toString()
                    discountTotal = tvDiscount.text.toString().toInt()
                    //  discountNewTotal = total - discountTotal
                    discountNewTotal = ((shippingTotal + total) - discountTotal)
                    tvTotal.text = discountNewTotal.toString()
                } else {
                    discountTotal = 0
                    discountNewTotal = 0
                    discountNewTotal = ((shippingTotal + total) - discountTotal)
                    tvTotal.text = discountNewTotal.toString()
                    tvDiscount.setText("0.00")
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
                    tvShippping.text = s.toString()
                    shippingTotal = edtShipping.text.toString().toInt()
                    shippingNewTotal = ((shippingTotal + total) - discountTotal)
                    tvTotal.text = shippingNewTotal.toString()
                } else {
                    shippingTotal = 0
                    shippingNewTotal = ((shippingTotal + total) - discountTotal)
                    tvTotal.text = shippingNewTotal.toString()
                    tvShippping.setText("0.00")
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

    }

    private fun showDatePicker(edtDate: EditText) {
        var mYear = 0
        var mMonth = 0
        var mDay = 0
        // Get Current Date
        val c = Calendar.getInstance()
        mYear = c[Calendar.YEAR]
        mMonth = c[Calendar.MONTH]
        mDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            this,
            { view, year, monthOfYear, dayOfMonth ->
                selectedDate =
                    year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString()
                edtDate.setText(year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString())
            },
            mYear,
            mMonth,
            mDay
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }


    private fun mNetworkCallGetUserAPI() {
        userList.clear()
        userStringList.clear()
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.getCustomerDropDown()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONArray("data")
                            userList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<CustomerModel?>?>() {}.type
                                )
                            )

                            setAdapter(userList)
                        } else {
                            Toast.makeText(
                                mContext,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            mContext,
                            mContext.resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject=JSONObject(response.errorBody()!!.string())
                        Util(mContext).logOutAlertDialog(mContext,JsonObject.optString("message"))
                    }catch (e: Exception){

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setAdapter(userList: ArrayList<CustomerModel>) {
        for (i in 0 until userList.size) {
            userStringList.add(userList[i].name)
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, userStringList)

        acUserName.threshold = 0 //will start working from first character

        acUserName.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView

        acUserName.setOnItemClickListener { parent, view, position, id ->
            userId = userList[position].xid
        }
    }


    private fun mNetworkCallGetWareHouseAPI() {
        wareHouseList.clear()
        wareHouseList.clear()
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.getWareHouseDropDown()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONArray("data")
                            wareHouseList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<WareHouseModel?>?>() {}.type
                                )
                            )

                            setwareHouseAdapter(wareHouseList)
                        } else {
                            Toast.makeText(
                                mContext,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            mContext,
                            mContext.resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject=JSONObject(response.errorBody()!!.string())
                        Util(mContext).logOutAlertDialog(mContext,JsonObject.optString("message"))
                    }catch (e: Exception){

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setwareHouseAdapter(wareHouseList: ArrayList<WareHouseModel>) {
        for (i in 0 until wareHouseList.size) {
            wareHouseStringList.add(wareHouseList[i].name)
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, wareHouseStringList)

        acWareHouse.threshold = 0 //will start working from first character

        acWareHouse.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView

        acWareHouse.setOnItemClickListener { parent, view, position, id ->
            wareHouseId = wareHouseList[position].xid
        }
    }


    private fun mNetworkCallGetProductAPI() {
        productList.clear()
        productStringList.clear()
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.getProductDropDown()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONArray("data")
                            productList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<ProductModel?>?>() {}.type
                                )
                            )

                            setProductAdapter(productList)
                        } else {
                            Toast.makeText(
                                mContext,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            mContext,
                            mContext.resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject=JSONObject(response.errorBody()!!.string())
                        Util(mContext).logOutAlertDialog(mContext,JsonObject.optString("message"))
                    }catch (e: Exception){

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setProductAdapter(productList: ArrayList<ProductModel>) {
        filterProductList.clear()
        for (i in 0 until productList.size) {
            productStringList.add(productList[i].name)
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, productStringList)

        acProduct.threshold = 0 //will start working from first character

        acProduct.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView

        acProduct.setOnItemClickListener { parent, view, position, id ->
            productId = productList[position].xid
            productName = productList[position].name
            productImage = productList[position].image_url
            productFilterModel = ProductFilterModel(
                productList[position].name,
                productList[position].image_url,
                "",
                productList[position].xid,
                0,
                0,
                productList[position].details.x_tax_id,
                productList[position].details.purchase_tax_type,
                productList[position].details.tax.rate,
                ((productList[position].details.sales_price.toInt() * productList[position].details.tax.rate) / 100),
                productList[position].x_unit_id,
                productList[position].details.sales_price,
                productList[position].details.sales_price,
                productList[position].details.sales_price,
                productList[position].details.current_stock,
                1
            )
            if (filterProductList.size > 0) {
                if (!filterProductList.contains(productFilterModel)) {

                    filterProductList.add(
                        ProductFilterModel(
                            productList[position].name,
                            productList[position].image_url,
                            "",
                            productList[position].xid,
                            0,
                            0,
                            productList[position].details.x_tax_id,
                            productList[position].details.purchase_tax_type,
                            productList[position].details.tax.rate,
                            ((productList[position].details.sales_price.toInt() * productList[position].details.tax.rate) / 100),
                            productList[position].x_unit_id,
                            productList[position].details.sales_price,
                            productList[position].details.sales_price,
                            productList[position].details.sales_price,
                            productList[position].details.current_stock,
                            1
                        )
                    )
                    total += productList[position].details.sales_price.toInt()
                    val disTotal = ((shippingTotal + total) - discountTotal)
                    tvTotal.setText(disTotal.toString())
                } else {
                    Toast.makeText(mContext, "Already added this items", Toast.LENGTH_SHORT).show()
                }
            } else {


                filterProductList.add(
                    ProductFilterModel(
                        productList[position].name,
                        productList[position].image_url,
                        "",
                        productList[position].xid,
                        0,
                        0,
                        productList[position].details.x_tax_id,
                        productList[position].details.purchase_tax_type,
                        productList[position].details.tax.rate,
                        ((productList[position].details.sales_price.toInt() * productList[position].details.tax.rate) / 100),
                        productList[position].x_unit_id,
                        productList[position].details.sales_price,
                        productList[position].details.sales_price,
                        productList[position].details.sales_price,
                        productList[position].details.current_stock,
                        1
                    )
                )
                total += productList[position].details.sales_price.toInt()
                val disTotal = ((shippingTotal + total) - discountTotal)
                tvTotal.setText(disTotal.toString())

            }


            setProductData(productName, productImage, filterProductList)
        }
    }

    private fun setProductData(
        productName: String,
        productImage: String,
        filterProductList: ArrayList<ProductFilterModel>
    ) {
        rvProducts.layoutManager = LinearLayoutManager(mContext)
        rvProducts.setHasFixedSize(true)
        rvProducts.isNestedScrollingEnabled = false
        filterProductAdapter =
            FilterProductAdapter(mContext, productName, productImage, filterProductList, this)
        rvProducts.adapter = filterProductAdapter
        filterProductAdapter.notifyDataSetChanged()

    }


    private fun mNetworkCallGetTaxAPI() {
        taxList.clear()
        taxStringList.clear()
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.getTaxesDropDown()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONArray("data")
                            taxList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<TaxesModel?>?>() {}.type
                                )
                            )

                            setTaxAdapter(taxList)
                        } else {
                            Toast.makeText(
                                mContext,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            mContext,
                            mContext.resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject=JSONObject(response.errorBody()!!.string())
                        Util(mContext).logOutAlertDialog(mContext,JsonObject.optString("message"))
                    }catch (e: Exception){

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setTaxAdapter(taxList: ArrayList<TaxesModel>) {
        for (i in 0 until taxList.size) {
            taxStringList.add(taxList[i].name)
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, taxStringList)

        acOrderTax.threshold = 0 //will start working from first character

        acOrderTax.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView

        acOrderTax.setOnItemClickListener { parent, view, position, id ->
            taxId = taxList[position].xid
            taxRate = taxList[position].rate
            taxAmount = taxList[position].rate
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.tvSave -> {
                if (InternetConnection.checkConnection(mContext)) {
                    if (isValidate()) {
                        mNetworkCallAddPurchaseAPI()
                    }
                } else {
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.str_check_internet_connections),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            R.id.edtDate -> {
                showDatePicker(edtDate)
            }
        }
    }

    private fun isValidate(): Boolean {
        var isValid = true
        when {
            edtInvoiceNumber.text.toString().trim().isEmpty() -> {
                Toast.makeText(mContext, "Please enter invoice number", Toast.LENGTH_SHORT).show()
                isValid = false
            }
            userId.isEmpty() -> {
                Toast.makeText(mContext, "Please select customer", Toast.LENGTH_SHORT).show()
                isValid = false
            }
            wareHouseId.isEmpty() -> {
                Toast.makeText(mContext, "Please select warehouse", Toast.LENGTH_SHORT).show()
                isValid = false
            }
            edtDate.text.toString().trim().isEmpty() -> {
                Toast.makeText(mContext, "Please select order date", Toast.LENGTH_SHORT).show()
                isValid = false
            }
            productId.isEmpty() -> {
                Toast.makeText(mContext, "Please select product", Toast.LENGTH_SHORT).show()
                isValid = false
            }
            taxId.isEmpty() -> {
                Toast.makeText(mContext, "Please select order id", Toast.LENGTH_SHORT).show()
                isValid = false
            }
            edtDiscount.text.toString().trim().isEmpty() -> {
                Toast.makeText(mContext, "Please enter discount", Toast.LENGTH_SHORT).show()
                isValid = false
            }
            edtShipping.text.toString().trim().isEmpty() -> {
                Toast.makeText(mContext, "Please enter shipping", Toast.LENGTH_SHORT).show()
                isValid = false
            }
            edtNotes.text.toString().trim().isEmpty() -> {
                Toast.makeText(mContext, "Please enter notes", Toast.LENGTH_SHORT).show()
                isValid = false
            }
            edtTerms.text.toString().trim().isEmpty() -> {
                Toast.makeText(mContext, "Please enter terms & conditions", Toast.LENGTH_SHORT)
                    .show()
                isValid = false
            }
        }
        return isValid
    }

    private fun mNetworkCallAddPurchaseAPI() {
        pbLoadData.visibility = View.VISIBLE


        val jsonArray = JSONArray()
        for (i in 0 until filterProductList.size) {
            val element = JSONObject()
            element.put("item_id", filterProductList.get(i).item_id)
            element.put("xid", filterProductList.get(i).xid)
            element.put("discount_rate", edtDiscount.text.toString().trim())
            element.put("total_discount", filterProductList.get(i).total_discount)
            element.put("x_tax_id", taxId)
            element.put("tax_type", "")
            element.put("tax_rate", taxRate)
            element.put("total_tax", "")
            element.put("x_unit_id", filterProductList.get(i).x_unit_id)
            element.put("unit_price", filterProductList.get(i).unit_price)
            element.put("single_unit_price", filterProductList.get(i).single_unit_price)
            element.put("subtotal", filterProductList.get(i).single_unit_price)
            element.put("quantity", filterProductList.get(i).quantity)
            jsonArray.put(element)
        }

        Log.e("JSONARRAYVAALUE", "=" + Gson().toJson(jsonArray))


        val call = apiInterface.addPurchases(
            "purchases",
            edtInvoiceNumber.text.toString().trim(),
            edtDate.text.toString().trim(),
            wareHouseId,
            userId,
            edtTerms.text.toString()
                .trim(),
            edtNotes.text.toString().trim(),
            "delivered",
            taxId,
            taxRate,
            taxAmount,
            edtDiscount.text.toString().trim(),
            edtShipping.text.toString().trim(),
            tvTotal.text.toString().trim(),
            tvTotal.text.toString().trim(),
            filterProductList.size.toString(),
            jsonArray
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            Toast.makeText(
                                mContext,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent()
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        } else {
                            Toast.makeText(
                                mContext,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            mContext,
                            mContext.resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject=JSONObject(response.errorBody()!!.string())
                        Util(mContext).logOutAlertDialog(mContext,JsonObject.optString("message"))
                    }catch (e: Exception){

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })


    }

    override fun onAddClick(
        filterModel: ProductFilterModel,
        position: Int,
        tvQuantity: TextView,
        tvSubTotal: TextView
    ) {
        // countQuantity++
        filterModel.quantity++
        if (filterModel.quantity <= filterModel.current_stock) {
            tvQuantity.text = filterModel.quantity.toString()
            val subtotal = (filterModel.unit_price.toInt() * filterModel.quantity)
            tvSubTotal.setText(subtotal.toString())
            total += filterModel.unit_price.toInt()
            val disTotal = ((shippingTotal + total) - discountTotal)
            tvTotal.setText(disTotal.toString())
            filterModel.single_unit_price = subtotal
        } else {
            Toast.makeText(mContext, "item stock is finished", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMinusClick(
        filterModel: ProductFilterModel,
        position: Int,
        tvQuantity: TextView,
        tvSubTotal: TextView
    ) {

        if (filterModel.quantity > 1) {
            filterModel.quantity--
            tvQuantity.setText(filterModel.quantity.toString())

            val subtotal = filterModel.unit_price.toInt() * filterModel.quantity
            tvSubTotal.setText(subtotal.toString())

            filterModel.single_unit_price = subtotal

            (total - filterModel.unit_price.toInt()).also { total = it }
            val disTotal = ((shippingTotal + total) - discountTotal)
            tvTotal.setText(disTotal.toString())
        }
    }

    override fun onDeleteClick(filterModel: ProductFilterModel, position: Int) {

        total = 0


        if (filterProductList.size > 0) {
            filterModel == null
            filterProductList.removeAt(position)

            filterProductAdapter.notifyDataSetChanged()

            for (i in 0 until filterProductList.size) {
                total = total + filterProductList[i].single_unit_price.toInt()
            }
            val disTotal = ((shippingTotal + total) - discountTotal)
            tvTotal.setText(disTotal.toString())
            //     (total- filterModel.unit_price.toInt()).also { total = it }
            //    tvTotal.setText(total.toString())
        }

    }
}