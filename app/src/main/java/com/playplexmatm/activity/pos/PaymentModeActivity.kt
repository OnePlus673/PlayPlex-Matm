package com.playplexmatm.activity.pos

import android.app.Activity
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplexmatm.R
import com.playplexmatm.activity.WebviewActivity
import com.playplexmatm.adapter.PaymentListAdapter
import com.playplexmatm.aeps.activities_aeps.AepsTransactionActivity
import com.playplexmatm.aeps.activities_aeps.PaysprintsOnboardingActivity
import com.playplexmatm.aeps.authentication.AepsLoginActivity
import com.playplexmatm.aeps.model.UserModel
import com.playplexmatm.aeps.network_calls.AppApiCalls
import com.playplexmatm.model.categoryofproducts.Items
import com.playplexmatm.model.paymentmode.PaymentModeModel
import com.playplexmatm.model.pos.PaymentModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.*
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.navigation_drawer.view.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PaymentModeActivity : AppCompatActivity(), View.OnClickListener,
    AppApiCalls.OnAPICallCompleteListener {

    lateinit var mContext: PaymentModeActivity
    lateinit var ivBack: ImageView
    lateinit var pbLoadData: ProgressBar
    lateinit var acPaymentMode: AutoCompleteTextView
    private var paymentModeList: ArrayList<PaymentModeModel> = arrayListOf()
    private var paymentModeStringList: ArrayList<String> = arrayListOf()
    lateinit var apiInterface: ApiInterface
    private var paymentModeName: String = ""
    private var paymentModeId: String = ""
    lateinit var tvPayingAmountValue: TextView
    lateinit var tvPaybleAmountValue: TextView
    lateinit var tvDueAmountValue: TextView
    lateinit var tvSave: TextView
    lateinit var edtNotes: EditText
    lateinit var edtAmount: EditText
    lateinit var rvPaymentMode: RecyclerView
    private var paymentModeAmount: Double = 0.0
    private var grandAmount: Double = 0.0
    lateinit var paymentListAdapter: PaymentListAdapter
    lateinit var tvAdd: TextView
    private var paymentFilterList: ArrayList<PaymentModel> = arrayListOf()
    private var posProductModelArrayList: java.util.ArrayList<Items> = arrayListOf()
    private var userId: String = ""
    private var dueAmountValue = 0.0
    private var dueAmountEditValue = 0.0
    private var payingAmountvalue = 0.0
    private var payingeditAmountvalue = 0.0
    private var paybleAmountvalue = 0.0
    private var payingtextvalue = 0.0
    lateinit var databaseHelper: DatabaseHelper


    lateinit var userModel: UserModel
    private var discount: String = ""
    private var shipping: String = ""
    var subtotal: Double = 0.0

    lateinit var from: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_mode)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        databaseHelper = DatabaseHelper(mContext)
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        ivBack = findViewById(R.id.ivBack)
        acPaymentMode = findViewById(R.id.acPaymentMode)
        pbLoadData = findViewById(R.id.pbLoadData)
        rvPaymentMode = findViewById(R.id.rvPaymentMode)
        tvPayingAmountValue = findViewById(R.id.tvPayingAmountValue)
        tvPaybleAmountValue = findViewById(R.id.tvPaybleAmountValue)
        tvDueAmountValue = findViewById(R.id.tvDueAmountValue)
        edtNotes = findViewById(R.id.edtNotes)
        edtAmount = findViewById(R.id.edtAmount)
        tvAdd = findViewById(R.id.tvAdd)
        tvSave = findViewById(R.id.tvSave)


        if (intent.extras != null) {
            posProductModelArrayList =
                (intent.getSerializableExtra("posProductModel") as java.util.ArrayList<Items>?)!!

            paymentModeAmount = intent.getDoubleExtra("PaymontModeAmount", 0.0)
            grandAmount = intent.getDoubleExtra("dueAmount", 0.0)
            userId = intent.getStringExtra("userId")!!
            discount = intent.getStringExtra("discount")!!
            shipping = intent.getStringExtra("shipping")!!
            subtotal = intent.getDoubleExtra("subtotal",0.0)
            from = intent.getStringExtra("from")!!
            tvPaybleAmountValue.text = paymentModeAmount.toString()
            tvDueAmountValue.text = grandAmount.toString()

            dueAmountValue = grandAmount
            paybleAmountvalue = grandAmount
        }

        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallGetPaymentModeAPI()
        } else {
            Toast.makeText(
                mContext,
                mContext.resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addListner() {
        ivBack.setOnClickListener(this)
        tvSave.setOnClickListener(this)
        tvAdd.setOnClickListener(this)

        acPaymentMode.setOnTouchListener { v, event ->
            acPaymentMode.showDropDown()
            false
        }


        edtAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                /*if (s!!.isNotEmpty()) {
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
                }*/

                if (s!!.isNotEmpty()) {
                    payingeditAmountvalue = edtAmount.text.toString().trim().toDouble()
                    payingtextvalue = tvPayingAmountValue.text.toString().trim().toDouble()
                    tvPayingAmountValue.text = payingeditAmountvalue.toString()
                    dueAmountEditValue = (dueAmountValue - payingeditAmountvalue)
                    tvDueAmountValue.text = dueAmountEditValue.toString()
                } else {
                    payingAmountvalue = 0.0
                    tvPayingAmountValue.setText(payingeditAmountvalue.toString())
                    tvDueAmountValue.text = dueAmountEditValue.toString()
                }


            }

            override fun afterTextChanged(s: Editable?) {
            }

        })


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.tvAdd -> {

                if (InternetConnection.checkConnection(mContext)) {
                    if (isValidate()) {

                        paymentFilterList.add(
                            PaymentModel(
                                paymentModeId,
                                paymentModeName,
                                edtAmount.text.toString().trim().toDouble(),
                                edtNotes.text.toString().trim()
                            )
                        )
                        rvPaymentMode.layoutManager = LinearLayoutManager(mContext)
                        rvPaymentMode.setHasFixedSize(true)
                        paymentListAdapter = PaymentListAdapter(mContext, paymentFilterList)
                        rvPaymentMode.adapter = paymentListAdapter
                        paymentListAdapter.notifyDataSetChanged()

//                        edtAmount.setText("")
//                        edtNotes.setText("")
//                        paymentModeName = ""
                        acPaymentMode.hint = "Payment Mode"
                        dueAmountValue = tvDueAmountValue.text.toString().trim().toDouble()


                    }
                } else {
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.str_check_internet_connections),
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }
            R.id.tvSave -> {
                if (isAddValidate()) {
                    if(from.equals("direct")) {
                        if (acPaymentMode.text.toString().equals("aeps", ignoreCase = true)) {
                            val islogin: Boolean =
                                AppPrefs.getBooleanPref(AppConstants.IS_LOGIN, mContext)
                            if (islogin) {
                                val gson = Gson()
                                val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, this)
                                userModel = gson.fromJson(json, UserModel::class.java)

                                dashboardApi(userModel.cus_mobile)
                            } else {
                                val intent = Intent(this,AepsLoginActivity::class.java)
                                startActivity(intent)
                            }
                        } else {
                            mNetworkCallPosSaveAPI(0)
                        }
                    } else {
                        if (acPaymentMode.text.toString().equals("aeps", ignoreCase = true)) {
                            val islogin: Boolean =
                                AppPrefs.getBooleanPref(AppConstants.IS_LOGIN, mContext)
                            if (islogin) {
                                val gson = Gson()
                                val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, this)
                                userModel = gson.fromJson(json, UserModel::class.java)

                                dashboardApi(userModel.cus_mobile)
                            } else {
                                val intent = Intent(this,AepsLoginActivity::class.java)
                                startActivity(intent)
                            }
                        } else {
                            mNetworkCallPosSaveAPI(1)
                        }
                    }
                }
            }
        }
    }

    private fun isValidate(): Boolean {
        var isValid = true
        if (paymentModeName.isEmpty()) {
            Toast.makeText(mContext, "Please select payment mode", Toast.LENGTH_SHORT).show()
            isValid = false
        } else if (edtAmount.text.toString().trim().isEmpty()) {
            Toast.makeText(mContext, "Please enter amount", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        return isValid
    }

    private fun isAddValidate(): Boolean {
        var isValid = true
        if (paymentFilterList.size == 0) {
            Toast.makeText(mContext, "Please select payment mode and amount", Toast.LENGTH_SHORT)
                .show()
            isValid = false
        }
        return isValid
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


    private fun mNetworkCallPosSaveAPI(is_product_invoice: Int) {
        val paymentArray = JSONArray()
        val detailsArray = JSONObject()
        detailsArray.put("is_product_invoice",is_product_invoice)
        detailsArray.put("discount_type", "percentage")
        detailsArray.put("discount_value", 0.0)
        if (discount.isEmpty()) {
            detailsArray.put("discount", 0.0)
        } else {
            detailsArray.put("discount", discount.toFloat())
        }

        if (shipping.isEmpty()) {
            detailsArray.put("shipping", 0.0)

        } else {
            detailsArray.put("shipping", shipping.toFloat())
        }


        detailsArray.put("subtotal", subtotal)
        detailsArray.put("total", tvPaybleAmountValue.text.toString().trim().toFloat())
        detailsArray.put("user_id", userId)
        detailsArray.put("tax_rate", 0.0)
        detailsArray.put("tax_amount", 0.0)


        Log.e("details", "=" + detailsArray)


        val jsonArray = JSONArray()
        for (i in 0 until posProductModelArrayList.size) {
            val element = JSONObject()
            element.put("item_id", posProductModelArrayList.get(i).item_id)
            element.put("xid", posProductModelArrayList.get(i).xid)
            if (discount.isEmpty()) {
                element.put("discount_rate", 0.0)
            } else {
                element.put("discount_rate", discount.toFloat())
            }

            if (discount.isEmpty()) {
                element.put("total_discount", 0.0)
            } else {
                element.put(
                    "total_discount", discount.toFloat()
                )
            }

            element.put("x_tax_id", "")
            element.put("tax_type", "")
            element.put("tax_rate", 0.0)
            element.put("total_tax", 0.0)
            element.put("x_unit_id", posProductModelArrayList.get(i).x_unit_id)
            element.put("unit_price", posProductModelArrayList.get(i).unit_price.toFloat())
            element.put(
                "single_unit_price", posProductModelArrayList.get(i).single_unit_price.toFloat()
            )
            element.put(
                "subtotal", posProductModelArrayList.get(i).single_unit_price.toFloat()
            )
            element.put("quantity", posProductModelArrayList.get(i).quantity.toFloat())
            jsonArray.put(element)
        }

        Log.e("JSONARRAYVAALUE", "=" + Gson().toJson(jsonArray))


        if(from.equals("direct")) {
            val element = JSONObject()
            element.put("payment_mode_id", "")
            element.put("amount", "")
            element.put("notes", "")

            paymentArray.put(element)
        } else {
            for (i in 0 until paymentFilterList.size) {
                val element = JSONObject()
                element.put("payment_mode_id", paymentFilterList[i].payment_mode_id)
                element.put("amount", paymentFilterList[i].amount)
                element.put("notes", paymentFilterList[i].notes)

                paymentArray.put(element)
            }
        }

        Log.e("paymentArray", "=" + paymentArray)


        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.posSave(paymentArray, detailsArray, jsonArray)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        Log.e("Response",response.body().toString())
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONObject("data")

                            startActivity(Intent(mContext,WebviewActivity::class.java).putExtra("data",data.optString("invoice_html")))
                            databaseHelper.delAll(mContext)

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

    private fun posSave() {
        val paymentArray = JSONArray()
        val detailsArray = JSONObject()
        detailsArray.put("discount_type", "percentage")
        detailsArray.put("discount_value", 0.0)
        if (discount.isEmpty()) {
            detailsArray.put("discount", 0.0)
        } else {
            detailsArray.put("discount", discount.toFloat())
        }

        if (shipping.isEmpty()) {
            detailsArray.put("shipping", 0.0)

        } else {
            detailsArray.put("shipping", shipping.toFloat())

        }


        detailsArray.put("subtotal", tvPaybleAmountValue.text.toString().trim().toFloat())
        detailsArray.put("user_id", userId)
        detailsArray.put("tax_rate", 0.0)
        detailsArray.put("tax_amount", 0.0)


        Log.e("details", "=" + detailsArray)
        Log.e("paymentArray", "=" + paymentArray)


        val jsonArray = JSONArray()
        for (i in 0 until posProductModelArrayList.size) {
            val element = JSONObject()
            element.put("item_id", posProductModelArrayList.get(i).item_id)
            element.put("xid", posProductModelArrayList.get(i).xid)
            if (discount.isEmpty()) {
                element.put("discount_rate", 0.0)
            } else {
                element.put("discount_rate", discount.toFloat())
            }

            if (discount.isEmpty()) {
                element.put("total_discount", 0.0)
            } else {
                element.put(
                    "total_discount", discount.toFloat()
                )
            }

            element.put("x_tax_id", "")
            element.put("tax_type", "")
            element.put("tax_rate", 0.0)
            element.put("total_tax", 0.0)
            element.put("x_unit_id", posProductModelArrayList.get(i).x_unit_id)
            element.put("unit_price", posProductModelArrayList.get(i).unit_price.toFloat())
            element.put(
                "single_unit_price", posProductModelArrayList.get(i).single_unit_price.toFloat()
            )
            element.put(
                "subtotal", posProductModelArrayList.get(i).single_unit_price.toFloat()
            )
            element.put("quantity", posProductModelArrayList.get(i).quantity.toFloat())
            jsonArray.put(element)
        }
        Log.e("JSONARRAYVAALUE", "=" + Gson().toJson(jsonArray))

        for (i in 0 until paymentFilterList.size) {
            val element = JSONObject()
            element.put("payment_mode_id", paymentFilterList[i].payment_mode_id)
            element.put("amount", paymentFilterList[i].amount)
            element.put("notes", paymentFilterList[i].notes)

            paymentArray.put(element)
        }

        pbLoadData.visibility = View.VISIBLE

        val bundle = Bundle()
        bundle.putString("jsonArray",jsonArray.toString())
        bundle.putString("paymentArray",paymentArray.toString())
        bundle.putString("detailsArray",detailsArray.toString())
        bundle.putString("amount",payingeditAmountvalue.toInt().toString())
        val intent = Intent(this,AepsTransactionActivity::class.java)
        intent.putExtras(bundle)
        startActivityForResult(intent,108)
    }


    private fun posSaveLogin() {
        val paymentArray = JSONArray()
        val detailsArray = JSONObject()
        detailsArray.put("discount_type", "percentage")
        detailsArray.put("discount_value", 0.0)
        if (discount.isEmpty()) {
            detailsArray.put("discount", 0.0)
        } else {
            detailsArray.put("discount", discount.toFloat())
        }

        if (shipping.isEmpty()) {
            detailsArray.put("shipping", 0.0)

        } else {
            detailsArray.put("shipping", shipping.toFloat())
        }


        detailsArray.put("subtotal", tvPaybleAmountValue.text.toString().trim().toFloat())
        detailsArray.put("user_id", userId)
        detailsArray.put("tax_rate", 0.0)
        detailsArray.put("tax_amount", 0.0)


        Log.e("details", "=" + detailsArray)
        Log.e("paymentArray", "=" + paymentArray)


        val jsonArray = JSONArray()
        for (i in 0 until posProductModelArrayList.size) {
            val element = JSONObject()
            element.put("item_id", posProductModelArrayList.get(i).item_id)
            element.put("xid", posProductModelArrayList.get(i).xid)
            if (discount.isEmpty()) {
                element.put("discount_rate", 0.0)
            } else {
                element.put("discount_rate", discount.toFloat())
            }

            if (discount.isEmpty()) {
                element.put("total_discount", 0.0)
            } else {
                element.put(
                    "total_discount", discount.toFloat()
                )
            }

            element.put("x_tax_id", "")
            element.put("tax_type", "")
            element.put("tax_rate", 0.0)
            element.put("total_tax", 0.0)
            element.put("x_unit_id", posProductModelArrayList.get(i).x_unit_id)
            element.put("unit_price", posProductModelArrayList.get(i).unit_price.toFloat())
            element.put(
                "single_unit_price", posProductModelArrayList.get(i).single_unit_price.toFloat()
            )
            element.put(
                "subtotal", posProductModelArrayList.get(i).single_unit_price.toFloat()
            )
            element.put("quantity", posProductModelArrayList.get(i).quantity.toFloat())
            jsonArray.put(element)
        }

        Log.e("JSONARRAYVAALUE", "=" + Gson().toJson(jsonArray))



        for (i in 0 until paymentFilterList.size) {
            val element = JSONObject()
            element.put("payment_mode_id", paymentFilterList[i].payment_mode_id)
            element.put("amount", paymentFilterList[i].amount)
            element.put("notes", paymentFilterList[i].notes)

            paymentArray.put(element)
        }

        pbLoadData.visibility = View.VISIBLE

        val bundle = Bundle()
        bundle.putString("jsonArray",jsonArray.toString())
        bundle.putString("paymentArray",paymentArray.toString())
        bundle.putString("detailsArray",detailsArray.toString())
        bundle.putString("amount",payingeditAmountvalue.toInt().toString())
        val intent = Intent(this,AepsLoginActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }


    private fun setData(data: String) {
        val webView = WebView(this)
        webView.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?, request: WebResourceRequest?
            ): Boolean {
                return false;
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                createWebPrintJob(view!!)
            }
        })
        val myHtml: String = data
        webView.loadDataWithBaseURL(null, myHtml, "text/HTML", "UTF-8", null)
    }

    private fun createWebPrintJob(webView: WebView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val printManager = mContext.getSystemService(Context.PRINT_SERVICE) as PrintManager
            val printAdapter = webView.createPrintDocumentAdapter("MyDocument")
            printManager.print(" My Print Job", printAdapter, PrintAttributes.Builder().build())

        } else {
            // SHOW MESSAGE or UPDATE UI
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
                            posSave()
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 108) {
            if(resultCode == Activity.RESULT_OK) {
                val extra: Bundle = data!!.getBundleExtra("data")!!

                if(from.equals("direct")) {
                    mNetworkCallPosSaveAPI(0)
                } else {
                    mNetworkCallPosSaveAPI(1)
                }
            }
        }
    }


}