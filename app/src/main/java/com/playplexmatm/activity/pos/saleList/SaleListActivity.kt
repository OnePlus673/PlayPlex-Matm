package com.playplexmatm.activity.pos.saleList

import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplexmatm.R
import com.playplexmatm.adapter.SaleListAdapter
import com.playplexmatm.model.saleList.SaleListModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.InternetConnection
import com.playplexmatm.util.PaginationScrollListener
import com.playplexmatm.util.Util
import kotlinx.android.synthetic.main.activity_add_sale.*
import kotlinx.android.synthetic.main.activity_sale_list.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class SaleListActivity : AppCompatActivity(), View.OnClickListener, SaleListAdapter.onClick {

    lateinit var mContext: SaleListActivity
    lateinit var ivBack: AppCompatImageView
    lateinit var tvTitle: AppCompatTextView
    lateinit var rvSaleList: RecyclerView
    lateinit var tvNoSaleList: TextView
    lateinit var tvNewSaleList: TextView
    lateinit var pbLoadData: ProgressBar
    lateinit var pbBottomLoadData: ProgressBar
    lateinit var apiInterface: ApiInterface
    private var salesArrayList: ArrayList<SaleListModel> = arrayListOf()
    lateinit var saleListAdapter: SaleListAdapter

    private var selectedDate: String = ""

    private var isloading: Boolean = false
    private var islastpage: Boolean = false
    private var currentPage = 1
    private var lastPage = 1
    private var offset: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sale_list)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        pbLoadData = findViewById(R.id.pbLoadData)
        ivBack = findViewById(R.id.ivBack)
        rvSaleList = findViewById(R.id.rvSaleList)
        tvNewSaleList = findViewById(R.id.tvNewSaleList)
        tvNoSaleList = findViewById(R.id.tvNoSaleList)
        pbBottomLoadData = findViewById(R.id.pbBottomLoadData)

        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallSaleListAPI(currentPage,"")
        } else {
            Toast.makeText(
                mContext, mContext.resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        setAdapter(salesArrayList)

        setPaginationData()

    }


    private fun setPaginationData() {
        rvSaleList.addOnScrollListener(object :
            PaginationScrollListener(rvSaleList.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                if (currentPage < lastPage) {
                    currentPage += 1
                    isloading = true
                    mNetworkCallSaleListAPI(currentPage,"")
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


    private fun addListner() {
        ivBack.setOnClickListener(this)
        tvNewSaleList.setOnClickListener(this)

        edtFromDate.setOnClickListener {
            showFromDatePicker(edtFromDate)
        }

        edtToDate.setOnClickListener {
            showToDatePicker(edtToDate)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.tvNewSaleList -> {
                startActivityForResult(Intent(mContext, AddSaleActivity::class.java), 201)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 201) {
            if (resultCode == Activity.RESULT_OK) {
                currentPage=1
                mNetworkCallSaleListAPI(currentPage,"")
            }
        }
    }

    private fun mNetworkCallSaleListAPI(newcurrentPage: Int, date: String) {

        if (newcurrentPage > 1) {
            offset = (newcurrentPage - 1) * 10
            pbBottomLoadData.visibility = View.VISIBLE
        }
        if (newcurrentPage == 1) {
            offset = (newcurrentPage - 1) * 10
            pbLoadData.visibility = View.VISIBLE
            salesArrayList.clear()
        }


        val call = apiInterface.getSales("id desc", "", offset, 10, date)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    pbBottomLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            isloading = false
                            val data = jsonObject.optJSONArray("data")
                            salesArrayList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<SaleListModel?>?>() {}.type
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


                            if (salesArrayList.size > 0) {
                                rvSaleList.visibility = View.VISIBLE
                                tvNoSaleList.visibility = View.GONE
                                saleListAdapter.notifyDataSetChanged()
                            } else {

                                if (currentPage == 1) {
                                    rvSaleList.visibility = View.GONE
                                    tvNoSaleList.visibility = View.VISIBLE
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
                                rvSaleList.visibility = View.GONE
                                tvNoSaleList.visibility = View.VISIBLE
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
                pbBottomLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setAdapter(saleList: ArrayList<SaleListModel>) {

        rvSaleList.layoutManager = LinearLayoutManager(mContext)
        rvSaleList.setHasFixedSize(true)
        saleListAdapter = SaleListAdapter(mContext, saleList, this)
        rvSaleList.adapter = saleListAdapter
        saleListAdapter.notifyDataSetChanged()
    }

    override fun onDeleteClick(saleListModel: SaleListModel, position: Int) {
        openSaleDialog(saleListModel, position)
    }

    private fun openSaleDialog(saleListModel: SaleListModel, position: Int) {
        val builder = AlertDialog.Builder(mContext)
        builder.setMessage("Are you sure you want to Delete Sale List?")
        builder.setPositiveButton("YES") { dialog, which ->
            if (InternetConnection.checkConnection(mContext)) {
                deleteSaleList(dialog, saleListModel, position)
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

    private fun deleteSaleList(
        dialog: DialogInterface,
        saleListModel: SaleListModel,
        position: Int
    ) {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.deleteSales(saleListModel.xid)
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
                            if (salesArrayList.size > 0) {
                                salesArrayList.removeAt(position)
                                currentPage=1
                                mNetworkCallSaleListAPI(currentPage,"")
                            } else {
                                rvSaleList.visibility = View.GONE
                                tvNoSaleList.visibility = View.VISIBLE
                            }
                        } else {
                            rvSaleList.visibility = View.GONE
                            tvNoSaleList.visibility = View.VISIBLE
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
                    mNetworkCallSaleListAPI(currentPage,edtFromDate.text.toString()+","+edtToDate.text.toString())
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
                    mNetworkCallSaleListAPI(currentPage,edtFromDate.text.toString()+","+edtToDate.text.toString())
                }
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    fun filter(text: String) {
        val temp: MutableList<SaleListModel> = ArrayList()
        for (d in salesArrayList) {


            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.invoice_number.contains(text,ignoreCase = true)) {
                temp.add(d)
            }
        }
        //update recyclerview
        saleListAdapter.updateList(temp)
    }


}