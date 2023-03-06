package com.playplelx.activity.settings

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
import com.playplelx.adapter.TaxesAdapter
import com.playplelx.adapter.WareHouseAdapter
import com.playplelx.model.taxes.TaxesModel
import com.playplelx.model.warehouse.WareHouseModel
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

class WareHouseSettingsActivity : AppCompatActivity(), View.OnClickListener,
    WareHouseAdapter.onClick {

    lateinit var mContext: WareHouseSettingsActivity
    lateinit var ivBack: ImageView
    lateinit var tvNewWareHouse: TextView
    lateinit var tvNoWareHouse: TextView
    lateinit var rvWareHouse: RecyclerView
    private var wareHouseArrayList: ArrayList<WareHouseModel> = arrayListOf()
    lateinit var wareHouseAdapter: WareHouseAdapter
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
        setContentView(R.layout.activity_ware_house_settings)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        supportActionBar!!.hide()
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        ivBack = findViewById(R.id.ivBack)
        rvWareHouse = findViewById(R.id.rvWareHouse)
        pbLoadData = findViewById(R.id.pbLoadData)
        tvNewWareHouse = findViewById(R.id.tvNewWareHouse)
        tvNoWareHouse = findViewById(R.id.tvNoWareHouse)
        pbBottomLoadData = findViewById(R.id.pbBottomLoadData)


        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallWareHouseAPI(currentPage)
        } else {
            Toast.makeText(
                mContext, mContext.resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        setAdapter(wareHouseArrayList)


        setPaginationData()
    }

    private fun addListner() {
        ivBack.setOnClickListener(this)
        tvNewWareHouse.setOnClickListener(this)
    }

    private fun setPaginationData() {
        rvWareHouse.addOnScrollListener(object :
            PaginationScrollListener(rvWareHouse.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                if (currentPage < lastPage) {
                    currentPage += 1
                    isloading = true
                    mNetworkCallWareHouseAPI(currentPage)
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


    private fun mNetworkCallWareHouseAPI(newcurrentPage: Int) {
        if (newcurrentPage > 1) {
            offset = (newcurrentPage - 1) * 10
            pbBottomLoadData.visibility = View.VISIBLE
        }
        if (newcurrentPage == 1) {
            offset = (newcurrentPage - 1) * 10
            pbLoadData.visibility = View.VISIBLE
            wareHouseArrayList.clear()
        }
        val call = apiInterface.getWareHouse("id", "", offset, 10)
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
                            wareHouseArrayList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object :
                                        TypeToken<java.util.ArrayList<WareHouseModel?>?>() {}.type
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


                            if (wareHouseArrayList.size > 0) {
                                rvWareHouse.visibility = View.VISIBLE
                                tvNoWareHouse.visibility = View.GONE
                                setAdapter(wareHouseArrayList)

                            } else {
                                if (currentPage == 1) {
                                    rvWareHouse.visibility = View.GONE
                                    tvNoWareHouse.visibility = View.VISIBLE
                                }

                            }

                        } else {
                            if (currentPage == 1) {
                                rvWareHouse.visibility = View.GONE
                                tvNoWareHouse.visibility = View.VISIBLE
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
                pbBottomLoadData.visibility = View.GONE
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setAdapter(wareHouseArrayList: ArrayList<WareHouseModel>) {
        rvWareHouse.layoutManager = LinearLayoutManager(mContext)
        rvWareHouse.setHasFixedSize(true)
        wareHouseAdapter = WareHouseAdapter(mContext, wareHouseArrayList, this)
        rvWareHouse.adapter = wareHouseAdapter
        wareHouseAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.tvNewWareHouse -> {
                val intent = Intent(mContext, AddEditWareHouseSettingActivity::class.java)
                intent.putExtra(Constants.mFrom, Constants.isAdd)
                startActivityForResult(intent, 201)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 201) {
            if (resultCode == Activity.RESULT_OK) {
                mNetworkCallWareHouseAPI(currentPage)
            }
        }
    }


    override fun onItemClick(wareHouseModel: WareHouseModel, position: Int) {
        startActivityForResult(
            Intent(mContext, AddEditWareHouseSettingActivity::class.java)
                .putExtra(Constants.mFrom, Constants.isEdit)
                .putExtra(Constants.wareHouseModel, Gson().toJson(wareHouseModel)), 201
        )

    }

    override fun onDeleteClick(wareHouseModel: WareHouseModel, position: Int) {
        openWareHouseDialog(wareHouseModel, position)
    }


    private fun openWareHouseDialog(wareHouseModel: WareHouseModel, position: Int) {
        val builder = AlertDialog.Builder(mContext)
        builder.setMessage("Are you sure you want to Delete WareHouse?")
        builder.setPositiveButton("YES") { dialog, which ->
            if (InternetConnection.checkConnection(mContext)) {
                deleteWareHouse(dialog, wareHouseModel, position)
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

    private fun deleteWareHouse(
        dialog: DialogInterface,
        wareHouseModel: WareHouseModel,
        position: Int
    ) {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.deleteWareHouse(wareHouseModel.xid)
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
                            if (wareHouseArrayList.size > 0) {
                                wareHouseArrayList.removeAt(position)
                                currentPage = 1
                                mNetworkCallWareHouseAPI(currentPage)
                            } else {
                                rvWareHouse.visibility = View.GONE
                                tvNoWareHouse.visibility = View.VISIBLE
                            }
                        } else {
                            rvWareHouse.visibility = View.GONE
                            tvNoWareHouse.visibility = View.VISIBLE
                            Toast.makeText(
                                mContext, jsonobject.optString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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