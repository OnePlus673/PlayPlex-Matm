package com.playplexmatm.activity.settings

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
import com.playplexmatm.R
import com.playplexmatm.adapter.PaymentModeAdapter
import com.playplexmatm.model.paymentmode.PaymentModeModel
import com.playplexmatm.network.ApiInterface
import com.playplexmatm.network.Apiclient
import com.playplexmatm.util.Constants
import com.playplexmatm.util.InternetConnection
import com.playplexmatm.util.PaginationScrollListener
import com.playplexmatm.util.Util
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class PaymentModeSettingsActivity : AppCompatActivity(), View.OnClickListener,
    PaymentModeAdapter.onClick {

    lateinit var mContext: PaymentModeSettingsActivity
    lateinit var ivBack: ImageView
    lateinit var tvNewPaymentMode: TextView
    lateinit var tvNoPaymentMode: TextView
    lateinit var rvPaymentMode: RecyclerView
    private var paymentModeList: ArrayList<PaymentModeModel> = arrayListOf()
    lateinit var paymentModeAdapter: PaymentModeAdapter
    lateinit var pbLoadData: ProgressBar
    lateinit var apiInterface: ApiInterface

    lateinit var pbBottomLoadData: ProgressBar
    private var isloading: Boolean = false
    private var islastpage: Boolean = false
    private var currentPage = 1
    private var lastPage = 1
    private var offset: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_mode_settings)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        pbLoadData = findViewById(R.id.pbLoadData)
        ivBack = findViewById(R.id.ivBack)
        tvNewPaymentMode = findViewById(R.id.tvNewPaymentMode)
        tvNoPaymentMode = findViewById(R.id.tvNoPaymentMode)
        rvPaymentMode = findViewById(R.id.rvPaymentMode)
        pbBottomLoadData = findViewById(R.id.pbBottomLoadData)

        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallPaymentModeAPI(currentPage)
        } else {
            Toast.makeText(
                mContext, mContext.resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        setAdapter(paymentModeList)

        setPaginationData()

    }

    private fun setPaginationData() {
        rvPaymentMode.addOnScrollListener(object :
            PaginationScrollListener(rvPaymentMode.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                if (currentPage < lastPage) {
                    currentPage += 1
                    isloading = true
                    mNetworkCallPaymentModeAPI(currentPage)
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
        tvNewPaymentMode.setOnClickListener(this)
    }


    private fun mNetworkCallPaymentModeAPI(newcurrentPage: Int) {
        if (newcurrentPage > 1) {
            offset = (newcurrentPage - 1) * 10
            pbBottomLoadData.visibility = View.VISIBLE
        }
        if (newcurrentPage == 1) {
            offset = (newcurrentPage - 1) * 10
            pbLoadData.visibility = View.VISIBLE
            paymentModeList.clear()
        }
        val call = apiInterface.getPaymentModes("id","",offset,10)
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
                            paymentModeList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<PaymentModeModel?>?>() {}.type
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

                            if (paymentModeList.size > 0) {
                                rvPaymentMode.visibility = View.VISIBLE
                                tvNoPaymentMode.visibility = View.GONE
                                setAdapter(paymentModeList)

                            } else {
                                if (currentPage == 1) {
                                    rvPaymentMode.visibility = View.GONE
                                    tvNoPaymentMode.visibility = View.VISIBLE
                                }

                            }

                        } else {
                            if (currentPage == 1) {
                                rvPaymentMode.visibility = View.GONE
                                tvNoPaymentMode.visibility = View.VISIBLE
                                Toast.makeText(
                                    mContext, jsonObject.optString("message"),
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

    private fun setAdapter(paymentModeList: ArrayList<PaymentModeModel>) {
        rvPaymentMode.layoutManager = LinearLayoutManager(mContext)
        rvPaymentMode.setHasFixedSize(true)
        paymentModeAdapter = PaymentModeAdapter(mContext, paymentModeList, this)
        rvPaymentMode.adapter = paymentModeAdapter
        paymentModeAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.tvNewPaymentMode -> {
                val intent = Intent(mContext, AddEditPaymentModeActivity::class.java)
                intent.putExtra(Constants.mFrom, Constants.isAdd)
                startActivityForResult(intent, 201)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 201) {
            if (resultCode == Activity.RESULT_OK) {
                currentPage = 1
                mNetworkCallPaymentModeAPI(currentPage)
            }
        }
    }


    override fun onItemClick(paymentModeModel: PaymentModeModel, position: Int) {
        startActivityForResult(
            Intent(mContext, AddEditPaymentModeActivity::class.java)
                .putExtra(Constants.mFrom, Constants.isEdit)
                .putExtra(Constants.paymentModel, Gson().toJson(paymentModeModel)), 201
        )

    }

    override fun onDeleteClick(paymentModeModel: PaymentModeModel, position: Int) {
        openPaymentModeDialog(paymentModeModel, position)
    }


    private fun openPaymentModeDialog(paymentModeModel: PaymentModeModel, position: Int) {
        val builder = AlertDialog.Builder(mContext)
        builder.setMessage("Are you sure you want to Delete Payment Mode?")
        builder.setPositiveButton("YES") { dialog, which ->
            if (InternetConnection.checkConnection(mContext)) {
                deletePaymentMode(dialog, paymentModeModel, position)
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

    private fun deletePaymentMode(
        dialog: DialogInterface,
        paymentModeModel: PaymentModeModel,
        position: Int
    ) {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.deletepaymentModes(paymentModeModel.xid)
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
                            if (paymentModeList.size > 0) {
                                paymentModeList.removeAt(position)
                                currentPage = 1
                                mNetworkCallPaymentModeAPI(currentPage)
                            } else {
                                rvPaymentMode.visibility = View.GONE
                                tvNoPaymentMode.visibility = View.VISIBLE
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
}