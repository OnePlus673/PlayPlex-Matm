package com.playplexmatm.activity.reports

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplexmatm.R
import com.playplexmatm.adapter.RateListAdapter
import com.playplexmatm.model.report.StockSummaryModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.InternetConnection
import com.playplexmatm.util.Util
import kotlinx.android.synthetic.main.activity_payment_reports.*
import kotlinx.android.synthetic.main.activity_rate_list.*
import kotlinx.android.synthetic.main.activity_rate_list.edtSearch
import kotlinx.android.synthetic.main.activity_rate_list.ivBack
import kotlinx.android.synthetic.main.activity_rate_list.pbBottomLoadData
import kotlinx.android.synthetic.main.activity_rate_list.pbLoadData
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class RateListActivity : AppCompatActivity() {

    lateinit var mContext: RateListActivity
    lateinit var apiInterface: ApiInterface
    private var salesSummaryModelArrayList: ArrayList<StockSummaryModel> = arrayListOf()
    lateinit var stockSummaryAdapter: RateListAdapter

    private var isloading: Boolean = false
    private var islastpage: Boolean = false
    private var currentPage = 1
    private var lastPage = 1
    private var offset: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate_list)
        mContext = this
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)


        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallStockSummaryReportAPI(currentPage)
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

    private fun mNetworkCallStockSummaryReportAPI(newcurrentPage: Int) {

        if (newcurrentPage > 1) {
            offset = (newcurrentPage - 1) * 10
            pbBottomLoadData.visibility = View.VISIBLE
        }
        if (newcurrentPage == 1) {
            offset = (newcurrentPage - 1) * 10
            pbLoadData.visibility = View.VISIBLE
            salesSummaryModelArrayList.clear()
        }


        val call = apiInterface.getStockSummary("id","", offset, 10)
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
                                        TypeToken<java.util.ArrayList<StockSummaryModel?>?>() {}.type
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
                                rvStockSummary.visibility = View.VISIBLE
                                tvNoReport.visibility = View.GONE
                                stockSummaryAdapter.notifyDataSetChanged()
                            } else {

                                if (currentPage == 1) {
                                    rvStockSummary.visibility = View.GONE
                                    tvNoReport.visibility = View.VISIBLE
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
                                rvStockSummary.visibility = View.GONE
                                tvNoReport.visibility = View.VISIBLE
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

    private fun setAdapter(salesSummaryModelArrayList: ArrayList<StockSummaryModel>) {

        rvStockSummary.layoutManager = LinearLayoutManager(mContext)
        rvStockSummary.setHasFixedSize(true)
        stockSummaryAdapter = RateListAdapter(mContext, salesSummaryModelArrayList)
        rvStockSummary.adapter = stockSummaryAdapter
        stockSummaryAdapter.notifyDataSetChanged()
    }

    fun filter(text: String) {
        val temp: MutableList<StockSummaryModel> = ArrayList()
        for (d in salesSummaryModelArrayList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.name.contains(text,ignoreCase = true)) {
                temp.add(d)
            }
        }
        //update recyclerview
        stockSummaryAdapter.updateList(temp)
    }
}