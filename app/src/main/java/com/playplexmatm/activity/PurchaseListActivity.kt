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
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplexmatm.R
import com.playplexmatm.adapter.PurchaseAdapter
import com.playplexmatm.model.purchasemodel.PurchaseModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.InternetConnection
import com.playplexmatm.util.PaginationScrollListener
import com.playplexmatm.util.Util
import kotlinx.android.synthetic.main.activity_purchase_list.*
import kotlinx.android.synthetic.main.activity_purchase_list.edtFromDate
import kotlinx.android.synthetic.main.activity_purchase_list.edtSearch
import kotlinx.android.synthetic.main.activity_purchase_list.edtToDate
import kotlinx.android.synthetic.main.activity_sale_list.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class PurchaseListActivity : AppCompatActivity(), View.OnClickListener, PurchaseAdapter.onClick {

    lateinit var mContext: PurchaseListActivity

    lateinit var ivBack: AppCompatImageView
    lateinit var tvTitle: AppCompatTextView
    lateinit var rvPurchase: RecyclerView
    lateinit var tvNoPurchase: TextView
    lateinit var tvNewPurchase: TextView
    lateinit var pbLoadData: ProgressBar
    lateinit var apiInterface: ApiInterface
    private var purchaseAraryList: ArrayList<PurchaseModel> = arrayListOf()
    lateinit var purchaseAdapter: PurchaseAdapter

    lateinit var bottomSheetDialog: BottomSheetDialog

    lateinit var pbBottomLoadData: ProgressBar
    private var isloading: Boolean = false
    private var islastpage: Boolean = false
    private var currentPage = 1
    private var lastPage = 1
    private var offset: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase_list)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        pbLoadData = findViewById(R.id.pbLoadData)
        ivBack = findViewById(R.id.ivBack)
        rvPurchase = findViewById(R.id.rvPurchase)
        tvNewPurchase = findViewById(R.id.tvNewPurchaseList)
        tvNoPurchase = findViewById(R.id.tvNoPurchaseList)
        pbBottomLoadData = findViewById(R.id.pbBottomLoadData)

        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallPurchaseListAPI(currentPage,"")
        } else {
            Toast.makeText(
                mContext, mContext.resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        setAdapter(purchaseAraryList)

        setPaginationData()

    }

    private fun addListner() {
        ivBack.setOnClickListener(this)
        tvNewPurchase.setOnClickListener(this)
        edtFromDate.setOnClickListener {
            showFromDatePicker(edtFromDate)
        }

        edtToDate.setOnClickListener {
            showToDatePicker(edtToDate)
        }
    }

    private fun setPaginationData() {
        rvPurchase.addOnScrollListener(object :
            PaginationScrollListener(rvPurchase.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                if (currentPage < lastPage) {
                    currentPage += 1
                    isloading = true
                    mNetworkCallPurchaseListAPI(currentPage,"")
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
            R.id.tvNewPurchaseList -> {
                startActivityForResult(
                    Intent(mContext, AddPurchaseListActivity::class.java),
                    201
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 201) {
            if (resultCode == Activity.RESULT_OK) {
                currentPage = 1
                mNetworkCallPurchaseListAPI(currentPage,"")
            }
        }
    }

    private fun mNetworkCallPurchaseListAPI(newcurrentPage: Int, date: String) {
        if (newcurrentPage > 1) {
            offset = (newcurrentPage - 1) * 10
            pbBottomLoadData.visibility = View.VISIBLE
        }
        if (newcurrentPage == 1) {
            offset = (newcurrentPage - 1) * 10
            pbLoadData.visibility = View.VISIBLE
            purchaseAraryList.clear()
        }
        val call = apiInterface.getPurchase("id desc","",offset,10, date)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    pbBottomLoadData.visibility = View.GONE

                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())

                        Log.e("Purchase List",jsonObject.toString())

                        if (jsonObject.optBoolean("status")) {
                            isloading = false
                            val data = jsonObject.optJSONArray("data")
                            purchaseAraryList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<PurchaseModel?>?>() {}.type
                                )
                            )

                            currentPage = newcurrentPage
                            lastPage = Math.ceil(
                                (jsonObject.optJSONObject("meta")!!.optJSONObject("paging")!!
                                    .optString("total").toDouble() / 10.00)
                            ).toInt()

                            Log.e("lastPage", "=" + lastPage)

                            if (currentPage == lastPage) {
                                islastpage = true
                            }

                            if (purchaseAraryList.size > 0) {
                                rvPurchase.visibility = View.VISIBLE
                                tvNoPurchase.visibility = View.GONE
                                setAdapter(purchaseAraryList)
                            } else {
                                if (currentPage == 1) {
                                    rvPurchase.visibility = View.GONE
                                    tvNoPurchase.visibility = View.VISIBLE
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
                            if (currentPage == 1) {
                                rvPurchase.visibility = View.GONE
                                tvNoPurchase.visibility = View.VISIBLE
                                Toast.makeText(
                                    mContext,
                                    jsonObject.optString("message"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

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

    private fun setAdapter(purchaseAraryList: ArrayList<PurchaseModel>) {

        rvPurchase.layoutManager = LinearLayoutManager(mContext)
        rvPurchase.setHasFixedSize(true)
        purchaseAdapter = PurchaseAdapter(mContext, purchaseAraryList, this)
        rvPurchase.adapter = purchaseAdapter
        purchaseAdapter.notifyDataSetChanged()
    }

    override fun onDeleteClick(purchaseModel: PurchaseModel, position: Int) {
        openSaleDialog(purchaseModel, position)
    }

    private fun openSaleDialog(purchaseModel: PurchaseModel, position: Int) {
        val builder = AlertDialog.Builder(mContext)
        builder.setMessage("Are you sure you want to Delete Purchase?")
        builder.setPositiveButton("YES") { dialog, which ->
            if (InternetConnection.checkConnection(mContext)) {
                deletepurchaseList(dialog, purchaseModel, position)
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

    private fun deletepurchaseList(
        dialog: DialogInterface,
        purchaseModel: PurchaseModel,
        position: Int
    ) {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.deletePurchase(purchaseModel.xid)
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
                            if (purchaseAraryList.size > 0) {
                                purchaseAraryList.removeAt(position)
                                currentPage = 1
                                mNetworkCallPurchaseListAPI(currentPage,"")
                            } else {
                                rvPurchase.visibility = View.GONE
                                tvNoPurchase.visibility = View.VISIBLE
                            }
                        } else {
                            rvPurchase.visibility = View.GONE
                            tvNoPurchase.visibility = View.VISIBLE
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
                    mNetworkCallPurchaseListAPI(currentPage,edtFromDate.text.toString()+","+edtToDate.text.toString())
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
                    mNetworkCallPurchaseListAPI(currentPage,edtFromDate.text.toString()+","+edtToDate.text.toString())
                }
            },
                year,
                month,
                day
            )
        datePickerDialog.show()
    }

    fun filter(text: String) {
        val temp: MutableList<PurchaseModel> = ArrayList()
        for (d in purchaseAraryList) {


            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.invoice_number.contains(text,ignoreCase = true)) {
                temp.add(d)
            }
        }
        //update recyclerview
        purchaseAdapter.updateList(temp)
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