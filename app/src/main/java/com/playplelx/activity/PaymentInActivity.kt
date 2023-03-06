package com.playplelx.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.playplelx.R
import com.playplelx.adapter.PaymentInAdapter
import com.playplelx.model.paymentin.PaymentInModel
import com.playplelx.model.units.UnitModel
import com.playplelx.network.ApiInterface
import com.playplelx.network.Apiclient
import com.playplelx.util.Constants
import com.playplelx.util.InternetConnection
import com.playplelx.util.PaginationScrollListener
import com.playplelx.util.Util
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class PaymentInActivity : AppCompatActivity(), View.OnClickListener, PaymentInAdapter.onClick {

    lateinit var mContext: PaymentInActivity
    lateinit var ivBack: ImageView
    lateinit var apiInterface: ApiInterface
    lateinit var rvPaymentIn: RecyclerView
    lateinit var tvNoPayment: TextView
    lateinit var tvNewPaymentIn: TextView
    lateinit var pbLoadData: ProgressBar
    private var paymentList: ArrayList<PaymentInModel> = arrayListOf()
    lateinit var paymentInAdapter: PaymentInAdapter

    lateinit var pbBottomLoadData: ProgressBar
    private var isloading: Boolean = false
    private var islastpage: Boolean = false
    private var currentPage = 1
    private var lastPage = 1
    private var offset: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_in)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        supportActionBar!!.hide()
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        pbLoadData = findViewById(R.id.pbLoadData)
        ivBack = findViewById(R.id.ivBack)
        rvPaymentIn = findViewById(R.id.rvPaymentIn)
        tvNoPayment = findViewById(R.id.tvNoPayment)
        tvNewPaymentIn = findViewById(R.id.tvNewPaymentIn)
        pbBottomLoadData = findViewById(R.id.pbBottomLoadData)


        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallPaymentINAPI(currentPage)
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
        tvNewPaymentIn.setOnClickListener(this)


    }


    private fun setPaginationData() {
        rvPaymentIn.addOnScrollListener(object :
            PaginationScrollListener(rvPaymentIn.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                if (currentPage < lastPage) {
                    currentPage += 1
                    isloading = true
                    mNetworkCallPaymentINAPI(currentPage)
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
            R.id.tvNewPaymentIn -> {
                startActivityForResult(
                    Intent(mContext, AddEditPaymentInActivity::class.java)
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
                mNetworkCallPaymentINAPI(currentPage)
            }
        }
    }

    private fun mNetworkCallPaymentINAPI(newcurrentPage: Int) {
        if (newcurrentPage > 1) {
            offset = (newcurrentPage - 1) * 10
            pbBottomLoadData.visibility = View.VISIBLE
        }
        if (newcurrentPage == 1) {
            offset = (newcurrentPage - 1) * 10
            pbLoadData.visibility = View.VISIBLE
            paymentList.clear()
        }

        val call = apiInterface.getPaymentIn("id","",offset,10)
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
                                        TypeToken<java.util.ArrayList<PaymentInModel?>?>() {}.type
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
                                rvPaymentIn.visibility = View.VISIBLE
                                tvNoPayment.visibility = View.GONE
                                setAdapter(paymentList)

                            } else {
                                if (currentPage == 1) {
                                    rvPaymentIn.visibility = View.GONE
                                    tvNoPayment.visibility = View.VISIBLE
                                }

                            }

                        } else {
                            if (currentPage == 1) {
                                rvPaymentIn.visibility = View.GONE
                                tvNoPayment.visibility = View.VISIBLE
                            }
                            Toast.makeText(
                                mContext, jsonobject.optString("message"),
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

    private fun setAdapter(paymentList: ArrayList<PaymentInModel>) {
        rvPaymentIn.layoutManager = LinearLayoutManager(mContext)
        rvPaymentIn.setHasFixedSize(true)
        paymentInAdapter = PaymentInAdapter(mContext, paymentList, this)
        rvPaymentIn.adapter = paymentInAdapter
        paymentInAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(paymentInModel: PaymentInModel, position: Int) {
        startActivityForResult(
            Intent(mContext, AddEditPaymentInActivity::class.java)
                .putExtra(Constants.mFrom, Constants.isEdit)
                .putExtra(Constants.paymentInModel, Gson().toJson(paymentInModel)), 201
        )
    }

    override fun onDeleteClick(paymentInModel: PaymentInModel, position: Int) {
        openPaymentInDialog(paymentInModel, position)
    }


    private fun openPaymentInDialog(paymentInModel: PaymentInModel, position: Int) {
        val builder = AlertDialog.Builder(mContext)
        builder.setMessage("Are you sure you want to delete PaymentIN?")
        builder.setPositiveButton("YES") { dialog, which ->
            if (InternetConnection.checkConnection(mContext)) {
                deletePaymentIn(dialog, paymentInModel, position)
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

    private fun deletePaymentIn(
        dialog: DialogInterface,
        paymentInModel: PaymentInModel,
        position: Int
    ) {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.deletePaymentIn(paymentInModel.xid)
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
                                mNetworkCallPaymentINAPI(currentPage)
                            } else {
                                rvPaymentIn.visibility = View.GONE
                                tvNoPayment.visibility = View.VISIBLE
                            }
                        } else {
                            rvPaymentIn.visibility = View.GONE
                            tvNoPayment.visibility = View.VISIBLE
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
}