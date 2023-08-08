package com.playplexmatm.activity.reports

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplexmatm.R
import com.playplexmatm.adapter.report.SalesSummaryAdapter
import com.playplexmatm.model.report.SalesSummaryModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.InternetConnection
import com.playplexmatm.util.Util
import kotlinx.android.synthetic.main.activity_sales_summary.*
import kotlinx.android.synthetic.main.activity_sales_summary.edtFromDate
import kotlinx.android.synthetic.main.activity_sales_summary.edtToDate
import kotlinx.android.synthetic.main.activity_sales_summary.ivBack
import kotlinx.android.synthetic.main.activity_sales_summary.pbBottomLoadData
import kotlinx.android.synthetic.main.activity_sales_summary.pbLoadData
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class SalesSummaryActivity : AppCompatActivity() {

    lateinit var mContext: SalesSummaryActivity
    lateinit var apiInterface: ApiInterface
    private var salesSummaryModelArrayList: ArrayList<SalesSummaryModel> = arrayListOf()
    lateinit var salesSummaryAdapter: SalesSummaryAdapter

    private var isloading: Boolean = false
    private var islastpage: Boolean = false
    private var currentPage = 1
    private var lastPage = 1
    private var offset: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_summary)
        mContext = this
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)


        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallSalesSummaryReportAPI(currentPage,"")
        } else {
            Toast.makeText(
                mContext, mContext.resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        setAdapter(salesSummaryModelArrayList)

        ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun mNetworkCallSalesSummaryReportAPI(newcurrentPage: Int, date: String) {

        if (newcurrentPage > 1) {
            offset = (newcurrentPage - 1) * 10
            pbBottomLoadData.visibility = View.VISIBLE
        }
        if (newcurrentPage == 1) {
            offset = (newcurrentPage - 1) * 10
            pbLoadData.visibility = View.VISIBLE
            salesSummaryModelArrayList.clear()
        }


        val call = apiInterface.getSalesSummary("id","", offset, 10,date)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    pbBottomLoadData.visibility = View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            Log.e("response",jsonObject.toString())

                            isloading = false
                            val data = jsonObject.optJSONArray("data")
                            salesSummaryModelArrayList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<SalesSummaryModel?>?>() {}.type
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


                            if (salesSummaryModelArrayList.size > 0) {
                                rvSalesSummary.visibility = View.VISIBLE
                                tvNoReport.visibility = View.GONE
                                salesSummaryAdapter.notifyDataSetChanged()
                            } else {

                                if (currentPage == 1) {
                                    rvSalesSummary.visibility = View.GONE
                                    tvNoReport.visibility = View.VISIBLE
                                }
                            }
                        } else {
                            if (currentPage == 1) {
                                rvSalesSummary.visibility = View.GONE
                                tvNoReport.visibility = View.VISIBLE
                                Toast.makeText(
                                    mContext,
                                    jsonObject.optString("message"),
                                    Toast.LENGTH_SHORT
                                ).show()
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
                        val JsonObject= JSONObject(response.errorBody()!!.string())
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

    private fun setAdapter(salesSummaryModelArrayList: ArrayList<SalesSummaryModel>) {

        rvSalesSummary.layoutManager = LinearLayoutManager(mContext)
        rvSalesSummary.setHasFixedSize(true)
        salesSummaryAdapter = SalesSummaryAdapter(mContext, salesSummaryModelArrayList)
        rvSalesSummary.adapter = salesSummaryAdapter
        salesSummaryAdapter.notifyDataSetChanged()
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
                    mNetworkCallSalesSummaryReportAPI(currentPage,edtFromDate.text.toString()+","+edtToDate.text.toString())
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
                    mNetworkCallSalesSummaryReportAPI(currentPage,edtFromDate.text.toString()+","+edtToDate.text.toString())
                }
            },
                year,
                month,
                day
            )
        datePickerDialog.show()
    }

    fun filter(text: String) {
        val temp: MutableList<SalesSummaryModel> = ArrayList()
        for (d in salesSummaryModelArrayList) {


            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.staff_member.name.contains(text,ignoreCase = true)) {
                temp.add(d)
            }
        }
        //update recyclerview
        salesSummaryAdapter.updateList(temp)
    }

}