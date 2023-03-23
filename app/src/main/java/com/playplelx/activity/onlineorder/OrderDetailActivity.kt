package com.playplelx.activity.onlineorder

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.util.IntentUtils
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.playplelx.R
import com.playplelx.adapter.OrderItemAdapter
import com.playplelx.model.onlineorder.OrderResponse
import com.playplelx.network.ApiInterface
import com.playplelx.network.Apiclient
import com.playplelx.util.Constants
import com.playplelx.util.InternetConnection
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderDetailActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var mContext: OrderDetailActivity
    lateinit var ivBack: ImageView
    lateinit var orderModel: OrderResponse
    lateinit var tvInvoiceNumber: TextView
    lateinit var tvOrderStatus: TextView
    lateinit var tvGrandTotal: TextView
    lateinit var tvCustomerName: TextView
    lateinit var tvPhoneNumber: TextView
    lateinit var tvAddress: TextView
    lateinit var rvOrders: RecyclerView
    lateinit var pbLoadData: ProgressBar
    lateinit var tvConfirm: TextView
    lateinit var tvCancel: TextView
    lateinit var orderItemAdapter: OrderItemAdapter
    lateinit var apiInterface: ApiInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        supportActionBar!!.hide()
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        ivBack = findViewById(R.id.ivBack)
        tvInvoiceNumber = findViewById(R.id.tvInvoiceNumber)
        tvOrderStatus = findViewById(R.id.tvOrderStatus)
        tvGrandTotal = findViewById(R.id.tvGrandTotal)
        rvOrders = findViewById(R.id.rvOrders)
        tvCustomerName = findViewById(R.id.tvCustomerName)
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber)
        tvAddress = findViewById(R.id.tvAddress)
        tvConfirm = findViewById(R.id.tvConfirm)
        tvCancel = findViewById(R.id.tvCancel)
        pbLoadData = findViewById(R.id.pbLoadData)

        if (intent.extras != null) {
            orderModel = Gson().fromJson(
                intent.getStringExtra(Constants.orderModel),
                OrderResponse::class.java
            )
            setData(orderModel)

        }
    }

    private fun addListner() {
        ivBack.setOnClickListener(this)
        tvCancel.setOnClickListener(this)
        tvConfirm.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    private fun setData(orderModel: OrderResponse) {
        tvInvoiceNumber.text = "Invoice Number:- " + orderModel.invoice_number
        tvGrandTotal.text = "Grand Total:- " + orderModel.subtotal.toString()
        tvCustomerName.text = "Customer Name:-  " + orderModel.shipping_address.name
        tvPhoneNumber.text = "Phone Number:-  " + orderModel.shipping_address.phone
        tvAddress.text =
            "Address-  " + orderModel.shipping_address.address + ", " + orderModel.shipping_address.shipping_address + ", " + orderModel.shipping_address.city + ", " + orderModel.shipping_address.state + ", " + orderModel.shipping_address.country + "- " + orderModel.shipping_address.zipcode

        if (orderModel.cancelled == 1) {
            tvConfirm.visibility = View.GONE
            tvCancel.visibility = View.GONE
            tvOrderStatus.text = "Canceled"
            tvOrderStatus.setTextColor(ContextCompat.getColor(mContext, R.color.red))
        } else if (orderModel.order_status.equals("ordered") && orderModel.cancelled == 0) {
            tvCancel.visibility = View.VISIBLE
            tvConfirm.visibility = View.VISIBLE
            tvConfirm.text = "Confirm"
            tvOrderStatus.text = "Pending"
            tvOrderStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
        } else {
            if (orderModel.cancelled == 0) {
              if (orderModel.order_status.equals("confirmed")) {
                    tvConfirm.visibility = View.VISIBLE
                    tvCancel.visibility = View.VISIBLE
                    tvConfirm.text = "Delivered"
                    tvOrderStatus.text = "Confirmed"
                    tvOrderStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorgreen))
                } else if (orderModel.order_status.equals("delivered")) {
                    tvConfirm.visibility = View.GONE
                    tvCancel.visibility = View.GONE
                    tvOrderStatus.text = "Delivered"
                    tvOrderStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorgreen))
                }
            }
        }


        rvOrders.layoutManager = LinearLayoutManager(mContext)
        rvOrders.setHasFixedSize(true)
        orderItemAdapter = OrderItemAdapter(mContext, orderModel.items)
        rvOrders.adapter = orderItemAdapter
        orderItemAdapter.notifyDataSetChanged()

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.tvConfirm -> {
                if (InternetConnection.checkConnection(mContext)) {
                    if (orderModel.order_status.equals("ordered")){
                        mNetworkCallConfirmOrderAPI(orderModel.unique_id)
                    }else if (orderModel.order_status.equals("confirmed")){
                        mNetworkCallDeliverdOrderAPI(orderModel.unique_id)
                    }
                } else {
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.str_check_internet_connections),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            R.id.tvCancel -> {
                if (InternetConnection.checkConnection(mContext)) {
                    mNetworkCallCanceledOrderAPI(orderModel.unique_id)
                } else {
                    Toast.makeText(
                        mContext,
                        mContext.resources.getString(R.string.str_check_internet_connections),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun mNetworkCallConfirmOrderAPI(id: String) {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.confirmOrders(id)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        pbLoadData.visibility = View.GONE
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            Toast.makeText(
                                mContext, "Confirm order successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent()
                            setResult(Activity.RESULT_OK, intent)
                            finish()

                        } else {
                            Toast.makeText(
                                mContext, jsonObject.optString("message"),
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
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun mNetworkCallDeliverdOrderAPI(id: String) {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.deliveredOrders(id)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        pbLoadData.visibility = View.GONE
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            Toast.makeText(
                                mContext, "Delivered order successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent()
                            setResult(Activity.RESULT_OK, intent)
                            finish()

                        } else {
                            Toast.makeText(
                                mContext, jsonObject.optString("message"),
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
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun mNetworkCallCanceledOrderAPI(id: String) {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.canceledOrders(id)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        pbLoadData.visibility = View.GONE
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            Toast.makeText(
                                mContext, "Canceled order successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent()
                            setResult(Activity.RESULT_OK, intent)
                            finish()

                        } else {
                            Toast.makeText(
                                mContext, jsonObject.optString("message"),
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
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pbLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

}