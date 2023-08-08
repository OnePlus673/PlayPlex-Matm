package com.playplexmatm.activity.pos.saleList

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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplexmatm.R
import com.playplexmatm.activity.itemlist.AddEditProductActivity
import com.playplexmatm.adapter.FilterProductAdapter
import com.playplexmatm.model.customers.CustomerModel
import com.playplexmatm.model.product.ProductModel
import com.playplexmatm.model.productfilter.ProductFilterModel
import com.playplexmatm.model.saleList.SaleListModel
import com.playplexmatm.model.taxes.TaxesModel
import com.playplexmatm.model.warehouse.WareHouseModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.InternetConnection
import com.playplexmatm.util.Util
import kotlinx.android.synthetic.main.activity_add_edit_purchase_list.*
import kotlinx.android.synthetic.main.activity_add_edit_sale_list.*
import kotlinx.android.synthetic.main.activity_add_edit_sale_list.acOrderStatus
import kotlinx.android.synthetic.main.activity_add_edit_sale_list.ivAddProduct
import kotlinx.android.synthetic.main.activity_add_edit_sale_list.llNotes
import kotlinx.android.synthetic.main.activity_add_edit_sale_list.llTC
import kotlinx.android.synthetic.main.activity_add_edit_sale_list.tvAddNotes
import kotlinx.android.synthetic.main.activity_add_edit_sale_list.tvAddTC
import kotlinx.android.synthetic.main.activity_add_edit_sale_list.tvBasicAmount
import kotlinx.android.synthetic.main.activity_add_edit_sale_list.tvItemDiscount
import kotlinx.android.synthetic.main.activity_add_edit_sale_list.tvOrderTax
import kotlinx.android.synthetic.main.activity_add_edit_sale_list.tvRemoveNotes
import kotlinx.android.synthetic.main.activity_add_edit_sale_list.tvRemoveTC
import kotlinx.android.synthetic.main.activity_add_edit_sale_list.tvShipppingTotal
import kotlinx.android.synthetic.main.layout_list_bottomsheet_invoice_number.view.*
import kotlinx.android.synthetic.main.layout_list_category.view.*
import kotlinx.android.synthetic.main.layout_listview_bottomsheet.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddEditSaleListActivity : AppCompatActivity(), View.OnClickListener,
    FilterProductAdapter.onClick {

    lateinit var mContext: AddEditSaleListActivity
    lateinit var ivBack: AppCompatImageView
    lateinit var edtInvoiceNumber: TextView
    lateinit var acUserName: AutoCompleteTextView
//    lateinit var acOrderTax: AutoCompleteTextView
    lateinit var acProduct: AutoCompleteTextView
//    lateinit var acWareHouse: AutoCompleteTextView
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
    var bottomSheetDialog: BottomSheetDialog? = null
    private var orderStatusList: ArrayList<String> = arrayListOf("Recieved","Pending","Ordered")
    lateinit var saleModel : SaleListModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_sale_list)
        mContext = this
        initUI()
        addListner()

        val bundle = intent.extras
        if(bundle != null) {
            saleModel = bundle.getSerializable("model") as SaleListModel

            edtInvoiceNumber.setText(saleModel.invoice_number)
            edtDate.setText(convertDate(saleModel.order_date))
            acSupplier.setText(saleModel.user.name)

            acOrderStatus.setText(saleModel.order_status)
            edtDiscount.setText(saleModel.discount)
            edtShipping.setText(saleModel.shipping)
            edtTerms.setText(saleModel.terms_condition)

            tvShippping.setText(saleModel.shipping)
            tvDiscount.setText(saleModel.discount)
            tvOrderTax.setText(saleModel.tax_amount)
            tvTotal.setText(saleModel.subtotal)

            /* for(i in 0 until purchaseModel.items.size) {

                 var x_tax_id = ""

                 try {
                     x_tax_id = purchaseModel.items[i].product.details.x_tax_id
                 } catch (e: Exception) {
                     x_tax_id = ""
                 }

                 try {
                     filterProductList.add(
                         ProductFilterModel(
                             purchaseModel.items[i].product.name,
                             purchaseModel.items[i].product.image_url,
                             "",
                             purchaseModel.items[i].xid,
                             0,
                             0.0,
                             x_tax_id,
                             purchaseModel.items[i].tax_type,
                             purchaseModel.items[i].tax_rate.toInt(),
                             ((purchaseModel.items[i].single_unit_price.toInt() * purchaseModel.items[i].tax_rate.toInt()) / 100),
                             purchaseModel.items[i].x_product_id,
                             purchaseModel.items[i].single_unit_price.toInt(),
                             purchaseModel.items[i].single_unit_price.toInt(),
                             purchaseModel.items[i].single_unit_price.toInt(),
                             purchaseModel.items[i].product.details.current_stock.toInt(),
                             1
                         )
                     )
                 } catch(e : Exception) {
                     filterProductList.add(
                         ProductFilterModel(
                             purchaseModel.items[i].product.name,
                             purchaseModel.items[i].product.image_url,
                             "",
                             purchaseModel.items[i].xid,
                             0,
                             0.0,
                             "",
                             purchaseModel.items[i].tax_type,
                             purchaseModel.items[i].tax_rate.toInt(),
                             ((purchaseModel.items[i].single_unit_price.toInt() * purchaseModel.items[i].tax_rate.toInt()) / 100),
                             purchaseModel.items[i].x_product_id,
                             purchaseModel.items[i].single_unit_price.toInt(),
                             purchaseModel.items[i].single_unit_price.toInt(),
                             purchaseModel.items[i].single_unit_price.toInt(),
                             purchaseModel.items[i].product.details.current_stock.toInt(),
                             1
                         )
                     )
                 }

                 setProductData(purchaseModel.items[i].product.name, purchaseModel.items[i].product.image_url, filterProductList)
             }
 */
        }
    }

    private fun initUI() {
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        ivBack = findViewById(R.id.ivBack)
        edtInvoiceNumber = findViewById(R.id.edtInvoiceNumber)
        acUserName = findViewById(R.id.acUserName)
        acProduct = findViewById(R.id.acProduct)
        edtDate = findViewById(R.id.edtDate)
        pbLoadData = findViewById(R.id.pbLoadData)
        tvSave = findViewById(R.id.tvSave)
        rvProducts = findViewById(R.id.rvProducts)
//        acOrderTax = findViewById(R.id.acOrderTax)
//        acWareHouse = findViewById(R.id.acWareHouse)
        edtDiscount = findViewById(R.id.edtDiscount)
        edtShipping = findViewById(R.id.edtShipping)
        edtNotes = findViewById(R.id.edtNotes)
        tvTotal = findViewById(R.id.tvTotal)
        tvDiscount = findViewById(R.id.tvDiscount)
        tvShippping = findViewById(R.id.tvShipppingTotal)
        edtTerms = findViewById(R.id.edtTerms)

        lltvAddItem.setOnClickListener {
            lltvAddItem.visibility = View.GONE
            llAddItems.visibility = View.VISIBLE

        }

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

        ivAddProduct.setOnClickListener {
            val intent = Intent(this, AddEditProductActivity::class.java)
            startActivity(intent)
        }

        tvAddNotes.setOnClickListener {
            tvAddNotes.visibility = View.GONE
            tvRemoveNotes.visibility = View.VISIBLE
            llNotes.visibility = View.VISIBLE
        }

        tvRemoveNotes.setOnClickListener {
            tvAddNotes.visibility = View.VISIBLE
            tvRemoveNotes.visibility = View.GONE
            llNotes.visibility = View.GONE
        }


        tvAddTC.setOnClickListener {
            tvAddTC.visibility = View.GONE
            tvRemoveTC.visibility = View.VISIBLE
            llTC.visibility = View.VISIBLE
        }

        tvRemoveTC.setOnClickListener {
            tvAddTC.visibility = View.VISIBLE
            tvRemoveTC.visibility = View.GONE
            llTC.visibility = View.GONE
        }

        acOrderStatus.setOnClickListener {
            showOrderStatusBottomSheet()
        }

        acUserName.setOnTouchListener { v, event ->
            acUserName.showDropDown()
            false
        }
        acProduct.setOnTouchListener { v, event ->
            acProduct.showDropDown()
            false
        }

//        acOrderTax.setOnTouchListener { v, event ->
//            acOrderTax.showDropDown()
//            false
//        }

//        acWareHouse.setOnTouchListener { v, event ->
//            acWareHouse.showDropDown()
//            false
//        }

        edtDiscount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty()) {
                    tvDiscount.text = s.toString()
//                    discountTotal = tvDiscount.text.toString().toInt()
                    discountNewTotal = total - discountTotal
//                    discountNewTotal = ((shippingTotal + total) - discountTotal)
//
//                    tvTotal.text = discountNewTotal.toString()

                    tvTotal.text =  (tvBasicAmount.text.toString().toDouble() -
                            tvItemDiscount.text.toString().toDouble()
                            + tvOrderTax.text.toString().toDouble()
                            + edtShipping.text.toString().toDouble()
                            - s.toString().toDouble()).toString()

                } else {
//                    discountTotal = 0
//                    discountNewTotal = 0
//                    discountNewTotal = ((shippingTotal + total) - discountTotal)
//                    tvTotal.text = discountNewTotal.toString()
                    tvDiscount.setText("0")

                    tvTotal.text =  (tvBasicAmount.text.toString().toDouble() -
                            tvItemDiscount.text.toString().toDouble()
                            + tvOrderTax.text.toString().toDouble()
                            + edtShipping.text.toString().toDouble()).toString()

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
//                    shippingTotal = edtShipping.text.toString().toInt()
//                    shippingNewTotal = ((shippingTotal + total) - discountTotal)
//                    tvTotal.text = shippingNewTotal.toString()


                    tvTotal.text =  (tvBasicAmount.text.toString().toDouble() -
                            tvItemDiscount.text.toString().toDouble()
                            + tvOrderTax.text.toString().toDouble()
                            + s.toString().toDouble()
                            - edtDiscount.text.toString().toDouble()).toString()

                } else {
                    shippingTotal = 0
                    tvShippping.text = s.toString()

                    tvTotal.text =  (tvBasicAmount.text.toString().toDouble() -
                            tvItemDiscount.text.toString().toDouble()
                            + tvOrderTax.text.toString().toDouble()
                            - edtDiscount.text.toString().toDouble()).toString()


//                    shippingNewTotal = ((shippingTotal + total) - discountTotal)
//                    tvTotal.text = shippingNewTotal.toString()
//                    tvShippping.setText("0.00")
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

    private fun setwareHouseAdapter(wareHouseList: ArrayList<WareHouseModel>) {
        for (i in 0 until wareHouseList.size) {
            wareHouseStringList.add(wareHouseList[i].name)
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, wareHouseStringList)
/*
        acWareHouse.threshold = 0 //will start working from first character

        acWareHouse.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView

        acWareHouse.setOnItemClickListener { parent, view, position, id ->
            wareHouseId = wareHouseList[position].xid
        }

 */
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
                0.0,
                0.0,
                productList[position].details.x_tax_id,
                productList[position].details.purchase_tax_type,
                productList[position].details.tax.rate,
                (((productList[position].details.sales_price.toInt() * productList[position].details.tax.rate) / 100).toDouble()),
                productList[position].x_unit_id,
                productList[position].details.sales_price.toDouble(),
                productList[position].details.sales_price.toDouble(),
                productList[position].details.sales_price.toDouble(),
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
                            0.0,

                            0.0,
                            productList[position].details.x_tax_id,
                            productList[position].details.purchase_tax_type,
                            productList[position].details.tax.rate,
                            (((productList[position].details.sales_price.toInt() * productList[position].details.tax.rate) / 100).toDouble()),
                            productList[position].x_unit_id,
                            productList[position].details.sales_price.toDouble(),
                            productList[position].details.sales_price.toDouble(),
                            productList[position].details.sales_price.toDouble(),
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
                        0.0,
                        0.0,
                        productList[position].details.x_tax_id,
                        productList[position].details.purchase_tax_type,
                        productList[position].details.tax.rate,
                        (((productList[position].details.sales_price.toInt() * productList[position].details.tax.rate) / 100).toDouble()),
                        productList[position].x_unit_id,
                        productList[position].details.sales_price.toDouble(),
                        productList[position].details.sales_price.toDouble(),
                        productList[position].details.sales_price.toDouble(),
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
            FilterProductAdapter(mContext, productName, productImage, filterProductList, this,taxStringList)
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

    private fun setTaxAdapter(taxList: ArrayList<TaxesModel>) {
        for (i in 0 until taxList.size) {
            taxStringList.add(taxList[i].name)
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, taxStringList)
/*
        acOrderTax.threshold = 0 //will start working from first character

        acOrderTax.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView

        acOrderTax.setOnItemClickListener { parent, view, position, id ->
            taxId = taxList[position].xid
            taxRate = taxList[position].rate
            taxAmount = taxList[position].rate
        }*/
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.tvSave -> {
                if (InternetConnection.checkConnection(mContext)) {
                    if (isValidate()) {
                        mNetworkCallAddSalesAPI()
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


        }
        return isValid
    }

    private fun mNetworkCallAddSalesAPI() {
        pbLoadData.visibility = View.VISIBLE


        val jsonArray = JSONArray()
        for (i in 0 until filterProductList.size) {
            val element = JSONObject()
            element.put("item_id", filterProductList.get(i).item_id)
            element.put("xid", filterProductList.get(i).xid)
            if (edtDiscount.text.toString().trim().isEmpty()) {
                element.put("discount_rate", 0.0)
            } else {
                element.put("discount_rate", edtDiscount.text.toString().trim().toDouble())

            }




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

        var discount = 0.0
        if (edtDiscount.text.toString().trim().isEmpty()) {
            discount = 0.0
        } else {
            discount = edtDiscount.text.toString().trim().toDouble()
        }

        var shipping = 0.0;
        if (edtShipping.text.toString().trim().isEmpty()) {
            shipping = 0.0
        } else {
            shipping = edtShipping.text.toString().trim().toDouble()
        }

        val call = apiInterface.addSales(
            "sales",
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
            discount.toString(),
            shipping.toString(),
            tvTotal.text.toString().trim(),
            tvTotal.text.toString().trim(),
            filterProductList.size.toString(),
            jsonArray,1
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
                } else if (response.code() == 422) {
                    pbLoadData.visibility = View.GONE
                    try {
                        val jsonObject = JSONObject(response.errorBody()?.string()!!)
                        Toast.makeText(
                            mContext,
                            jsonObject.optJSONObject("error").optJSONObject("details").toString(),
                            Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: JSONException) {
                        e.printStackTrace()
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

    override fun onAddClick(
        filterModel: ProductFilterModel,
        position: Int,
        tvQuantity: TextView,
        tvSubTotal: TextView
    ) {
        // countQuantity++
        filterModel.quantity++
//        if (filterModel.quantity <= filterModel.current_stock) {
            tvQuantity.text = filterModel.quantity.toString()
            val subtotal = (filterModel.unit_price.toInt() * filterModel.quantity)
            tvSubTotal.setText(subtotal.toString())
            total += filterModel.unit_price.toInt()
//            val disTotal = ((shippingTotal + total) - discountTotal)
//            tvTotal.setText(disTotal.toString())
            filterModel.single_unit_price = subtotal.toDouble()
//        } else {
//            Toast.makeText(mContext, "item stock is finished", Toast.LENGTH_SHORT).show()
//        }


        val tempDiscount = (( filterModel.discount_rate.toString().toDouble() * subtotal ).toDouble() / 100)
        val tempTaxRate = ((  filterModel.tax_rate.toString().toDouble() * subtotal ).toDouble() / 100)

//        Log.e("Temp Discount",tempDiscount.toString())
//        Log.e("Temp Tax Rate",tempTaxRate.toString())

        val subtotalNew = subtotal.toDouble() -  tempDiscount.toDouble() + tempTaxRate.toDouble()

        tvSubTotal.setText(subtotalNew.toString())


        total += filterModel.unit_price.toInt()
        val disTotal = ((shippingTotal + total) - discountTotal)
        tvTotal.setText(disTotal.toString())
        filterModel.single_unit_price = subtotal.toDouble()


        val basicAmount = subtotal
        val basicAmountNew = subtotal.toDouble()
        tvBasicAmount.setText( basicAmountNew.toString())

        val itemDiscount = tvItemDiscount.text.toString().toDouble()
        val itemDiscountNew = itemDiscount + tempDiscount
        tvItemDiscount.setText( itemDiscountNew.toString())


        val orderTax = tvOrderTax.text.toString().toDouble()
        val orderTaxNew = orderTax + tempTaxRate
        tvOrderTax.setText( orderTaxNew.toString())

//           tvDiscountTotal.setText()



        tvTotal.text =  (tvBasicAmount.text.toString().toDouble() -
                tvItemDiscount.text.toString().toDouble()
                + tvOrderTax.text.toString().toDouble()
                + tvShipppingTotal.text.toString().toDouble()
                - edtDiscount.text.toString().toDouble()).toString()

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

            filterModel.single_unit_price = subtotal.toDouble()

            (total - filterModel.unit_price.toInt()).also { total = it }
//            val disTotal = ((shippingTotal + total) - discountTotal)
//            tvTotal.setText(disTotal.toString())


            val tempDiscount = (( filterModel.discount_rate.toString().toDouble() * subtotal ).toDouble() / 100)
            val tempTaxRate = ((  filterModel.tax_rate.toString().toDouble() * subtotal ).toDouble() / 100)

//        Log.e("Temp Discount",tempDiscount.toString())
//        Log.e("Temp Tax Rate",tempTaxRate.toString())

            val subtotalNew = subtotal.toDouble() -  tempDiscount.toDouble() + tempTaxRate.toDouble()

            tvSubTotal.setText(subtotalNew.toString())

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

        }

    }

    override fun onEditClick(filterModel: ProductFilterModel, position: Int) {

    }

    private fun showOrderStatusBottomSheet()
    {


        val view: View = layoutInflater.inflate(R.layout.layout_listview_bottomsheet, null)


        val  listAdapter = ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, orderStatusList)

        view.lvCategory.adapter = listAdapter

        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog!!.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        view.lvCategory.setOnItemClickListener { parent, view, position, id ->
            val element = listAdapter.getItem(position) // The item that was clicked

//            Toast.makeText(this,element,Toast.LENGTH_SHORT).show()

            acOrderStatus.setText(element)

            bottomSheetDialog!!.dismiss()


        }




        bottomSheetDialog!!.show()

    }


    fun convertDate(date: String): String {
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+SS:SS")
        val output = SimpleDateFormat("dd-MM-yyyy")

        var d: Date? = null
        try {
            d = input.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val formatted = output.format(d)
        Log.i("DATE", "" + formatted)
        return formatted
    }

    fun showInvoiceNumberBottomSheet() {
        val view: View = layoutInflater.inflate(R.layout.layout_list_bottomsheet_invoice_number, null)

        view.btnSave.setOnClickListener {
            if(view.etInvoiceNumber.text.toString().isNullOrEmpty()) {
                view.etInvoiceNumber.requestFocus()
                view.etInvoiceNumber.setError("Invalid Invoice Number")
            } else {
                edtInvoiceNumber.setText(view.etInvoiceNumber.text.toString())
                bottomSheetDialog!!.dismiss()
            }
        }

        view.ivClose.setOnClickListener {
            bottomSheetDialog!!.dismiss()
        }

        bottomSheetDialog = BottomSheetDialog(mContext,R.style.SheetDialog)
        bottomSheetDialog!!.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetDialog!!.show()
    }


}