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
import com.playplexmatm.adapter.report.ProductReportsAdapter
import com.playplexmatm.model.report.ProductsReportModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.InternetConnection
import com.playplexmatm.util.Util
import kotlinx.android.synthetic.main.activity_payment_reports.*
import kotlinx.android.synthetic.main.activity_product_report.*
import kotlinx.android.synthetic.main.activity_product_report.edtSearch
import kotlinx.android.synthetic.main.activity_product_report.ivBack
import kotlinx.android.synthetic.main.activity_product_report.pbBottomLoadData
import kotlinx.android.synthetic.main.activity_product_report.pbLoadData
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class ProductReportActivity : AppCompatActivity() {

    lateinit var mContext: ProductReportActivity
    lateinit var apiInterface: ApiInterface
    private var productReportModelArrayList: ArrayList<ProductsReportModel> = arrayListOf()
    lateinit var productReportsAdapter: ProductReportsAdapter

    private var isloading: Boolean = false
    private var islastpage: Boolean = false
    private var currentPage = 1
    private var lastPage = 1
    private var offset: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_report)
        mContext = this
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)


        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallProductReportAPI(currentPage)
        } else {
            Toast.makeText(
                mContext, mContext.resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        setAdapter(productReportModelArrayList)

        ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun mNetworkCallProductReportAPI(newcurrentPage: Int) {

        if (newcurrentPage > 1) {
            offset = (newcurrentPage - 1) * 10
            pbBottomLoadData.visibility = View.VISIBLE
        }
        if (newcurrentPage == 1) {
            offset = (newcurrentPage - 1) * 10
            pbLoadData.visibility = View.VISIBLE
            productReportModelArrayList.clear()
        }


        val call = apiInterface.getStockAlert("id","", offset, 10)
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
                            productReportModelArrayList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<ProductsReportModel?>?>() {}.type
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


                            if (productReportModelArrayList.size > 0) {
                                rvStockAlert.visibility = View.VISIBLE
                                tvNoReport.visibility = View.GONE
                                productReportsAdapter.notifyDataSetChanged()
                            } else {

                                if (currentPage == 1) {
                                    rvStockAlert.visibility = View.GONE
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
                                rvStockAlert.visibility = View.GONE
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

    private fun setAdapter(productReportModelArrayList: ArrayList<ProductsReportModel>) {
        rvStockAlert.layoutManager = LinearLayoutManager(mContext)
        rvStockAlert.setHasFixedSize(true)
        productReportsAdapter = ProductReportsAdapter(mContext, productReportModelArrayList)
        rvStockAlert.adapter = productReportsAdapter
        productReportsAdapter.notifyDataSetChanged()
    }


    fun filter(text: String) {
        val temp: MutableList<ProductsReportModel> = ArrayList()
        for (d in productReportModelArrayList) {


            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.name.contains(text,ignoreCase = true)) {
                temp.add(d)
            }
        }
        //update recyclerview
        productReportsAdapter.updateList(temp)
    }

}