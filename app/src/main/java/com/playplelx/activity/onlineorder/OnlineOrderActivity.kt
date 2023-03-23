package com.playplelx.activity.onlineorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplelx.R
import com.playplelx.adapter.OrderDetailsAdapter
import com.playplelx.model.onlineorder.OrderResponse
import com.playplelx.network.ApiInterface
import com.playplelx.network.Apiclient
import com.playplelx.util.InternetConnection
import com.playplelx.util.PaginationScrollListener
import com.playplelx.util.Util
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class OnlineOrderActivity : AppCompatActivity(), View.OnClickListener,OrderDetailsAdapter.onItemclick {

    lateinit var mContext: OnlineOrderActivity
    lateinit var ivBack: ImageView
    lateinit var apiInterface: ApiInterface
    lateinit var pbLoadData: ProgressBar
    lateinit var pbBottomLoadData: ProgressBar
    lateinit var rvOrderList: RecyclerView
    lateinit var tvNoOrder: TextView
    private var orderList: ArrayList<OrderResponse> = arrayListOf()
    lateinit var orderDetailsAdapter: OrderDetailsAdapter
    private var isloading: Boolean = false
    private var islastpage: Boolean = false
    private var currentPage = 1
    private var lastPage = 1
    private var offset: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_order)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        supportActionBar!!.hide()
        ivBack = findViewById(R.id.ivBack)
        pbLoadData = findViewById(R.id.pbLoadData)
        pbBottomLoadData = findViewById(R.id.pbBottomLoadData)
        rvOrderList = findViewById(R.id.rvOrderList)
        tvNoOrder = findViewById(R.id.tvNoOrder)
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)

        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallOnlineOrderAPI(currentPage)
        } else {
            Toast.makeText(
                mContext, mContext.resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        setAdapter(orderList)

        setPaginationData()
    }

    private fun addListner() {
        ivBack.setOnClickListener(this)
    }


    private fun setPaginationData() {
        rvOrderList.addOnScrollListener(object :
            PaginationScrollListener(rvOrderList.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                if (currentPage < lastPage) {
                    currentPage += 1
                    isloading = true
                    mNetworkCallOnlineOrderAPI(currentPage)
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


    private fun mNetworkCallOnlineOrderAPI(newcurrentPage: Int) {

        if (newcurrentPage > 1) {
            offset = (newcurrentPage - 1) * 10
            pbBottomLoadData.visibility = View.VISIBLE
        }
        if (newcurrentPage == 1) {
            offset = (newcurrentPage - 1) * 10
            pbLoadData.visibility = View.VISIBLE
            orderList.clear()
        }


        val call = apiInterface.getOnlineOrders("id", "", offset, 10)
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
                            orderList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<OrderResponse?>?>() {}.type
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


                            if (orderList.size > 0) {
                                rvOrderList.visibility = View.VISIBLE
                                tvNoOrder.visibility = View.GONE
                                orderDetailsAdapter.notifyDataSetChanged()
                            } else {

                                if (currentPage == 1) {
                                    rvOrderList.visibility = View.GONE
                                    tvNoOrder.visibility = View.VISIBLE
                                }


                            }

                        } else {
                            if (currentPage == 1) {
                                rvOrderList.visibility = View.GONE
                                tvNoOrder.visibility = View.VISIBLE
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

    private fun setAdapter(orderList: ArrayList<OrderResponse>) {

        rvOrderList.layoutManager = LinearLayoutManager(mContext)
        rvOrderList.setHasFixedSize(true)
        orderDetailsAdapter = OrderDetailsAdapter(mContext, orderList, this)
        rvOrderList.adapter = orderDetailsAdapter
        orderDetailsAdapter.notifyDataSetChanged()
    }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    override fun onClick(orderResponse: OrderResponse, position: Int) {

    }
}