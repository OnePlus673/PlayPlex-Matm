package com.playplexmatm.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.widget.AppCompatEditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplexmatm.R
import com.playplexmatm.aeps.activities_aeps.AepsTransactionActivity
import com.playplexmatm.aeps.activities_aeps.PaysprintsOnboardingActivity
import com.playplexmatm.aeps.authentication.AepsLoginActivity
import com.playplexmatm.aeps.model.UserModel
import com.playplexmatm.aeps.network_calls.AppApiCalls
import com.playplexmatm.model.customers.CustomerModel
import com.playplexmatm.model.paymentin.PaymentInModel
import com.playplexmatm.model.paymentmode.PaymentModeModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class AddEditPaymentInActivity : AppCompatActivity(), View.OnClickListener,
    AppApiCalls.OnAPICallCompleteListener {

    lateinit var mContext: AddEditPaymentInActivity
    lateinit var ivBack: ImageView
    lateinit var apiInterface: ApiInterface
    lateinit var pbLoadData: ProgressBar
    lateinit var tvSave: TextView
    lateinit var tvViewPdf: TextView
    lateinit var acUserName: AutoCompleteTextView
    lateinit var edtDate: AppCompatEditText
    lateinit var edtAmount: EditText
    lateinit var acPaymentMode: AutoCompleteTextView
    lateinit var edtNotes: EditText
    private var userList: ArrayList<CustomerModel> = arrayListOf()
    private var userStringList: ArrayList<String> = arrayListOf()
    private var paymentModeList: ArrayList<PaymentModeModel> = arrayListOf()
    private var paymentModeStringList: ArrayList<String> = arrayListOf()
    private var userId: String = ""
    private var PaymentModeName: String = ""
    private var selectedDate: String = ""
    lateinit var tvTitle: TextView
    private var mFrom: String = ""
    lateinit var paymentInModel: PaymentInModel
    var viewPdf = false

    lateinit var userModel: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_payment_in)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        //supportActionBar!!.hide()
        ivBack = findViewById(R.id.ivBack)
        pbLoadData = findViewById(R.id.pbLoadData)
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        tvSave = findViewById(R.id.tvSave)
        tvViewPdf = findViewById(R.id.tvViewPdf)
        acUserName = findViewById(R.id.acUserName)
        acPaymentMode = findViewById(R.id.acPaymentMode)
        edtDate = findViewById(R.id.edtDate)
        edtAmount = findViewById(R.id.edtAmount)
        edtNotes = findViewById(R.id.edtNotes)
        tvTitle = findViewById(R.id.tvTitle)

        if (intent.extras != null) {
            mFrom = intent.getStringExtra(Constants.mFrom)!!
            if (mFrom.equals(Constants.isEdit)) {
                tvTitle.text = "Update Money In"
                paymentInModel = Gson().fromJson(
                    intent.getStringExtra(Constants.paymentInModel),
                    PaymentInModel::class.java
                )
                setData(paymentInModel)
            } else {
                tvTitle.text = "Add Money In"
            }
        }

        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallGetUserAPI()
            mNetworkCallGetPaymentModeAPI()
        } else {
            Toast.makeText(
                mContext,
                mContext.resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setData(paymentInModel: PaymentInModel) {
        acUserName.setText(paymentInModel.user.name)
        edtDate.setText(paymentInModel.date)
        edtAmount.setText(paymentInModel.amount)
        edtNotes.setText(paymentInModel.notes)
        acPaymentMode.setText(paymentInModel.payment_mode.name)

        userId = paymentInModel.user.xid
        PaymentModeName = paymentInModel.payment_mode.xid
    }

    private fun addListner() {
        ivBack.setOnClickListener(this)
        tvSave.setOnClickListener(this)
        tvViewPdf.setOnClickListener(this)
        edtDate.setOnClickListener(this)

        acUserName.setOnTouchListener { v, event ->
            acUserName.showDropDown()
            false
        }


        acPaymentMode.setOnTouchListener { v, event ->
            acPaymentMode.showDropDown()
            false
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
                        if (mFrom.equals(Constants.isEdit)) {
                            mNetworkCallUpdatePaymentInAPI()
                        } else {
                            if(acPaymentMode.text.toString().equals("aeps",ignoreCase = true)) {
                                val islogin: Boolean =
                                    AppPrefs.getBooleanPref(AppConstants.IS_LOGIN, mContext)
                                if (islogin) {
                                    val gson = Gson()
                                    val json = AppPrefs.getStringPref(AppConstants.USER_MODEL, this)
                                    userModel = gson.fromJson(json, UserModel::class.java)

                                    dashboardApi(userModel.cus_mobile)
                                } else {
                                    val intent = Intent(this, AepsLoginActivity::class.java)
                                    startActivity(intent)
                                }
                            } else {
                                mNetworkCallAddPaymentInAPI()
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
                viewPdf = true
                if (InternetConnection.checkConnection(mContext)) {
                    if (isValidate()) {
                        if (mFrom.equals(Constants.isEdit)) {
                            mNetworkCallUpdatePaymentInAPI()
                        } else {
                            mNetworkCallAddPaymentInAPI()
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


    private fun showDatePicker(edtDate: AppCompatEditText) {
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

    private fun isValidate(): Boolean {
        var isValid = true
        if (userId.isEmpty()) {
            Toast.makeText(
                mContext,
                "please select user", Toast.LENGTH_SHORT
            ).show()
            isValid = false
        } else if (edtDate.text.toString().trim().isEmpty()) {
            Toast.makeText(
                mContext,
                "please select date", Toast.LENGTH_SHORT
            ).show()
            isValid = false
        } else if (PaymentModeName.isEmpty()) {
            Toast.makeText(
                mContext,
                "please select payment mode", Toast.LENGTH_SHORT
            ).show()
            isValid = false
        } else if (edtAmount.text.toString().trim().isEmpty()) {
            Toast.makeText(
                mContext,
                "please enter amount", Toast.LENGTH_SHORT
            ).show()
            isValid = false
        }
        return isValid
    }

    private fun mNetworkCallAddPaymentInAPI() {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.addPaymentIn(
            userId,
            "in",
            PaymentModeName,
            edtNotes.text.toString().trim(),
            edtDate.text.toString().trim(),
            edtAmount.text.toString().trim()
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            Log.e("Response",jsonObject.toString())

                            Toast.makeText(
                                mContext,
                                jsonObject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                            if(viewPdf.equals(true)) {
                                mNetworkCallPaymentPdfAPI(jsonObject.getJSONObject("data").getString("xid"))
                            } else {
                                val intent = Intent()
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }
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


    private fun mNetworkCallUpdatePaymentInAPI() {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.updatePaymentIn(
            paymentInModel.xid,
            userId,
            "in",
            PaymentModeName,
            edtNotes.text.toString().trim(),
            edtDate.text.toString().trim(),
            edtAmount.text.toString().trim()
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

                            if(viewPdf.equals(true)) {
                                mNetworkCallPaymentPdfAPI(jsonObject.getJSONObject("data").getString("xid"))
                            } else {
                                val intent = Intent()
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }

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

    private fun mNetworkCallPaymentPdfAPI(xid: String) {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.paymentPdf(xid)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.e("response",response.toString())
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONObject("data")
                            Log.e("data",data.getString("html"))
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
                return false;
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                createWebPrintJob(view!!)
            }
        })
        val myHtml: String = data
        webView.loadDataWithBaseURL(null, myHtml, "text/HTML", "UTF-8", null)
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

                            val intent = Intent(this, AepsTransactionActivity::class.java)
                            val bundle = Bundle()
                            bundle.putString("amount",edtAmount.text.toString())
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            108 -> {
                if(resultCode == Activity.RESULT_OK) {
                    val extra: Bundle = data!!.getBundleExtra("data")!!

                    mNetworkCallAddPaymentInAPI()
                }
            }
        }
    }

}