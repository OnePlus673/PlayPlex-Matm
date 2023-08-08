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
import com.playplexmatm.adapter.TaxesAdapter
import com.playplexmatm.model.taxes.TaxesModel
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

class TaxesSettingsActivity : AppCompatActivity(), View.OnClickListener, TaxesAdapter.onClick {

    lateinit var mContext: TaxesSettingsActivity
    lateinit var ivBack: ImageView
    lateinit var tvNewTax: TextView
    lateinit var tvNoTax: TextView
    lateinit var rvTax: RecyclerView
    private var taxArrayList: ArrayList<TaxesModel> = arrayListOf()
    lateinit var taxesAdapter: TaxesAdapter
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
        setContentView(R.layout.activity_taxes_settings)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        ivBack = findViewById(R.id.ivBack)
        rvTax = findViewById(R.id.rvTax)
        pbLoadData = findViewById(R.id.pbLoadData)
        tvNewTax = findViewById(R.id.tvNewTax)
        tvNoTax = findViewById(R.id.tvNoTax)
        pbBottomLoadData = findViewById(R.id.pbBottomLoadData)

        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallTaxAPI(currentPage)
        } else {
            Toast.makeText(
                mContext, mContext.resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        setAdapter(taxArrayList)

        setPaginationData()
    }

    private fun addListner() {
        ivBack.setOnClickListener(this)
        tvNewTax.setOnClickListener(this)
    }

    private fun setPaginationData() {
        rvTax.addOnScrollListener(object :
            PaginationScrollListener(rvTax.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                if (currentPage < lastPage) {
                    currentPage += 1
                    isloading = true
                    mNetworkCallTaxAPI(currentPage)
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
            R.id.tvNewTax -> {
                val intent = Intent(mContext, AddEditTaxActivity::class.java)
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
                mNetworkCallTaxAPI(currentPage)
            }
        }
    }

    private fun mNetworkCallTaxAPI(newcurrentPage: Int) {
        if (newcurrentPage > 1) {
            offset = (newcurrentPage - 1) * 10
            pbBottomLoadData.visibility = View.VISIBLE
        }
        if (newcurrentPage == 1) {
            offset = (newcurrentPage - 1) * 10
            pbLoadData.visibility = View.VISIBLE
            taxArrayList.clear()
        }
        val call = apiInterface.getTaxes("id","",offset,10)
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
                            taxArrayList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object : TypeToken<java.util.ArrayList<TaxesModel?>?>() {}.type
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



                            if (taxArrayList.size > 0) {
                                rvTax.visibility = View.VISIBLE
                                tvNoTax.visibility = View.GONE
                                setAdapter(taxArrayList)

                            } else {
                                if (currentPage == 1) {
                                    rvTax.visibility = View.GONE
                                    tvNoTax.visibility = View.VISIBLE
                                }

                            }

                        } else {
                            if (currentPage == 1) {
                                rvTax.visibility = View.GONE
                                tvNoTax.visibility = View.VISIBLE
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

    private fun setAdapter(taxArrayList: ArrayList<TaxesModel>) {
        rvTax.layoutManager = LinearLayoutManager(mContext)
        rvTax.setHasFixedSize(true)
        taxesAdapter = TaxesAdapter(mContext, taxArrayList, this)
        rvTax.adapter = taxesAdapter
        taxesAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(taxesModel: TaxesModel, position: Int) {
        startActivityForResult(
            Intent(mContext, AddEditTaxActivity::class.java)
                .putExtra(Constants.mFrom, Constants.isEdit)
                .putExtra(Constants.taxesModel, Gson().toJson(taxesModel)), 201
        )
    }

    override fun onDeleteClick(taxesModel: TaxesModel, position: Int) {
        openTaxDialog(taxesModel, position)
    }


    private fun openTaxDialog(taxesModel: TaxesModel, position: Int) {
        val builder = AlertDialog.Builder(mContext)
        builder.setMessage("Are you sure you want to Delete Tax?")
        builder.setPositiveButton("YES") { dialog, which ->
            if (InternetConnection.checkConnection(mContext)) {
                deleteTax(dialog, taxesModel, position)
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

    private fun deleteTax(
        dialog: DialogInterface,
        taxesModel: TaxesModel,
        position: Int
    ) {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.deleteTaxes(taxesModel.xid)
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
                            if (taxArrayList.size > 0) {
                                taxArrayList.removeAt(position)
                                currentPage = 1
                                mNetworkCallTaxAPI(currentPage)
                            } else {
                                rvTax.visibility = View.GONE
                                tvNoTax.visibility = View.VISIBLE
                            }
                        } else {
                            rvTax.visibility = View.GONE
                            tvNoTax.visibility = View.VISIBLE
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