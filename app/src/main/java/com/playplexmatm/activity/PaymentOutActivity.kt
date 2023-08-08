package com.playplexmatm.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
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
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplexmatm.R
import com.playplexmatm.adapter.PaymentOutAdapter
import com.playplexmatm.model.paymentout.PaymentOutModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.Constants
import com.playplexmatm.util.InternetConnection
import com.playplexmatm.util.PaginationScrollListener
import com.playplexmatm.util.Util
import kotlinx.android.synthetic.main.activity_payment_in.*
import kotlinx.android.synthetic.main.activity_payment_out.*
import kotlinx.android.synthetic.main.activity_payment_out.edtFromDate
import kotlinx.android.synthetic.main.activity_payment_out.edtSearch
import kotlinx.android.synthetic.main.activity_payment_out.edtToDate
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class PaymentOutActivity : AppCompatActivity(), View.OnClickListener, PaymentOutAdapter.onClick {

    lateinit var mContext: PaymentOutActivity
    lateinit var ivBack: ImageView
    lateinit var apiInterface: ApiInterface
    lateinit var rvPaymentOut: RecyclerView
    lateinit var tvNoPayment: TextView
    lateinit var tvNewPaymentOut: TextView
    lateinit var pbLoadData: ProgressBar
    private var paymentList: ArrayList<PaymentOutModel> = arrayListOf()
    lateinit var paymentOutAdapter: PaymentOutAdapter
    lateinit var pbBottomLoadData: ProgressBar
    private var isloading: Boolean = false
    private var islastpage: Boolean = false
    private var currentPage = 1
    private var lastPage = 1
    private var offset: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_out)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        //supportActionBar!!.hide()
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        pbLoadData = findViewById(R.id.pbLoadData)
        ivBack = findViewById(R.id.ivBack)
        rvPaymentOut = findViewById(R.id.rvPaymentOut)
        tvNoPayment = findViewById(R.id.tvNoPayment)
        tvNewPaymentOut = findViewById(R.id.tvNewPaymentOut)
        pbBottomLoadData = findViewById(R.id.pbBottomLoadData)

        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallPaymentOutAPI(currentPage,"")
        } else {
            Toast.makeText(
                mContext, mContext.resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        setAdapter(paymentList)

        setPaginationData()
    }

    private fun addListner() {
        ivBack.setOnClickListener(this)
        tvNewPaymentOut.setOnClickListener(this)


        edtFromDate.setOnClickListener {
            showFromDatePicker(edtFromDate)
        }

        edtToDate.setOnClickListener {
            showToDatePicker(edtToDate)
        }

    }


    private fun setPaginationData() {
        rvPaymentOut.addOnScrollListener(object :
            PaginationScrollListener(rvPaymentOut.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                if (currentPage < lastPage) {
                    currentPage += 1
                    isloading = true
                    mNetworkCallPaymentOutAPI(currentPage,"")
                }
            }

            override fun isLoading(): Boolean {

                return isloading
            }

            override fun isLastPage(): Boolean {
                return islastpage
            }
        })
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.tvNewPaymentOut -> {
                startActivityForResult(
                    Intent(mContext, AddEditPaymentOutActivity::class.java)
                        .putExtra(Constants.mFrom, Constants.isAdd), 201
                )

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 201) {
            if (resultCode == Activity.RESULT_OK) {
                currentPage = 1
                mNetworkCallPaymentOutAPI(currentPage,"")
            }
        }
    }

    private fun mNetworkCallPaymentOutAPI(newcurrentPage: Int, date: String) {
        if (newcurrentPage > 1) {
            offset = (newcurrentPage - 1) * 10
            pbBottomLoadData.visibility = View.VISIBLE
        }
        if (newcurrentPage == 1) {
            offset = (newcurrentPage - 1) * 10
            pbLoadData.visibility = View.VISIBLE
            paymentList.clear()
        }
        val call = apiInterface.getPaymentOut("id desc", "=", offset, 10, date)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    pbBottomLoadData.visibility = View.GONE

                    if (response.code() == 200) {
                        val jsonobject = JSONObject(response.body().toString())
                        if (jsonobject.optBoolean("status")) {
                            isloading = false
                            val data = jsonobject.optJSONArray("data")
                            paymentList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<PaymentOutModel?>?>() {}.type
                                )
                            )
                            currentPage = newcurrentPage
                            lastPage = Math.ceil(
                                (jsonobject.optJSONObject("meta")!!.optJSONObject("paging")!!
                                    .optString("total").toDouble() / 10.00)
                            ).toInt()

                            Log.e("lastPage", "=" + lastPage)

                            if (currentPage == lastPage) {
                                islastpage = true
                            }

                            if (paymentList.size > 0) {


                                if (paymentList.size > 0) {
                                    rvPaymentOut.visibility = View.VISIBLE
                                    tvNoPayment.visibility = View.GONE
                                    setAdapter(paymentList)

                                } else {
                                    if (currentPage == 1) {
                                        rvPaymentOut.visibility = View.GONE
                                        tvNoPayment.visibility = View.VISIBLE
                                    }
                                }


                            } else {
                                if (currentPage == 1) {
                                    rvPaymentOut.visibility = View.GONE
                                    tvNoPayment.visibility = View.VISIBLE
                                }

                            }
                        }

                        edtSearch.addTextChangedListener(object : TextWatcher {
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
                                filter(s.toString())
                            }
                        })

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

    private fun setAdapter(paymentList: ArrayList<PaymentOutModel>) {
        rvPaymentOut.layoutManager = LinearLayoutManager(mContext)
        rvPaymentOut.setHasFixedSize(true)
        paymentOutAdapter = PaymentOutAdapter(mContext, paymentList, this)
        rvPaymentOut.adapter = paymentOutAdapter
        paymentOutAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(paymentOutModel: PaymentOutModel, position: Int) {
        startActivityForResult(
            Intent(mContext, AddEditPaymentOutActivity::class.java)
                .putExtra(Constants.mFrom, Constants.isEdit)
                .putExtra(Constants.paymentOutModel, Gson().toJson(paymentOutModel)), 201
        )
    }

    override fun onDeleteClick(paymentOutModel: PaymentOutModel, position: Int) {
        openPaymentOutDialog(paymentOutModel, position)
    }

    override fun onPrintClick(paymentOutModel: PaymentOutModel, position: Int) {
        if(paymentOutModel != null) {
            mNetworkCallPaymentPdfAPI(paymentOutModel.xid)
        }
    }


    private fun openPaymentOutDialog(paymentOutModel: PaymentOutModel, position: Int) {
        val builder = AlertDialog.Builder(mContext)
        builder.setMessage("Are you sure you want to delete PaymentOut?")
        builder.setPositiveButton("YES") { dialog, which ->
            if (InternetConnection.checkConnection(mContext)) {
                deletePaymentOut(dialog, paymentOutModel, position)
            } else {
                Toast.makeText(
                    mContext,
                    mContext.resources.getString(R.string.str_check_internet_connections),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        builder.setNegativeButton(
            "NO"
        ) { dialog, which -> // Do nothing
            dialog.dismiss()
        }
        val alert = builder.create()
        alert.show()
    }

    private fun deletePaymentOut(
        dialog: DialogInterface,
        paymentOutModel: PaymentOutModel,
        position: Int
    ) {
        pbLoadData.visibility = View.VISIBLE
        Log.e("xid",paymentOutModel.xid)
        val call = apiInterface.deletePaymentOUt(paymentOutModel.xid)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonobject = JSONObject(response.body().toString())
                        if (jsonobject.optBoolean("status")) {
                            Toast.makeText(
                                mContext,
                                jsonobject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                            if (paymentList.size > 0) {
                                paymentList.removeAt(position)
                                currentPage = 1
                                mNetworkCallPaymentOutAPI(currentPage,"")
                            } else {
                                rvPaymentOut.visibility = View.GONE
                                tvNoPayment.visibility = View.VISIBLE
                            }
                        } else {

                            Toast.makeText(
                                mContext, jsonobject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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

    private fun showFromDatePicker(edtDate: EditText) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        var datePickerDialog =
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                val mmMonth = mMonth + 1
                val date = "$mYear-$mmMonth-$mDay"
                edtFromDate.setText(date)
                if(edtToDate.text.toString().isNullOrEmpty()) {
                    Toast.makeText(this, "Select To Date", Toast.LENGTH_SHORT).show()
                } else {
                    mNetworkCallPaymentOutAPI(currentPage,edtFromDate.text.toString()+","+edtToDate.text.toString())
                }
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun showToDatePicker(edtDate: EditText) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        var datePickerDialog =
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                val mmMonth = mMonth + 1
                val date = "$mYear-$mmMonth-$mDay"
                edtToDate.setText(date)
                if(edtFromDate.text.toString().isNullOrEmpty()) {
                    Toast.makeText(this, "Select From Date", Toast.LENGTH_SHORT).show()
                } else {
                    mNetworkCallPaymentOutAPI(currentPage,edtFromDate.text.toString()+","+edtToDate.text.toString())
                }
            },
                year,
                month,
                day
            )
        datePickerDialog.show()
    }

    fun filter(text: String) {
        val temp: MutableList<PaymentOutModel> = ArrayList()
        for (d in paymentList) {


            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.user.name.contains(text,ignoreCase = true) || d.amount.contains(text,ignoreCase = true)) {
                temp.add(d)
            }
        }
        //update recyclerview
        paymentOutAdapter.updateList(temp)
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
}