package com.playplexmatm.activity

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplexmatm.R
import com.playplexmatm.activity.itemlist.AddEditProductActivity
import com.playplexmatm.activity.partylist.AddEditSupplierActivity
import com.playplexmatm.adapter.FilterProductAdapter
import com.playplexmatm.adapter.SupplierListAdapter
import com.playplexmatm.aeps.activities_aeps.AepsTransactionActivity
import com.playplexmatm.aeps.activities_aeps.PaysprintsOnboardingActivity
import com.playplexmatm.aeps.authentication.AepsLoginActivity
import com.playplexmatm.aeps.model.UserModel
import com.playplexmatm.aeps.network_calls.AppApiCalls
import com.playplexmatm.model.customers.CustomerModel
import com.playplexmatm.model.paymentmode.PaymentModeModel
import com.playplexmatm.model.pos.PaymentModel
import com.playplexmatm.model.product.ProductModel
import com.playplexmatm.model.productfilter.ProductFilterModel
import com.playplexmatm.model.purchasemodel.PurchaseModel
import com.playplexmatm.model.taxes.TaxesModel
import com.playplexmatm.model.warehouse.WareHouseModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.*
import kotlinx.android.synthetic.main.activity_add_purchase_list.*
import kotlinx.android.synthetic.main.layout_list_bottomsheet_customer.view.*
import kotlinx.android.synthetic.main.layout_list_bottomsheet_invoice_number.view.*
import kotlinx.android.synthetic.main.layout_list_supplier.view.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class AddPurchaseListActivity : AppCompatActivity(), View.OnClickListener,
    FilterProductAdapter.onClick, SupplierListAdapter.ListAdapterListener,
    AppApiCalls.OnAPICallCompleteListener {

    lateinit var mContext: AddPurchaseListActivity
    private var supplierStringList: ArrayList<String> = arrayListOf()
    private var orderStatusList: ArrayList<String> = arrayListOf("Recieved","Pending","Ordered")
    lateinit var purchaseModel: PurchaseModel
    lateinit var ivBack: AppCompatImageView
    lateinit var selectedSupplierId : String
    lateinit var edtInvoiceNumber: TextView
    lateinit var supplierListAdapter: SupplierListAdapter
    lateinit var acPaymentMode: AutoCompleteTextView

    private var PaymentModeName: String = ""

    //    lateinit var acUserName: AutoCompleteTextView
//    lateinit var acOrderTax: AutoCompleteTextView
    lateinit var acProduct: AutoCompleteTextView
    //    lateinit var acWareHouse: AutoCompleteTextView
    lateinit var edtDate: EditText
    private var userList: ArrayList<CustomerModel> = arrayListOf()
    private var userStringList: ArrayList<String> = arrayListOf()
    private var userId: String = ""
    private var supplierArrayList: java.util.ArrayList<CustomerModel> = arrayListOf()

    private var wareHouseList: ArrayList<WareHouseModel> = arrayListOf()
    private var wareHouseStringList: ArrayList<String> = arrayListOf()
    private var wareHouseId: String = ""

    var from = ""
    private var productId: String = ""
    private var productName: String = ""
    private var productImage: String = ""
    private var taxId: String = ""
    private var taxRate: String = ""
    private var taxAmount: Double = 0.0
    private var productList: ArrayList<ProductModel> = arrayListOf()
    private var productStringList: ArrayList<String> = arrayListOf()
    private var filterProductList: ArrayList<ProductFilterModel> = arrayListOf()
    private var taxList: ArrayList<TaxesModel> = arrayListOf()
    private var taxStringList: ArrayList<String> = arrayListOf()
    private var taxIntList: ArrayList<Int> = arrayListOf()


    private var paymentModeList: ArrayList<PaymentModeModel> = arrayListOf()
    private var paymentModeStringList: ArrayList<String> = arrayListOf()
    private var paymentFilterList: ArrayList<PaymentModel> = arrayListOf()

    lateinit var pbLoadData: ProgressBar
    lateinit var tvSave: TextView
    lateinit var tvViewPdf: TextView
    lateinit var apiInterface: ApiInterface
    private var selectedDate: String = ""
    lateinit var rvProducts: RecyclerView
    lateinit var edtDiscount: EditText
    lateinit var edtShipping: EditText
    lateinit var edtNotes: EditText
    lateinit var filterProductAdapter: FilterProductAdapter
    lateinit var productFilterModel: ProductFilterModel
    lateinit var tvTotal: EditText
    private var total = 0.0
    private var basicAmount: Double = 0.0
    private var discountTotal: Double = 0.0
    private var quantity: Int = 0
    private var individualDiscountTotal: Double = 0.0
    private var discountNewTotal = 0.0
    private var shippingTotal = 0.0
    private var shippingNewTotal = 0.0
    lateinit var tvDiscount: TextView
    lateinit var tvShippping: TextView
    lateinit var edtTerms: EditText

    var grandTotal: Double = 0.0

    var bottomSheetDialog: BottomSheetDialog? = null

    private val ADD_PRODUCTS = 10
    private val EDIT_PRODUCTS = 11
    var type = "product"
    private var paymentModeName: String = ""
    private var paymentModeId: String = ""
    lateinit var userModel: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_purchase_list)
        mContext = this
        initUI()
        addListner()

        val date = getCurrentDateTime()
        val dateInString = date.toString("yyyy-MM-dd")
        edtDate.setText(dateInString)

        val bundle = intent.extras
        if(bundle != null) {
            purchaseModel = bundle.getSerializable("model") as PurchaseModel
            from = "edit"
            llAddItemForm.visibility = View.VISIBLE
            rl_amount.visibility = View.VISIBLE
            edtInvoiceNumber.setText(purchaseModel.invoice_number)
            edtDate.setText(convertDate(purchaseModel.order_date))
            acSupplier.setText(purchaseModel.user.name)

            selectedSupplierId = purchaseModel.user.xid

            acOrderStatus.setText(purchaseModel.order_status)
            edtDiscount.setText(purchaseModel.discount)
            edtShipping.setText(purchaseModel.shipping)
            edtTerms.setText(purchaseModel.terms_condition)

            tvShippping.setText(purchaseModel.shipping)
            tvDiscount.setText(purchaseModel.discount)
            tvOrderTax.setText(purchaseModel.tax_amount)
            tvTotal.setText((DecimalFormat("##.##").format(purchaseModel.subtotal)))

            Log.e("Model", purchaseModel.items.toString())

            if (purchaseModel.items.size > 0) {
                for (i in 0 until purchaseModel.items.size) {

                    Log.e("Basic Amount", purchaseModel.items[i].subtotal.toString())
                    basicAmount += purchaseModel.items[i].subtotal
                    tvBasicAmount.setText(resources.getString(R.string.Rupee) + " " + (DecimalFormat("##.##").format(basicAmount)))

                    Log.e("Total Discount", purchaseModel.items[i].total_discount.toString())
                    individualDiscountTotal += purchaseModel.items[i].total_discount
                    tvItemDiscount.setText(resources.getString(R.string.Rupee) + " " + individualDiscountTotal)

                    taxAmount += purchaseModel.items[i].total_tax
                    tvOrderTax.setText(
                        resources.getString(R.string.Rupee) + " " + (DecimalFormat("##.##").format(
                            taxAmount
                        ))
                    )

                    tvDiscountTotal.setText(resources.getString(R.string.Rupee) + " " + edtDiscount.text.toString())

                    tvShippping.setText(resources.getString(R.string.Rupee) + " " + edtShipping.text.toString())

                    total += purchaseModel.items[i].subtotal

                    discountTotal += purchaseModel.items[i].total_discount

                    quantity += purchaseModel.items[i].quantity.toInt()

                    tvQuantity.setText(quantity.toString())

                    filterProductList.add(
                        ProductFilterModel(
                            purchaseModel.items[i].product.name,
                            purchaseModel.items[i].product.image_url,
                            purchaseModel.items[i].xid,
                            purchaseModel.items[i].product.details.xid,
                            purchaseModel.items[i].discount_rate.toInt(),
                            purchaseModel.items[i].discount_rate,
                            purchaseModel.items[i].total_discount,
                            "",
                            purchaseModel.items[i].tax_type,
                            purchaseModel.items[i].tax_rate.toInt(),
                            purchaseModel.items[i].total_tax,
                            purchaseModel.items[i].product.unit.xid,
                            purchaseModel.items[i].unit_price,
                            purchaseModel.items[i].single_unit_price,
                            purchaseModel.items[i].subtotal,
                            purchaseModel.items[i].product.details.current_stock.toInt(),
                            purchaseModel.items[i].quantity.toInt()
                        )
                    )
                }
                setProductData(
                    filterProductList[0].name,
                    filterProductList[0].image_url,
                    filterProductList
                )
            }
            Log.e("size",filterProductList.size.toString())

        }

        tvProduct.setOnClickListener {
            type = "product"
            tvProduct.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            tvProduct.setTextColor(resources.getColor(R.color.white))
            tvDirect.setBackgroundColor(resources.getColor(R.color.colorBackground))
            tvDirect.setTextColor(resources.getColor(R.color.colorPrimary))
            rvProducts.visibility = View.VISIBLE
            rl_add_item.visibility = View.VISIBLE
            rl_amount.visibility = View.VISIBLE
            tvBasicAmountTAG1.visibility = View.GONE
            edtBasicAmount.visibility = View.GONE

            total = 0.0
            taxAmount = 0.0
            basicAmount = 0.0
            individualDiscountTotal = 0.0
            quantity = 0
            grandTotal = 0.0
        }

        tvDirect.setOnClickListener {
            type = "direct"
            tvDirect.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            tvDirect.setTextColor(resources.getColor(R.color.white))
            tvProduct.setBackgroundColor(resources.getColor(R.color.colorBackground))
            tvProduct.setTextColor(resources.getColor(R.color.colorPrimary))
            rvProducts.visibility = View.GONE
            rl_add_item.visibility = View.GONE
            rl_amount.visibility = View.GONE
            llAddItemForm.visibility = View.VISIBLE
            tvBasicAmountTAG1.visibility = View.VISIBLE
            edtBasicAmount.visibility = View.VISIBLE

            total = 0.0
            taxAmount = 0.0
            basicAmount = 0.0
            individualDiscountTotal = 0.0
            quantity = 0
            grandTotal = 0.0
        }
    }

    private fun initUI() {
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        ivBack = findViewById(R.id.ivBack)
        ivBack = findViewById(R.id.ivBack)
        edtInvoiceNumber = findViewById(R.id.edtInvoiceNumber)
        acPaymentMode = findViewById(R.id.acPaymentMode)
        acProduct = findViewById(R.id.acProduct)
        edtDate = findViewById(R.id.edtDate)
        pbLoadData = findViewById(R.id.pbLoadData)
        tvSave = findViewById(R.id.tvSave)
        tvViewPdf = findViewById(R.id.tvViewPdf)
        rvProducts = findViewById(R.id.rvProducts)
//        acOrderTax = findViewById(R.id.acOrderTax)
//        acWareHouse = findViewById(R.id.acWareHouse)
        edtDiscount = findViewById(R.id.edtDiscount)
        edtShipping = findViewById(R.id.edtShipping)
        edtNotes = findViewById(R.id.edtNotes)
        tvTotal = findViewById(R.id.tvTotal)
        tvDiscount = findViewById(R.id.tvDiscountTotal)
        tvShippping = findViewById(R.id.tvShipppingTotal)
        edtTerms = findViewById(R.id.edtTerms)

        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallGetUserAPI()
//            mNetworkCallGetProductAPI()
            mNetworkCallGetTaxAPI()
            mNetworkCallGetWareHouseAPI()
            mNetworkCallGetSuppliersAPI()
            mNetworkCallGetPaymentModeAPI()
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
        tvViewPdf.setOnClickListener(this)

        ivAddProduct.setOnClickListener {
            val intent = Intent(this,AddEditProductActivity::class.java)
            startActivity(intent)
        }

        ivAddSupplier.setOnClickListener {
            val intent = Intent(this,AddEditSupplierActivity::class.java)
            startActivity(intent)
        }

        acPaymentMode.setOnTouchListener { v, event ->
            acPaymentMode.showDropDown()
            false
        }


        rl_add_item.setOnClickListener {
            llAddItemForm.visibility = View.VISIBLE
            rl_amount.visibility = View.VISIBLE
//            tvAddItem.visibility = View.GONE
            val intent = Intent(this,AddProductsActivity::class.java)
            startActivityForResult(intent,ADD_PRODUCTS)
        }


        acSupplier.setOnClickListener {
            showSupplierBottomSheet()
        }

        acOrderStatus.setOnClickListener {
            showOrderStatusBottomSheet()
        }

//        acUserName.setOnTouchListener { v, event ->
//            acUserName.showDropDown()
//            false
//        }
        acProduct.setOnTouchListener { v, event ->
            acProduct.showDropDown()
            false
        }

        edtBasicAmount.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!!.isNotEmpty()) {
                    basicAmount = edtBasicAmount.text.toString().toDouble()
                    total = basicAmount - discountNewTotal + shippingTotal
                    tvTotal.setText(total.toString())
                } else {
                    basicAmount = 0.0
                    total = basicAmount - discountNewTotal + shippingTotal
                    tvTotal.setText(total.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

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
                    discountNewTotal = edtDiscount.text.toString().toDouble()
//                    discountNewTotal = total - discountTotal.toDouble()
//                    discountNewTotal = ((shippingTotal + total) - discountTotal)

//                    tvTotal.text = discountNewTotal.toString()

                    tvTotal.setText((DecimalFormat("##.##").format((basicAmount -
                            discountTotal + taxAmount
                            - s.toString().toDouble()
                            + shippingTotal))).toString())

                } else {
//                    discountTotal = 0.0
                    discountNewTotal = 0.0
//                    discountNewTotal = ((shippingTotal + total) - discountTotal)
//                    tvTotal.text = discountNewTotal.toString()
                    tvDiscount.setText("0")

                    tvTotal.setText((DecimalFormat("##.##").format((basicAmount -
                            discountTotal + taxAmount
                            - discountNewTotal
                            + shippingTotal))).toString())

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
                    shippingTotal = edtShipping.text.toString().toDouble()
//                    tvTotal.text = shippingNewTotal.toString()


                    tvTotal.setText(DecimalFormat("##.##").format(basicAmount -
                            discountTotal
                            + taxAmount
                            + s.toString().toDouble()
                            - discountNewTotal).toString())

                } else {
                    shippingTotal = 0.0
                    shippingNewTotal = 0.0
                    tvShippping.text = s.toString()

                    tvTotal.setText(DecimalFormat("##.##").format(basicAmount -
                            discountTotal
                            + taxAmount
                            + shippingTotal
                            - discountNewTotal).toString())


//                    shippingNewTotal = ((shippingTotal + total) - discountTotal)
//                    tvTotal.text = shippingNewTotal.toString()
//                    tvShippping.setText("0.00")
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        edtInvoiceNumber.setOnClickListener {
            showInvoiceNumberBottomSheet()
        }
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


    private fun showSupplierBottomSheet()
    {
        val view: View = layoutInflater.inflate(R.layout.layout_list_bottomsheet_customer, null)


        view.rvCustomer.apply {

            layoutManager = LinearLayoutManager(this@AddPurchaseListActivity)
            supplierListAdapter = SupplierListAdapter(
                context, supplierArrayList, this@AddPurchaseListActivity
            )
            view.rvCustomer.adapter = supplierListAdapter
        }
        view.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable?) {
                filterCustomer(s.toString())
            }
        })


        bottomSheetDialog = BottomSheetDialog(this,R.style.SheetDialog)
        bottomSheetDialog!!.setContentView(view)

        val bottomSheetBehavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from(view.parent as View)
        bottomSheetBehavior.peekHeight = 1000
        bottomSheetDialog!!.show()

    }


    fun filterCustomer(text: String) {
        val temp: MutableList<CustomerModel> = ArrayList()
        for (d in userList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.name.contains(text, ignoreCase = true) || d.phone.contains(text, ignoreCase = true)) {
                temp.add(d)
            }
        }
        //update recyclerview
        supplierListAdapter.updateList(temp)
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


            acOrderStatus.setText(element)

            bottomSheetDialog!!.dismiss()


        }

        bottomSheetDialog!!.show()

    }


    private fun setAdapter(userList: ArrayList<CustomerModel>) {
        for (i in 0 until userList.size) {
            userStringList.add(userList[i].name)
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, userStringList)

//        acUserName.threshold = 0 //will start working from first character
//
//        acUserName.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView
//
//        acUserName.setOnItemClickListener { parent, view, position, id ->
//            userId = userList[position].xid
//        }
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

    private fun mNetworkCallGetSuppliersAPI() {

        val call = apiInterface.getSuppliersDropDown()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())

                        Log.e("response supplier",jsonObject.toString())

                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONArray("data")
                            supplierArrayList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<CustomerModel?>?>() {}.type
                                )
                            )

                            for (i in 0 until supplierArrayList.size) {

                                val  data = supplierArrayList[i].name + " - " +supplierArrayList[i].phone
                                supplierStringList.add(data )
                            }


                        }

                    } else {
                        Toast.makeText(
                            this@AddPurchaseListActivity,
                            resources.getString(R.string.str_something_went_wrong_on_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(this@AddPurchaseListActivity, t.message, Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun setwareHouseAdapter(wareHouseList: ArrayList<WareHouseModel>) {
        for (i in 0 until wareHouseList.size) {
            wareHouseStringList.add(wareHouseList[i].name)
        }

        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, wareHouseStringList)

//        acWareHouse.threshold = 0 //will start working from first character
//
//        acWareHouse.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView
//
//        acWareHouse.setOnItemClickListener { parent, view, position, id ->
//            wareHouseId = wareHouseList[position].xid
//        }
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
            FilterProductAdapter(mContext, productName, productImage, filterProductList, this,
                taxStringList)
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

                        Log.e("TAX API",jsonObject.toString())

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

    private fun mNetworkCallGetPurchaseDetailsAPI(id: String) {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.getPurchaseId(id)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())

                        Log.e("TAX API",jsonObject.toString())

                        if (jsonObject.optBoolean("status")) {
                            var data = jsonObject.getJSONObject("data").getJSONObject("order").getString("unique_id")
                            data = "http://pospayplex.com/api/rest/pdf/${data}/en"


                            val i = Intent(Intent.ACTION_VIEW)
                            i.setDataAndType(data.toUri(), "application/pdf")

                            startActivity(i)





//
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
            taxStringList.add(taxList[i].rate)
            taxIntList.add(taxList[i].rate.toInt())
        }




        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, R.layout.dropdown, taxStringList)

//        acOrderTax.threshold = 0 //will start working from first character
//
//        acOrderTax.setAdapter<ArrayAdapter<String>>(adapter) //setting the adapter data into the AutoCompleteTextView
//
//        acOrderTax.setOnItemClickListener { parent, view, position, id ->
//            taxId = taxList[position].xid
//            taxRate = taxList[position].rate
//            taxAmount = taxList[position].rate
//        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.tvSave -> {
                if (InternetConnection.checkConnection(mContext)) {
                    if (isValidate()) {
                        if(from.equals("edit")) {
                            mNetworkCallUpdatePurchaseAPI()
                        }else {
                            if (acPaymentMode.text.toString().equals("aeps", ignoreCase = true)) {
//                                val islogin: Boolean =
//                                    AppPrefs.getBooleanPref(AppConstants.IS_LOGIN, mContext)
//                                if (islogin) {
//                                    val gson = Gson()
//                                    val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, this)
//                                    userModel = gson.fromJson(json, UserModel::class.java)
//
//                                    dashboardApi(userModel.cus_mobile)
//
//                                } else {
//                                    val intent = Intent(this,AepsLoginActivity::class.java)
//                                    startActivity(intent)
//                                }
                                val bundle = Bundle()
                                bundle.putString("basicAmount",basicAmount.toString())
                                bundle.putString("totalAmount",tvTotal.text.toString())
                                val intent = Intent(this,PurchasePaymentModeActivity::class.java)
                                intent.putExtras(bundle)
                                startActivityForResult(intent,108)
                            } else {
                                if(type.equals("direct")) {
                                    mNetworkCallAddPurchaseAPI(0)
                                } else {
                                    mNetworkCallAddPurchaseAPI(1)
                                }
                            }

                        }
                    }
                } else {
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.str_check_internet_connections),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            R.id.tvViewPdf -> {
                if (InternetConnection.checkConnection(mContext)) {
                    if (isValidate()) {
                        if(from.equals("edit")) {
                            mNetworkCallUpdatePurchaseAPI()
                        }else {
                            if(type.equals("direct")) {
                                mNetworkCallAddPurchaseViewPdfAPI(0)
                            } else {
                                mNetworkCallAddPurchaseViewPdfAPI(1)
                            }
                        }
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
//            edtInvoiceNumber.text.toString().trim().isEmpty() -> {
//                Toast.makeText(mContext, "Please enter invoice number", Toast.LENGTH_SHORT).show()
//                isValid = false
//            }
            selectedSupplierId.isNullOrEmpty() -> {
                Toast.makeText(mContext, "Please select Supplier", Toast.LENGTH_SHORT).show()
                isValid = false
            }
//            wareHouseId.isEmpty() -> {
//                Toast.makeText(mContext, "Please select warehouse", Toast.LENGTH_SHORT).show()
//                isValid = false
//            }
            edtDate.text.toString().trim().isEmpty() -> {
                Toast.makeText(mContext, "Please select order date", Toast.LENGTH_SHORT).show()
                isValid = false
            }

            filterProductList.isEmpty() -> {
                if(type.equals("product")) {
                    Toast.makeText(mContext, "Please select product", Toast.LENGTH_SHORT).show()
                    isValid = false
                }
            }
//            taxId.isEmpty() -> {
//                Toast.makeText(mContext, "Please select order id", Toast.LENGTH_SHORT).show()
//                isValid = false
//            }
        }
        return isValid
    }

    private fun mNetworkCallUpdatePurchaseAPI() {
        pbLoadData.visibility = View.VISIBLE

        val jsonArray = JSONArray()
        for (i in 0 until filterProductList.size) {
            val element = JSONObject()
            element.put("item_id", filterProductList.get(i).xid)
            Log.e("item_id", filterProductList.get(i).item_id)
            element.put("xid", filterProductList.get(i).xid)

            if (edtDiscount.text.toString().trim().isEmpty()) {
                element.put("discount_rate", 0.0)
            } else {
                element.put("discount_rate", edtDiscount.text.toString().trim().toDouble())
            }

            element.put("total_discount", filterProductList.get(i).total_discount)
            element.put("x_tax_id",  filterProductList.get(i).x_tax_id)
            element.put("tax_type", filterProductList.get(i).tax_type)
            element.put("tax_rate",  filterProductList.get(i).tax_rate)
            element.put("total_tax",  filterProductList.get(i).total_tax)
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

        Log.e("tax_amount",taxAmount.toString())

        Log.e("Update Purchase","purchases:"
                +purchaseModel.xid
                +edtInvoiceNumber.text.toString().trim()+":"
                +edtDate.text.toString().trim()+":"
                +wareHouseId+":"
                +selectedSupplierId+":"
                + edtTerms.text.toString().trim()+":"
                +edtNotes.text.toString().trim()+":"
                +acOrderStatus.text.toString()+":"
                +"00"+":"
                +"0"+":"
                +taxAmount.toString()+":"
                +discount.toString()+":"
                +shipping.toString()+":"
                +tvTotal.text.toString().trim()+":"
                +jsonArray.toString()+":")


        val call = apiInterface.updatePurchases(
            purchaseModel.xid,
            "purchases",
            edtInvoiceNumber.text.toString().trim(),
            edtDate.text.toString().trim(),
            wareHouseId,
            selectedSupplierId,
            edtTerms.text.toString().trim(),
            edtNotes.text.toString().trim(),
            acOrderStatus.text.toString(),
            null,
            "0",
            taxAmount.toString(),
            discount.toString(),
            shipping.toString(),
            tvTotal.text.toString().trim(),
            tvTotal.text.toString().trim(),
            filterProductList.size.toString(),
            jsonArray
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.e("Resp Code",response.toString())
                if (response.isSuccessful) {

                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())

                        Log.e("response",jsonObject.toString())

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
                } else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject = JSONObject(response.errorBody()!!.string())
                        Log.e("response",JsonObject.toString())

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

    private fun mNetworkCallAddPurchaseAPI(is_product_invoice: Int) {
        pbLoadData.visibility = View.VISIBLE

        val jsonArray = JSONArray()
        for (i in 0 until filterProductList.size) {
            val element = JSONObject()
            element.put("item_id", filterProductList.get(i).item_id)
            Log.e("item_id", filterProductList.get(i).item_id)
            element.put("xid", filterProductList.get(i).xid)

            if (edtDiscount.text.toString().trim().isEmpty()) {
                element.put("discount_rate", 0.0)
            } else {
                element.put("discount_rate", edtDiscount.text.toString().trim().toDouble())
            }

            element.put("total_discount", filterProductList.get(i).total_discount)
            element.put("x_tax_id",  filterProductList.get(i).x_tax_id)
            element.put("tax_type", filterProductList.get(i).tax_type)
            element.put("tax_rate",  filterProductList.get(i).tax_rate)
            element.put("total_tax",  filterProductList.get(i).total_tax)
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

        Log.e("tax_amount",taxAmount.toString())

        Log.e("Add Purchase","purchases:"
                +edtInvoiceNumber.text.toString().trim()+":"
                +edtDate.text.toString().trim()+":"
                +wareHouseId+":"
                +selectedSupplierId+":"
                + edtTerms.text.toString().trim()+":"
                +edtNotes.text.toString().trim()+":"
                +acOrderStatus.text.toString()+":"
                +"00"+":"
                +"0"+":"
                +taxAmount.toString()+":"
                +discount.toString()+":"
                +shipping.toString()+":"
                +tvTotal.text.toString().trim()+":"
                +jsonArray.toString()+":"
                +is_product_invoice.toString()+":")


        val call = apiInterface.addPurchases(
            "purchases",
            edtInvoiceNumber.text.toString().trim(),
            edtDate.text.toString().trim(),
            wareHouseId,
            selectedSupplierId,
            edtTerms.text.toString().trim(),
            edtNotes.text.toString().trim(),
            acOrderStatus.text.toString(),
            null,
            "0",
            taxAmount.toString(),
            discount.toString(),
            shipping.toString(),
            tvTotal.text.toString().trim(),
            tvTotal.text.toString().trim(),
            filterProductList.size.toString(),
            jsonArray,is_product_invoice
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.e("Resp Code",response.toString())
                if (response.isSuccessful) {

                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())

                        Log.e("response",jsonObject.toString())

                        if (jsonObject.optBoolean("status")) {
                            Toast.makeText(
                                mContext,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()

//                            mNetworkCallGetPurchaseDetailsAPI(jsonObject.getJSONObject("data").getString("xid"))

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
                } else if (response.code() == 401) {
                    try {
                        pbLoadData.visibility = View.GONE
                        val JsonObject = JSONObject(response.errorBody()!!.string())
                        Log.e("response",JsonObject.toString())

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

    private fun mNetworkCallAddPurchaseViewPdfAPI(is_product_invoice: Int) {
        pbLoadData.visibility = View.VISIBLE

        val jsonArray = JSONArray()
        for (i in 0 until filterProductList.size) {
            val element = JSONObject()
            element.put("item_id", filterProductList.get(i).item_id)
            Log.e("item_id", filterProductList.get(i).item_id)
            element.put("xid", filterProductList.get(i).xid)

            if (edtDiscount.text.toString().trim().isEmpty()) {
                element.put("discount_rate", 0.0)
            } else {
                element.put("discount_rate", edtDiscount.text.toString().trim().toDouble())
            }

            element.put("total_discount", filterProductList.get(i).total_discount)
            element.put("x_tax_id",  filterProductList.get(i).x_tax_id)
            element.put("tax_type", filterProductList.get(i).tax_type)
            element.put("tax_rate",  filterProductList.get(i).tax_rate)
            element.put("total_tax",  filterProductList.get(i).total_tax)
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

        Log.e("tax_amount",taxAmount.toString())

        Log.e("Add Purchase","purchases:"
                +edtInvoiceNumber.text.toString().trim()+":"
                +edtDate.text.toString().trim()+":"
                +wareHouseId+":"
                +selectedSupplierId+":"
                + edtTerms.text.toString().trim()+":"
                +edtNotes.text.toString().trim()+":"
                +acOrderStatus.text.toString()+":"
                +"00"+":"
                +"0"+":"
                +taxAmount.toString()+":"
                +discount.toString()+":"
                +shipping.toString()+":"
                +tvTotal.text.toString().trim()+":"
                +jsonArray.toString()+":"
                +is_product_invoice.toString()+":")


        val call = apiInterface.addPurchases(
            "purchases",
            edtInvoiceNumber.text.toString().trim(),
            edtDate.text.toString().trim(),
            wareHouseId,
            selectedSupplierId,
            edtTerms.text.toString().trim(),
            edtNotes.text.toString().trim(),
            acOrderStatus.text.toString(),
            null,
            "0",
            taxAmount.toString(),
            discount.toString(),
            shipping.toString(),
            tvTotal.text.toString().trim(),
            tvTotal.text.toString().trim(),
            filterProductList.size.toString(),
            jsonArray,is_product_invoice
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.e("Resp Code",response.toString())
                if (response.isSuccessful) {

                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())

                        Log.e("response",jsonObject.toString())

                        if (jsonObject.optBoolean("status")) {
                            Toast.makeText(
                                mContext,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()

                            mNetworkCallOrderHtmlAPI(jsonObject.getJSONObject("data").getString("xid"))

//                            val intent = Intent()
//                            setResult(Activity.RESULT_OK, intent)
//                            finish()
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
                        Log.e("response",JsonObject.toString())

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


        val tempDiscount = (( filterModel.discount_rate.toString().toDouble() * subtotal ).toDouble() / 100)
        val tempTaxRate = ((  filterModel.tax_rate.toString().toDouble() * filterModel.unit_price.toInt() ).toDouble() / 100)

//        Log.e("Temp Discount",tempDiscount.toString())
//        Log.e("Temp Tax Rate",tempTaxRate.toString())

        val subtotalNew = subtotal.toDouble() -  tempDiscount.toDouble() + tempTaxRate.toDouble()

        tvSubTotal.setText(subtotalNew.toString())


        total += filterModel.unit_price.toInt()
        val disTotal = ((shippingTotal + total) - discountTotal)
        tvTotal.setText((DecimalFormat("##.##").format(disTotal)).toString())
        filterModel.single_unit_price = subtotal.toDouble()


        val basicAmount = subtotal
        val basicAmountNew = subtotal.toDouble()
        tvBasicAmount.setText((DecimalFormat("##.##").format(basicAmountNew)).toString())

        val itemDiscount = tvItemDiscount.text.toString().toDouble()
        val itemDiscountNew = itemDiscount + tempDiscount
        tvItemDiscount.setText( itemDiscountNew.toString())


        val orderTax = tvOrderTax.text.toString().toFloat()
        val orderTaxNew = orderTax + tempTaxRate
        tvOrderTax.setText( orderTaxNew.toString())

//           tvDiscountTotal.setText()



        tvTotal.setText((DecimalFormat("##.##").format(tvBasicAmount.text.toString().toDouble() -
                tvItemDiscount.text.toString().toDouble()
                + tvOrderTax.text.toString().toDouble()
                + tvShipppingTotal.text.toString().toDouble()
                - edtDiscount.text.toString().toDouble())).toString())

        val itemShipping = tvShipppingTotal.text.toString().toDouble()
//        val itemShippingNew = itemShipping + filterModel.sh
//        tvItemDiscount.setText( itemDiscountNew.toString())
//           tvShipppingTotal.setText()


/*
            val basicSubtotal = (filterModel.unit_price.toInt() * filterModel.quantity)
            val tempDiscount = (( filterModel.discount_rate.toString().toDouble() * basicSubtotal ).toDouble() / 100)
            val tempTaxRate = (( filterModel.tax_rate.toString().toDouble() * basicSubtotal ).toDouble() / 100)
            val subtotal = basicSubtotal.toDouble() -  tempDiscount.toDouble() + tempTaxRate.toDouble()
  */



//        } else {
//            Toast.makeText(mContext, "item stock is finished", Toast.LENGTH_SHORT).show()
//        }
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

            filterModel.single_unit_price = subtotal.toDouble()


            val tempDiscount = (( filterModel.discount_rate.toString().toDouble() * subtotal ).toDouble() / 100)
            val tempTaxRate = ((  filterModel.tax_rate.toString().toDouble() * filterModel.unit_price.toInt() ).toDouble() / 100)

//        Log.e("Temp Discount",tempDiscount.toString())
//        Log.e("Temp Tax Rate",tempTaxRate.toString())

            val subtotalNew = subtotal.toDouble() -  tempDiscount.toDouble() + tempTaxRate.toDouble()

            tvSubTotal.setText(subtotalNew.toString())

            Log.e("temp tax rate",tvOrderTax.text.toString()+"::"+tempTaxRate.toString())
            val orderTax = tvOrderTax.text.toString().toFloat()
            val orderTaxNew = orderTax - tempTaxRate
            tvOrderTax.setText(orderTaxNew.toString())

            (total - filterModel.unit_price.toInt()).also { total = it }
            val disTotal = ((shippingTotal + total) - discountTotal)
            tvTotal.setText((DecimalFormat("##.##").format(disTotal)).toString())
        }
    }

    override fun onDeleteClick(filterModel: ProductFilterModel, position: Int) {

        total = 0.0
        taxAmount = 0.0
        basicAmount = 0.0
        individualDiscountTotal = 0.0
        quantity = 0
        grandTotal = 0.0
        shippingTotal = 0.0


        if (filterProductList.size > 0) {
            filterModel == null
            filterProductList.removeAt(position)

            filterProductAdapter.notifyDataSetChanged()

            for (i in 0 until filterProductList.size) {

                basicAmount += filterProductList[i].subtotal
                Log.e("basicAmount", basicAmount.toString())

                individualDiscountTotal += filterProductList[i].total_discount
                Log.e("individualDiscountTotal", individualDiscountTotal.toString())

                taxAmount += filterProductList[i].total_tax
                Log.e("taxAmount", taxAmount.toString())


                total += filterProductList[i].subtotal

                discountTotal += filterProductList[i].total_discount

                quantity += filterProductList[i].quantity

                grandTotal += (filterProductList[i].subtotal - filterProductList[i].total_discount + filterProductList[i].total_tax)

            }
            tvBasicAmount.setText(resources.getString(R.string.Rupee)+" "+(DecimalFormat("##.##").format(basicAmount)))
            tvItemDiscount.setText(resources.getString(R.string.Rupee)+" "+individualDiscountTotal)

            tvOrderTax.setText(resources.getString(R.string.Rupee)+" "+(DecimalFormat("##.##").format(taxAmount)))
            tvDiscountTotal.setText(resources.getString(R.string.Rupee)+" "+edtDiscount.text.toString())

            tvShippping.setText(resources.getString(R.string.Rupee)+" "+edtShipping.text.toString())

            tvQuantity.setText(quantity.toString())

            tvTotal.setText((DecimalFormat("##.##").format(grandTotal - discountNewTotal + shippingNewTotal)).toString())

        }

    }

    override fun onEditClick(filterModel: ProductFilterModel, position: Int) {
//        filterProductList[position].discount_rate = filterModel.discount_rate
        filterProductAdapter.notifyDataSetChanged()

        val bundle = Bundle()
        bundle.putSerializable("filterModel",filterModel)
        bundle.putString("position", position.toString())
        val intent = Intent(this, AddProductsActivity::class.java)
        intent.putExtras(bundle)
        startActivityForResult(intent,EDIT_PRODUCTS)
    }


    private fun getSupplierId() : String
    {
        for(i in 0 until supplierArrayList.size)
        {
            if( acSupplier.text.toString().equals( supplierArrayList.get(i).name))
            {
                return supplierArrayList.get(i).xid
            }
        }
        return "-1"

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

    override fun onClickAtOKButton(customerModel: CustomerModel) {
        if(customerModel != null) {
            acSupplier.setText(customerModel.name)
            selectedSupplierId = customerModel.xid
            wareHouseId = customerModel.x_warehouse_id
            bottomSheetDialog!!.dismiss()
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ADD_PRODUCTS -> {
                if (resultCode === RESULT_OK) {
                    val extra: Bundle = data!!.getBundleExtra("extra")!!
                    productFilterModel = extra.getSerializable("model") as ProductFilterModel

                    var flag = false
                    for(i in 0 until filterProductList.size) {
                        if(filterProductList[i].xid.equals(productFilterModel.xid)) {
                            flag = true
                        }
                    }
                    if (filterProductList.size > 0) {
                        if (flag.equals(false)) {

                            filterProductList.add(
                                ProductFilterModel(
                                    productFilterModel.name,
                                    productFilterModel.image_url,
                                    productFilterModel.item_id,
                                    productFilterModel.xid,
                                    productFilterModel.discount_rate,
                                    productFilterModel.individual_discount,
                                    productFilterModel.total_discount,
                                    productFilterModel.x_tax_id,
                                    productFilterModel.tax_type,
                                    productFilterModel.tax_rate,
                                    productFilterModel.total_tax,
                                    productFilterModel.x_unit_id,
                                    productFilterModel.single_unit_price,
                                    productFilterModel.single_unit_price,
                                    productFilterModel.subtotal,
                                    productFilterModel.current_stock,
                                    productFilterModel.quantity
                                )
                            )
                            Log.e("Basic Amount",productFilterModel.subtotal.toString())
                            basicAmount += productFilterModel.subtotal
                            tvBasicAmount.setText(resources.getString(R.string.Rupee)+" "+(DecimalFormat("##.##").format(basicAmount)))

                            Log.e("Total Discount",productFilterModel.total_discount.toString())
                            individualDiscountTotal += productFilterModel.total_discount
                            tvItemDiscount.setText(resources.getString(R.string.Rupee)+" "+individualDiscountTotal)

                            taxAmount += productFilterModel.total_tax
                            tvOrderTax.setText(resources.getString(R.string.Rupee)+" "+(DecimalFormat("##.##").format(taxAmount)))

                            tvDiscountTotal.setText(resources.getString(R.string.Rupee)+" "+edtDiscount.text.toString())

                            tvShippping.setText(resources.getString(R.string.Rupee)+" "+edtShipping.text.toString())

                            total += productFilterModel.subtotal

                            discountTotal += productFilterModel.total_discount

                            quantity += productFilterModel.quantity
                            tvQuantity.setText(quantity.toString())

                            if(productFilterModel.tax_type.equals("inclusive",ignoreCase = true)) {
                                grandTotal += (productFilterModel.subtotal - productFilterModel.total_discount)
                            } else {
                                grandTotal += (productFilterModel.subtotal - productFilterModel.total_discount + productFilterModel.total_tax)
                            }
                            tvTotal.setText((DecimalFormat("##.##").format(grandTotal)).toString())

                        } else {
                            Toast.makeText(mContext, "Already added this items", Toast.LENGTH_SHORT).show()
                        }
                    } else {

                        filterProductList.add(
                            ProductFilterModel(
                                productFilterModel.name,
                                productFilterModel.image_url,
                                productFilterModel.item_id,
                                productFilterModel.xid,
                                productFilterModel.discount_rate,
                                productFilterModel.individual_discount,
                                productFilterModel.total_discount,
                                productFilterModel.x_tax_id,
                                productFilterModel.tax_type,
                                productFilterModel.tax_rate,
                                productFilterModel.total_tax,
                                productFilterModel.x_unit_id,
                                productFilterModel.single_unit_price,
                                productFilterModel.single_unit_price,
                                productFilterModel.subtotal,
                                productFilterModel.current_stock,
                                productFilterModel.quantity
                            )
                        )

                        Log.e("Basic Amount",productFilterModel.subtotal.toString())
                        basicAmount += productFilterModel.subtotal
                        tvBasicAmount.setText(resources.getString(R.string.Rupee)+" "+(DecimalFormat("##.##").format(basicAmount)))

                        Log.e("Total Discount",productFilterModel.total_discount.toString())
                        individualDiscountTotal += productFilterModel.total_discount
                        tvItemDiscount.setText(resources.getString(R.string.Rupee)+" "+individualDiscountTotal)

                        taxAmount += productFilterModel.total_tax
                        tvOrderTax.setText(resources.getString(R.string.Rupee)+" "+(DecimalFormat("##.##").format(taxAmount)))

                        tvDiscountTotal.setText(resources.getString(R.string.Rupee)+" "+edtDiscount.text.toString())

                        tvShippping.setText(resources.getString(R.string.Rupee)+" "+edtShipping.text.toString())

                        total += productFilterModel.subtotal

                        discountTotal += productFilterModel.total_discount

                        quantity += productFilterModel.quantity
                        tvQuantity.setText(quantity.toString())

                        if(productFilterModel.tax_type.equals("inclusive",ignoreCase = true)) {
                            grandTotal += (productFilterModel.subtotal - productFilterModel.total_discount)
                        } else {
                            grandTotal += (productFilterModel.subtotal - productFilterModel.total_discount + productFilterModel.total_tax)
                        }
                        tvTotal.setText((DecimalFormat("##.##").format(grandTotal)).toString())
                    }
                    setProductData(productFilterModel.name, productFilterModel.image_url, filterProductList)
                }
            }
            EDIT_PRODUCTS -> {
                if (resultCode === RESULT_OK) {
                    val extra: Bundle = data!!.getBundleExtra("extra")!!
                    productFilterModel = extra.getSerializable("model") as ProductFilterModel

                    val position = extra.getString("position")!!.toInt()

                    total = 0.0
                    taxAmount = 0.0
                    basicAmount = 0.0
                    individualDiscountTotal = 0.0
                    grandTotal = 0.0
                    quantity = 0

                    if (filterProductList.size > 0) {
                        filterProductList.removeAt(position)
                        filterProductList.add(position,productFilterModel)

                        filterProductAdapter.notifyDataSetChanged()

                        for (i in 0 until filterProductList.size) {

                            basicAmount += filterProductList[i].subtotal
                            Log.e("basicAmount", basicAmount.toString())

                            individualDiscountTotal += filterProductList[i].total_discount
                            Log.e("individualDiscountTotal", individualDiscountTotal.toString())

                            taxAmount += filterProductList[i].total_tax
                            Log.e("taxAmount", taxAmount.toString())


                            total += filterProductList[i].subtotal

                            discountTotal += filterProductList[i].total_discount
                            quantity += productFilterModel.quantity
                            tvQuantity.setText(quantity.toString())

                            if(filterProductList[i].tax_type.equals("inclusive",ignoreCase = true)) {
                                grandTotal += (productFilterModel.subtotal - productFilterModel.total_discount)
                            } else {
                                grandTotal += (productFilterModel.subtotal - productFilterModel.total_discount + productFilterModel.total_tax)
                            }

                            tvTotal.setText((DecimalFormat("##.##").format(grandTotal)).toString())

                        }
                        tvBasicAmount.setText(resources.getString(R.string.Rupee)+" "+(DecimalFormat("##.##").format(basicAmount)))
                        tvItemDiscount.setText(resources.getString(R.string.Rupee)+" "+individualDiscountTotal)

                        tvOrderTax.setText(resources.getString(R.string.Rupee)+" "+(DecimalFormat("##.##").format(taxAmount)))
                        tvDiscountTotal.setText(resources.getString(R.string.Rupee)+" "+edtDiscount.text.toString())

                        tvShippping.setText(resources.getString(R.string.Rupee)+" "+edtShipping.text.toString())

                    }
                }
            }
            108 -> {
                if(resultCode == Activity.RESULT_OK) {
                    val extra: Bundle = data!!.getBundleExtra("data")!!

                    if(type.equals("direct")) {
                        mNetworkCallAddPurchaseAPI(0)
                    } else {
                        mNetworkCallAddPurchaseAPI(1)
                    }
                }
            }
        }
    }


    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }


    private fun mNetworkCallOrderHtmlAPI(xid: String) {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.orderHtml(xid)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONObject("data")
                            Log.e("data",data.getString("html"))
//                            startActivity(Intent(mContext,WebviewActivity::class.java).putExtra("data",data.optString("html")))
                            setData(data.optString("html"))
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

    private fun createWebPrintJob(webView: WebView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val printManager = this.getSystemService(Context.PRINT_SERVICE) as PrintManager
            val printAdapter = webView.createPrintDocumentAdapter("MyDocument")
            printManager.print("My Print Job", printAdapter, PrintAttributes.Builder().build())
        } else {
            // SHOW MESSAGE or UPDATE UI
        }
    }

    private fun setData(data: String) {
        val webView = WebView(this)
        webView.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?, request: WebResourceRequest?
            ): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                createWebPrintJob(view!!)
            }
        })
        val myHtml: String = data
        webView.loadDataWithBaseURL(null, myHtml, "text/HTML", "UTF-8", null)
    }


    private fun mNetworkCallGetPaymentModeAPI() {
        paymentModeList.clear()
        paymentModeStringList.clear()
        paymentFilterList.clear()
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
            paymentModeId = paymentList[position].xid
            paymentModeName = paymentList[position].name

        }
    }

    private fun dashboardApi(
        cus_id: String
    ) {
        pbLoadData.visibility = View.VISIBLE
        if (AppCommonMethods(this).isNetworkAvailable) {
            val mAPIcall = AppApiCalls(
                this,
                AppConstants.DASHBOARD_API,
                this
            )
            mAPIcall.dashboard(cus_id)
        } else {
            toast(getString(R.string.error_internet))
        }
    }

    override fun onAPICallCompleteListner(item: Any?, flag: String?, result: String) {
        if (flag.equals(AppConstants.DASHBOARD_API)) {
            pbLoadData.visibility = View.GONE
            Log.e(AppConstants.DASHBOARD_API, result)
            val jsonObject = JSONObject(result)
            val status = jsonObject.getString(AppConstants.STATUS)
            val messageCode = jsonObject.getString(AppConstants.MESSAGE)

            //   val token = jsonObject.getString(AppConstants.TOKEN)

            Log.e(AppConstants.STATUS, status)
            Log.e(AppConstants.MESSAGE, messageCode)
            if (status.contains(AppConstants.TRUE)) {

                try {
                    val cusData = jsonObject.getJSONArray("cusData")
                    for (i in 0 until cusData.length()) {
                        val notifyObjJson = cusData.getJSONObject(i)
                        if (notifyObjJson.getString("newaepskyc_status").equals("done")) {
                            val intent = Intent(this,AepsTransactionActivity::class.java)
                            val bundle = Bundle()
                            bundle.putString("amount",tvTotal.text.toString())
                            intent.putExtras(bundle)
                            startActivityForResult(intent,108)
                        } else {
                            toast("Please Complete KYC")
                            val intent = Intent(this, PaysprintsOnboardingActivity::class.java)
                            startActivity(intent)
                        }
                    }
                } catch(e: Exception) {
                    toast("Please Login Again")
                    val intent = Intent(this,AepsLoginActivity::class.java)
                    startActivity(intent)
                }

            } else {
                if (messageCode.equals(getString(R.string.error_expired_token))) {
                    AppCommonMethods.logoutOnExpiredDialog(this)
                } else {
                    toast(messageCode.trim())
                }
            }
        }

    }
}