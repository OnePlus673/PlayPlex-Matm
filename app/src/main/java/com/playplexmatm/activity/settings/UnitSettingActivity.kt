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
import com.playplexmatm.adapter.UnitAdapter
import com.playplexmatm.model.units.UnitModel
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

class UnitSettingActivity : AppCompatActivity(), View.OnClickListener, UnitAdapter.onClick {

    lateinit var mContext: UnitSettingActivity

    lateinit var ivBack: ImageView
    lateinit var tvNewUnit: TextView
    lateinit var tvNoUnit: TextView
    lateinit var rvUnit: RecyclerView
    private var unitArrayList: ArrayList<UnitModel> = arrayListOf()
    lateinit var unitAdapter: UnitAdapter
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
        setContentView(R.layout.activity_unit_setting)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        //supportActionBar!!.hide()
        apiInterface = Apiclient(mContext).getClient()!!.create(ApiInterface::class.java)
        ivBack = findViewById(R.id.ivBack)
        rvUnit = findViewById(R.id.rvUnits)
        pbLoadData = findViewById(R.id.pbLoadData)
        tvNewUnit = findViewById(R.id.tvNewUnit)
        tvNoUnit = findViewById(R.id.tvNoUnit)
        pbBottomLoadData = findViewById(R.id.pbBottomLoadData)

        if (InternetConnection.checkConnection(mContext)) {
            mNetworkCallUnitAPI(currentPage)
        } else {
            Toast.makeText(
                mContext, mContext.resources.getString(R.string.str_check_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

        setAdapter(unitArrayList)

        setPaginationData()

    }

    private fun addListner() {
        ivBack.setOnClickListener(this)
        tvNewUnit.setOnClickListener(this)
    }

    private fun setPaginationData() {
        rvUnit.addOnScrollListener(object :
            PaginationScrollListener(rvUnit.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                if (currentPage < lastPage) {
                    currentPage += 1
                    isloading = true
                    mNetworkCallUnitAPI(currentPage)
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
            R.id.tvNewUnit -> {
                val intent = Intent(mContext, AddEditUnitActivity::class.java)
                intent.putExtra(Constants.mFrom, Constants.isAdd)
                startActivityForResult(intent, 201)
            }
        }
    }


    private fun mNetworkCallUnitAPI(newcurrentPage: Int) {
        if (newcurrentPage > 1) {
            offset = (newcurrentPage - 1) * 10
            pbBottomLoadData.visibility = View.VISIBLE
        }
        if (newcurrentPage == 1) {
            offset = (newcurrentPage - 1) * 10
            pbLoadData.visibility = View.VISIBLE
            unitArrayList.clear()
        }
        val call = apiInterface.getUnits(
            "id", "",offset,10
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    pbLoadData.visibility = View.GONE
                    pbBottomLoadData.visibility=View.GONE
                    if (response.code() == 200) {
                        val jsonObject = JSONObject(response.body().toString())
                        if (jsonObject.optBoolean("status")) {
                            isloading = false
                            val data = jsonObject.optJSONArray("data")
                            unitArrayList.addAll(
                                Gson().fromJson(
                                    data.toString(),
                                    object : TypeToken<java.util.ArrayList<UnitModel?>?>() {}.type
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



                            if (unitArrayList.size > 0) {
                                rvUnit.visibility = View.VISIBLE
                                tvNoUnit.visibility = View.GONE
                                setAdapter(unitArrayList)

                            } else {
                                if (currentPage==1){
                                    rvUnit.visibility = View.GONE
                                    tvNoUnit.visibility = View.VISIBLE
                                }

                            }

                        } else {
                            if (currentPage==1){
                                rvUnit.visibility = View.GONE
                                tvNoUnit.visibility = View.VISIBLE
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 201) {
            if (resultCode == Activity.RESULT_OK) {
                currentPage=1
                mNetworkCallUnitAPI(currentPage)
            }
        }
    }


    private fun setAdapter(unitArrayList: ArrayList<UnitModel>) {
        rvUnit.layoutManager = LinearLayoutManager(mContext)
        rvUnit.setHasFixedSize(true)
        unitAdapter = UnitAdapter(mContext, unitArrayList, this)
        rvUnit.adapter = unitAdapter
        unitAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(unitModel: UnitModel, position: Int) {
        startActivityForResult(
            Intent(mContext, AddEditUnitActivity::class.java)
                .putExtra(Constants.mFrom, Constants.isEdit)
                .putExtra(Constants.unitModel, Gson().toJson(unitModel)), 201
        )
    }

    override fun onDeleteClick(unitModel: UnitModel, position: Int) {
        openUnitDialog(unitModel,position)
    }


    private fun openUnitDialog(unitModel: UnitModel, position: Int) {
        val builder = AlertDialog.Builder(mContext)
        builder.setMessage("Are you sure you want to Delete Unit?")
        builder.setPositiveButton("YES") { dialog, which ->
            if (InternetConnection.checkConnection(mContext)) {
                deleteUnit(dialog, unitModel, position)
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

    private fun deleteUnit(
        dialog: DialogInterface,
        unitModel: UnitModel,
        position: Int
    ) {
        pbLoadData.visibility = View.VISIBLE
        val call = apiInterface.deleteUnits(unitModel.xid)
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
                            if (unitArrayList.size > 0) {
                                unitArrayList.removeAt(position)
                                currentPage=1
                                mNetworkCallUnitAPI(currentPage)
                            } else {
                                rvUnit.visibility = View.GONE
                                tvNoUnit.visibility = View.VISIBLE
                            }
                        } else {
                            rvUnit.visibility = View.GONE
                            tvNoUnit.visibility = View.VISIBLE
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