package com.playplelx.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.playplelx.R
import com.playplelx.activity.pos.saleList.AddEditSaleListActivity
import com.playplelx.adapter.PurchaseAdapter
import com.playplelx.adapter.SaleListAdapter
import com.playplelx.model.purchasemodel.PurchaseModel
import com.playplelx.model.saleList.SaleListModel
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
        supportActionBar!!.hide()
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        pbLoadData = findViewById(R.id.pbLoadData)
        ivBack = findViewById(R.id.ivBack)
        rvPurchase = findViewById(R.id.rvPurchase)
        tvNewPurchase = findViewById(R.id.tvNewPurchaseList)
        tvNoPurchase = findViewById(R.id.tvNoPurchaseList)
        pbBottomLoadData = findViewById(R.id.pbBottomLoadData)

        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallPurchaseListAPI(currentPage)
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
    }

    private fun setPaginationData() {
        rvPurchase.addOnScrollListener(object :
            PaginationScrollListener(rvPurchase.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                if (currentPage < lastPage) {
                    currentPage += 1
                    isloading = true
                    mNetworkCallPurchaseListAPI(currentPage)
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
                    Intent(mContext, AddEditPurchaseListActivity::class.java),
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
                mNetworkCallPurchaseListAPI(currentPage)
            }
        }
    }

    private fun mNetworkCallPurchaseListAPI(newcurrentPage: Int) {
        if (newcurrentPage > 1) {
            offset = (newcurrentPage - 1) * 10
            pbBottomLoadData.visibility = View.VISIBLE
        }
        if (newcurrentPage == 1) {
            offset = (newcurrentPage - 1) * 10
            pbLoadData.visibility = View.VISIBLE
            purchaseAraryList.clear()
        }
        val call = apiInterface.getPurchase()
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
                                mNetworkCallPurchaseListAPI(currentPage)
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

}